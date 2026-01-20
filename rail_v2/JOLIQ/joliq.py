

import cv2
import pytesseract
import asyncio
import time
from datetime import datetime, timedelta
from ultralytics import YOLO
import re
import os
import tempfile
from JOLIQ.amqp import send_to_rabbitmq
import logging
import platform
from telegram_uploader import upload_video  # ✅ Telegram uploader

# Логгер
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    handlers=[logging.StreamHandler()]
)
logger = logging.getLogger(__name__)

# Windows үшін Tesseract жолы
if platform.system() == "Windows":
    pytesseract.pytesseract.tesseract_cmd = r"C:\\Program Files\\Tesseract-OCR\\tesseract.exe"

# Константалар
WAGON_LENGTH_METERS = 14
FPS = 60
fourcc = cv2.VideoWriter_fourcc(*'mp4v')
os.makedirs("videos", exist_ok=True)  # Видеолар папкасы

# Жылдамдықты есептеу
def calculate_speed(start_frame, end_frame):
    frames = end_frame - start_frame
    return round((WAGON_LENGTH_METERS * FPS * 3.6) / frames, 2) if frames > 0 else 0

# Нөмірді классификациялау
def classify_number(text):
    number = re.sub(r'\D', '', text)
    if len(number) == 8:
        return "wagon", number
    elif 4 <= len(number) <= 6:
        return "locomotive", number
    return None, None

# Кадр көрсету (қаласаң қосуға болады)
def show_frame(frame): pass
# def show_frame(frame):
#     cv2.imshow("Камера кадры", frame)
#     if cv2.waitKey(1) & 0xFF == ord('q'):
#         cv2.destroyAllWindows()
#         exit(0)

# Основной процесс
async def process_camera_stream(camera_url, station_name):
    cap = cv2.VideoCapture(str(camera_url))
    if not cap.isOpened():
        logger.error(f"❌ Камера қосылмады: {camera_url}")
        return

    model = YOLO("runs/detect/wagon_number_model/weights/best.pt")
    recording, video_writer, temp_video = False, None, None
    last_detected_time, frame_count, start_frame = None, 0, None
    detected_numbers = {}

    while True:
        ret, frame = await asyncio.to_thread(cap.read)
        if not ret:
            logger.warning(f"⚠️ Кадр оқылмады: {camera_url}")
            break

        frame_count += 1
        await asyncio.to_thread(show_frame, frame)

        now = datetime.now()
        detected_numbers = {
            number: ts for number, ts in detected_numbers.items()
            if (now - ts) < timedelta(seconds=30)
        }

        results = model(frame, verbose=False)

        for result in results:
            for box, conf in zip(result.boxes.xyxy.cpu().numpy(), result.boxes.conf.cpu().numpy()):
                x1, y1, x2, y2 = map(int, box)
                crop_img = frame[y1:y2, x1:x2]
                ocr_text = pytesseract.image_to_string(crop_img, config="--psm 7 digits")
                obj_type, number = classify_number(ocr_text)

                if number and number not in detected_numbers:
                    detected_numbers[number] = now
                    avg_conf = (conf + 0.85) / 2
                    status = "жасыл" if avg_conf >= 0.8 else "сары" if avg_conf >= 0.6 else "қызыл"
                    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

                    logger.info(f"🚆 {obj_type.upper()}: {number} | {status} | Сенімділік: {avg_conf:.2f}")

                    # ✅ Crop сақтау
                    save_crop_path = os.path.join("videos", f"{station_name}_{number}_{timestamp.replace(' ', '_').replace(':', '-')}_CROP.jpg")
                    cv2.imwrite(save_crop_path, crop_img)
                    logger.info(f"🖼️ Нөмір аймағының суреті сақталды: {save_crop_path}")

                    # ✅ Bounding Box салу
                    color = (0, 255, 0) if status == "жасыл" else (0, 255, 255) if status == "сары" else (0, 0, 255)
                    cv2.rectangle(frame, (x1, y1), (x2, y2), color, 2)
                    cv2.putText(frame, f"{obj_type.upper()}: {number} ({status})", (x1, y1 - 10),
                                cv2.FONT_HERSHEY_SIMPLEX, 0.7, color, 2)

                    # ✅ Frame сақтау
                    save_frame_path = os.path.join("videos", f"{station_name}_{number}_{timestamp.replace(' ', '_').replace(':', '-')}_FRAME.jpg")
                    cv2.imwrite(save_frame_path, frame)
                    logger.info(f"🖼️ Frame суреті Bounding Box-пен сақталды: {save_frame_path}")

                    # ✅ Видео жазуды бастау
                    if not recording:
                        temp_video = tempfile.NamedTemporaryFile(delete=False, suffix=".mp4", dir="videos")
                        video_writer = cv2.VideoWriter(temp_video.name, fourcc, FPS, (frame.shape[1], frame.shape[0]))
                        recording = True
                        start_frame = frame_count
                        logger.info(f"🎥 Видео жазу басталды (уақытша файл): {temp_video.name}")

                    last_detected_time = time.time()
                    speed = calculate_speed(start_frame, frame_count)

                    # ✅ Временно RabbitMQ (Processing)
                    await send_to_rabbitmq(
                        locomotive_number=str(number) if obj_type == "locomotive" else None,
                        wagon_number=str(number) if obj_type == "wagon" else None,
                        station=str(station_name),
                        speed=str(speed),
                        status=str(status),
                        timestamp=str(timestamp),
                        video_url="Processing... uploading to Telegram"
                    )

        # Видео жазу аяқтау
        if recording:
            video_writer.write(frame)
            if time.time() - last_detected_time > 10:
                video_writer.release()
                logger.info(f"⏹ Видео жазу тоқтады (уақытша файл): {temp_video.name}")
                recording = False

                # ✅ Telegram-ға жіберу
                telegram_url = upload_video(
                    temp_video.name,
                    title=f"{station_name} - {number} - {timestamp}"
                )
                logger.info(f"📤 Telegram URL: {telegram_url}")

                # ✅ RabbitMQ-ға Telegram URL жіберу
                await send_to_rabbitmq(
                    locomotive_number=str(number) if obj_type == "locomotive" else None,
                    wagon_number=str(number) if obj_type == "wagon" else None,
                    station=str(station_name),
                    speed=str(speed),
                    status=str(status),
                    timestamp=str(timestamp),
                    video_url=str(telegram_url)
                )

                # ✅ Уақытша файлды өшіру
                try:
                    os.remove(temp_video.name)
                    logger.info(f"🗑️ Уақытша файл өшірілді: {temp_video.name}")
                except Exception as e:
                    logger.error(f"❌ Уақытша файлды өшіру қатесі: {e}")

        await asyncio.sleep(0)

    cap.release()
    cv2.destroyAllWindows()
