@echo off
rem Copy cac dong nay vao file setenv.bat cua Tomcat hoac import vao moi truong truoc khi start server.

rem =========================
rem Gmail SMTP
rem =========================
set "AUTOPRO_SMTP_HOST=smtp.gmail.com"
set "AUTOPRO_SMTP_PORT=587"
set "AUTOPRO_SMTP_USERNAME=your_gmail@gmail.com"
set "AUTOPRO_SMTP_PASSWORD=your_gmail_app_password"
set "AUTOPRO_SMTP_FROM=your_gmail@gmail.com"
set "AUTOPRO_SMTP_FROM_NAME=AutoPro"
set "AUTOPRO_SMTP_STARTTLS=true"
set "AUTOPRO_SMTP_SSL=false"

rem =========================
rem SMS gateway: Twilio
rem =========================
set "AUTOPRO_SMS_PROVIDER=twilio"
set "AUTOPRO_SMS_DEFAULT_COUNTRY_CODE=+84"
set "AUTOPRO_TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
set "AUTOPRO_TWILIO_AUTH_TOKEN=your_twilio_auth_token"
rem Neu dung API Key / Secret thi dung 2 bien duoi day thay cho AUTH_TOKEN
rem set "AUTOPRO_TWILIO_AUTH_USERNAME=SKxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
rem set "AUTOPRO_TWILIO_AUTH_PASSWORD=your_twilio_api_secret"
set "AUTOPRO_TWILIO_FROM=+1xxxxxxxxxx"
rem Hoac dung Messaging Service SID thay cho so gui
rem set "AUTOPRO_TWILIO_MESSAGING_SERVICE_SID=MGxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
