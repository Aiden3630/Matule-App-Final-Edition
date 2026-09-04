@echo off
echo [INFO] Начинаю копирование базы данных с устройства...

:: Путь к папке на телефоне
set PHONE_PATH=/storage/emulated/0/Android/data/com.aiden3630.chempionat/files/

:: Путь к папке в твоем проекте (модуль data)
set PC_PATH=./data/src/main/java/com/aiden3630/data/

:: Команда копирования через ADB
adb pull %PHONE_PATH%shop_products.json %PC_PATH%
adb pull %PHONE_PATH%matule_users_db.json %PC_PATH%

echo [SUCCESS] Файлы успешно скопированы в папку проекта (модуль DATA)!
pause

//.\SYNC_DATABASE.bat