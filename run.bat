@echo off
echo === Compiling Java source files ===

:: 启用变量延迟展开
setlocal enabledelayedexpansion

:: 设置 classpath，包括 lib 和资源目录
set CP=.;lib\*;resources

:: 确保 bin 目录存在
if not exist bin mkdir bin

:: 清空 bin 中旧的 class 文件
del /s /q bin\*.class >nul 2>&1

:: 编译所有 Java 文件
for /r src %%f in (*.java) do (
    echo Compiling %%f
    javac -encoding UTF-8 -d bin -cp "!CP!" "%%f"
)

echo === Compilation Finished ===

:: 复制资源到 bin（图片、Archive等）
xcopy /E /Y /I resources bin

echo === Running Shapeville Game ===
java -cp "bin;lib\*" com.shapeville.ui.Main
pause