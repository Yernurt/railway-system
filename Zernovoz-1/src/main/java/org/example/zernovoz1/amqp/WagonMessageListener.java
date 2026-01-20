package org.example.zernovoz1.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zernovoz1.models.LocomotiveModel;
import org.example.zernovoz1.models.WagonModel;
import org.example.zernovoz1.services.LocomotiveService;
import org.example.zernovoz1.services.SuspiciousCacheService;
import org.example.zernovoz1.services.WagonService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WagonMessageListener {

    private final WagonService wagonService;
    private final LocomotiveService locomotiveService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpMessagingTemplate messagingTemplate;
    private final SuspiciousCacheService suspiciousCacheService;

    @RabbitListener(queues = "wagon_queue", ackMode = "MANUAL")
    public void receiveWagonMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            Map<String, String> data = objectMapper.readValue(message, Map.class);
            String wagonNumber = data.get("wagon_number");
            String station = data.get("station");
            String lastUpdated = data.get("timestamp");
            String speedKmh = data.get("speed_kmh");
            String identificationStatus = data.get("status");
            String video = data.get("video_url");

            log.info("🚃 Вагон келген хабар: №{} | Станция: {} | {}", wagonNumber, station, identificationStatus);

            // 1. Вагон базаға тіркелген бе?
            Optional<WagonModel> optionalWagon = wagonService.getWagonByNumber(wagonNumber);
            if (optionalWagon.isEmpty()) {
                log.warn("❗️ Вагон №{} тіркелмеген. Кешке қосылды", wagonNumber);
                SuspiciousCacheService.SuspiciousData suspiciousData = new SuspiciousCacheService.SuspiciousData();
                suspiciousData.station = station;
                suspiciousData.lastUpdated = lastUpdated;
                suspiciousData.speedKmh = speedKmh;
                suspiciousData.identificationStatus = identificationStatus;
                suspiciousData.video = video;

                suspiciousCacheService.put(wagonNumber, suspiciousData);

                messagingTemplate.convertAndSend("/topic/manualCheck", Map.of(
                        "wagonNumber", wagonNumber,
                        "video", video,
                        "reason", "Базаға тіркелмеген вагон"
                ));
                channel.basicAck(tag, false);
                return;
            }

            WagonModel wagon = optionalWagon.get();

            // 2. Ең соң анықталған локомотивті іздейміз
            Optional<LocomotiveModel> latestLoco = locomotiveService.getMostRecentlyUpdatedLocomotiveAtStation(station);
            if (latestLoco.isPresent()) {
                wagon.setLocomotive(latestLoco.get());
                log.info("🔗 Вагон №{} → Локомотивке байланды: {}", wagonNumber, latestLoco.get().getLocomotiveNumber());
            } else {
                log.warn("⚠️ Сәйкес локомотив табылмады. Байланыс жасалмады.");
            }

            // 3. Қалған деректерді жаңартамыз
            wagon.setStation(station);
            wagon.setLastUpdated(lastUpdated);
            wagon.setSpeedKmh(speedKmh);
            wagon.setIdentificationStatus(identificationStatus);
            wagon.setVideo(video);

            WagonModel saved = wagonService.saveWagon(wagon);

            // 4. Веб-сокет арқылы жіберу
            messagingTemplate.convertAndSend("/topic/wagonUpdates", saved);

            channel.basicAck(tag, false);

        } catch (IOException e) {
            log.error("❌ Вагон хабарламасын оқу қатесі: {}", e.getMessage());
        }
    }
}
