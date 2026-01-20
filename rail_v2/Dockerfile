FROM pytorch/pytorch:2.0.1-cuda11.7-cudnn8-devel

WORKDIR /app

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y \
    tesseract-ocr \
    libglib2.0-0 libsm6 libxext6 libxrender-dev ffmpeg \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
# model файлын контейнер ішіне көшіру
COPY runs/detect/wagon_number_model/weights/best.pt /app/runs/detect/wagon_number_model/weights/best.pt


RUN pip install --upgrade pip && \
    pip install torch==2.0.1+cu117 torchvision==0.15.2+cu117 \
      -f https://download.pytorch.org/whl/cu117/torch_stable.html && \
    pip install --no-cache-dir -r requirements.txt
COPY . .

CMD ["python", "main.py"]
