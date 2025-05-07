#!/bin/bash

# Crear directorio para las capturas
mkdir -p screenshots

# Función para capturar pantalla
capture_screen() {
    local name=$1
    adb shell screencap -p /sdcard/screen_$name.png
    adb pull /sdcard/screen_$name.png screenshots/
    adb shell rm /sdcard/screen_$name.png
}

# Asegurarse que el dispositivo está conectado
if ! adb devices | grep -q "device$"; then
    echo "No hay dispositivos Android conectados"
    exit 1
fi

# Capturar pantalla de login
echo "Capturando pantalla de login..."
adb shell am start -n com.example.nutriknoledge/.LoginActivity
sleep 3
capture_screen "login"

# Capturar pantalla principal
echo "Capturando pantalla principal..."
adb shell am start -n com.example.nutriknoledge/.MainActivity
sleep 3
capture_screen "main"

# Capturar pantalla de menú
echo "Capturando pantalla de menú..."
adb shell am start -n com.example.nutriknoledge/.MenuActivity
sleep 3
capture_screen "menu"

# Capturar pantalla de configuración
echo "Capturando pantalla de configuración..."
adb shell am start -n com.example.nutriknoledge/.SettingsActivity
sleep 3
capture_screen "settings"

# Capturar pantalla de contenido
echo "Capturando pantalla de contenido..."
adb shell am start -n com.example.nutriknoledge/.ContentActivity
sleep 3
capture_screen "content"

# Capturar pantalla de glosario
echo "Capturando pantalla de glosario..."
adb shell am start -n com.example.nutriknoledge/.GlossaryActivity
sleep 5  # Aumentamos el tiempo de espera para asegurar que la pantalla se cargue
capture_screen "glossary"

# Capturar pantalla de destacados
echo "Capturando pantalla de destacados..."
adb shell am start -n com.example.nutriknoledge/.HighlightsActivity
sleep 3
capture_screen "highlights"

echo "¡Capturas completadas! Las imágenes se encuentran en el directorio 'screenshots'" 