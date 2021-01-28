@echo off
IF "%~1" == "" (xaridar.lscript-1.0.0 %CD%) ELSE (xaridar.lscript-1.0.0 %CD%\%1)
