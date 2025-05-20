@echo off
echo Compiling...

setlocal enabledelayedexpansion
set CP=.;lib\*

if not exist bin mkdir bin
del /s /q bin\*.class >nul 2>&1

for /r src %%f in (*.java) do (
    javac -d bin -cp "!CP!" "%%f"
)

echo Running...
java -cp "bin;lib\*" com.shapeville.ui.Main
pause