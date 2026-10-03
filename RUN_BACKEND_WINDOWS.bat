@echo off
setlocal
cd /d "%~dp0"
echo Cleaning and starting QCollect backend...
call mvnw.cmd clean spring-boot:run
if errorlevel 1 (
  echo.
  echo Backend failed to start. Review the error above.
  pause
)
endlocal
