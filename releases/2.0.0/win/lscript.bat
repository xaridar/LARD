@echo off
IF "%~1" == "" (lscript-2.0.0 %CD%) ELSE (lscript-2.0.0 %CD%\%1)
