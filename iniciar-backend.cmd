@echo off
setlocal
title GameHub - Iniciar backend
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\local.ps1"
set "resultado=%errorlevel%"
if /i not "%~1"=="--sin-pausa" pause
exit /b %resultado%
