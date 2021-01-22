@echo off
IF "%~1" == "" (lscript-1.0.0) ELSE (lscript-1.0.0 %CD%\%1)