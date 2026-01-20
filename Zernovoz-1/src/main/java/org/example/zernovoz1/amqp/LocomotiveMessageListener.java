package org.example.zernovoz1.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zernovoz1.services.CompositionCacheService;
import org.example.zernovoz1.models.LocomotiveModel;
import org.example.zernovoz1.models.WagonModel;
import org.example.zernovoz1.services.LocomotiveService;
import org.example.zernovoz1.services.WagonService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocomotiveMessageListener {

    private final LocomotiveService locomotiveService;
    private final WagonService wagonService;
    private final CompositionCacheService compositionCacheService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "locomotive_queue", ackMode = "MANUAL")
    public void receiveLocomotiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, Map.class);

            String locomotiveNumber = String.valueOf(data.get("locomotive_number"));
            String stationLocomotive = String.valueOf(data.get("station"));
            String lastUpdatedLocomotive = String.valueOf(data.get("timestamp"));
            String speedKmhLocomotive = String.valueOf(data.get("speed_kmh"));
            String identificationStatusLocomotive = String.valueOf(data.get("status"));
            String videoLocomotive = String.valueOf(data.get("video_url"));

            log.info("🚂 Локомотив келді: №{} | Станция: {} | Уақыты: {}", locomotiveNumber, stationLocomotive, lastUpdatedLocomotive);

            // 🔍 Базадан іздеу
            LocomotiveModel loco = locomotiveService.getLocomotiveByNumber(locomotiveNumber)
                    .orElseThrow(() -> new RuntimeException("Локомотив базаға тіркелмеген"));

            // 🔄 Жаңарту
            loco.setStationLocomotive(stationLocomotive);
            loco.setLastUpdatedLocomotive(lastUpdatedLocomotive);
            loco.setSpeedKmhLocomotive(speedKmhLocomotive);
            loco.setIdentificationStatusLocomotive(identificationStatusLocomotive);
            loco.setVideoLocomotive(videoLocomotive);
            locomotiveService.saveLocomotive(loco);

            // 📦 Станция бойынша барлық вагондарды алу
            if (compositionCacheService.hasWagons(stationLocomotive)) {
                List<WagonModel> wagons = compositionCacheService.getWagonsByStation(stationLocomotive);
                for (WagonModel wagon : wagons) {
                    wagon.setLocomotive(loco);
                    wagonService.saveWagon(wagon);
                }

                log.info("📦 {} вагон локомотивке байланыстырылды: №{}", wagons.size(), locomotiveNumber);

                // Кэшті тазалау
                compositionCacheService.clearStation(stationLocomotive);

                // 🛰 WebSocket хабарлама
                messagingTemplate.convertAndSend("/topic/compositions", Map.of(
                        "locomotiveNumber", locomotiveNumber,
                        "stationLocomotive", stationLocomotive,
                        "wagonCount", wagons.size(),
                        "time", LocalDateTime.now().toString()
                ));
            }

            channel.basicAck(tag, false);

        } catch (IOException e) {
            log.error("❌ Локомотив хабарламасын оқу қатесі: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Жалпы қате: {}", e.getMessage());
        }
    }
}
