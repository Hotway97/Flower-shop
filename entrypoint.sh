#!/bin/sh

ollama serve &

sleep 5

# Проверяем наличие модели и загружаем при необходимости
if ! ollama list | grep -q "deepseek-r1:7b"; then
  echo "Загружаем модель deepseek-r1:7b..."
  ollama run deepseek-r1:7b
fi

wait