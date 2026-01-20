import asyncio
from aiohttp import web
import os
from JOLIQ.joliq import process_camera_stream

# 📁 Видеолар папкасы
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
VIDEOS_DIR = os.path.join(BASE_DIR, "videos")
os.makedirs(VIDEOS_DIR, exist_ok=True)

# 🎯 Локалды камералардың default станциялары
CAMERA_DEFAULTS = {
    "0": "Алматы",
    "1": "Астана",
    "2": "Шымкент",
    "3":"Ақтөбе"

    # Қосымша локал камералар керек болса, осында қос
}

# ✅ Камераны қосу API
async def start_camera(request):
    data = await request.json()
    camera_url = str(data.get("camera_url"))
    station = data.get("station")

    if not station:
        station = CAMERA_DEFAULTS.get(camera_url, "Белгісіз станция")

    asyncio.create_task(process_camera_stream(camera_url, station))
    return web.json_response({"status": f"Камера қосылды: {camera_url} | Станция: {station}"})

# ✅ Сервер орнату
app = web.Application()
app.router.add_post('/start_camera', start_camera)
app.router.add_static('/videos/', VIDEOS_DIR, name='videos')

if __name__ == "__main__":
    web.run_app(app, host="0.0.0.0", port=8080)
