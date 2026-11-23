@echo off
setlocal
title GameHub - Detener backend
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\local.ps1" -Action Stop
set "resultado=%errorlevel%"
if /i not "%~1"=="--sin-pausa" pause
exit /b %resultado%
