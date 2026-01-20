import requests

# Твой БОТ TOKEN
TELEGRAM_BOT_TOKEN = '8018687579:AAFaC6bsPz9rrhzntsmLb-hTyR3h0VOtW28'  # ← осы жерге ДҰРЫС токен қоясың!

# Каналдың username (мысалы @rail_system_channel)
TELEGRAM_CHANNEL = '@rail_system_channel'

def upload_video(video_path, title):
    url = f"https://api.telegram.org/bot{TELEGRAM_BOT_TOKEN}/sendVideo"
    files = {'video': open(video_path, 'rb')}
    data = {
        'chat_id': TELEGRAM_CHANNEL,
        'caption': title
    }
    response = requests.post(url, files=files, data=data)
    if response.status_code == 200:
        result = response.json()['result']
        message_id = result['message_id']
        telegram_url = f"https://t.me/{TELEGRAM_CHANNEL.lstrip('@')}/{message_id}"
        return telegram_url
    else:
        print(f"❌ Telegram жіберу қатесі: {response.text}")
        return "Failed to upload to Telegram"
