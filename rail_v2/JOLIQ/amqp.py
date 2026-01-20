import asyncio
import os
import aio_pika
import json
import logging

# 🔧 Логгерді баптау
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
handler = logging.StreamHandler()
formatter = logging.Formatter('%(asctime)s [%(levelname)s] %(message)s')
handler.setFormatter(formatter)
logger.addHandler(handler)

# 🛠️ RabbitMQ параметрлері
RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "amqp://guest:guest@rabbitmq:5672/")
EXCHANGE_NAME = "vehicle_exchange"

# 🔧 Exchange және Queue-ларды конфигурациялау
async def setup_rabbitmq():
    try:
        connection = await aio_pika.connect_robust(RABBITMQ_HOST)
        async with connection:
            channel = await connection.channel()

            exchange = await channel.declare_exchange(EXCHANGE_NAME, aio_pika.ExchangeType.TOPIC, durable=True)

            wagon_queue = await channel.declare_queue("wagon_queue", durable=True)
            loco_queue = await channel.declare_queue("locomotive_queue", durable=True)

            await wagon_queue.bind(exchange, routing_key="vehicle.wagon")
            await loco_queue.bind(exchange, routing_key="vehicle.locomotive")

            logger.info("✅ RabbitMQ дайын: exchange, queues, bindings")
    except Exception as e:
        logger.error(f"❌ RabbitMQ setup қатесі: {e}")

# 📤 Хабар жіберу функциясы
async def send_to_rabbitmq(locomotive_number, wagon_number, station, speed, status, timestamp, video_url):
    try:
        connection = await aio_pika.connect_robust(RABBITMQ_HOST)
        async with connection:
            channel = await connection.channel()
            exchange = await channel.declare_exchange(EXCHANGE_NAME, aio_pika.ExchangeType.TOPIC, durable=True)

            if locomotive_number and locomotive_number.strip().isdigit():
                routing_key = "vehicle.locomotive"
                message_body = {
                    "timestamp": timestamp,
                    "locomotive_number": locomotive_number,
                    "station": station,
                    "speed_kmh": speed,
                    "status": status,
                    "video_url": video_url or "null"
                }
            elif wagon_number and wagon_number.strip().isdigit():
                routing_key = "vehicle.wagon"
                message_body = {
                    "timestamp": timestamp,
                    "wagon_number": wagon_number,
                    "station": station,
                    "speed_kmh": speed,
                    "status": status,
                    "video_url": video_url or "null"
                }
            else:
                logger.warning("⚠️ Нөмір берілмеді, хабар жіберілмейді.")
                return

            message = aio_pika.Message(
                body=json.dumps(message_body).encode(),
                delivery_mode=aio_pika.DeliveryMode.PERSISTENT
            )

            await exchange.publish(message, routing_key=routing_key)
            logger.info(f"📡 [{routing_key}] жіберілді: {message_body}")

    except Exception as e:
        logger.error(f"❌ RabbitMQ жіберу қатесі: {e}")



