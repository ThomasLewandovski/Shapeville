@echo off
echo === Compiling Shapeville ===

:: 设置 classpath（lib 可选）
set CP=.;lib\*;resources

:: 创建 bin 目录
if not exist bin mkdir bin

:: 清空 bin
del /s /q bin\*.class >nul 2>&1

:: 编译：一次性编译所有 Java 文件（重点）
javac -encoding UTF-8 -cp "%CP%" -d bin @sources.txt

:: 复制资源（图片等）
xcopy /E /Y /I resources bin

echo === Running Game ===
java -cp "bin;lib\*" com.shapeville.ui.Main
pause