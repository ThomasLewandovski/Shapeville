@echo off
set JAVA_FX_PATH=lib
set FX_MODULES=javafx.controls,javafx.fxml

:: 编译所有源代码（包括子文件夹）
echo Compiling...
javac --module-path %JAVA_FX_PATH% --add-modules %FX_MODULES% -d out src/com/shapeville/**/*.java

:: 运行程序
echo Running...
java --module-path %JAVA_FX_PATH% --add-modules %FX_MODULES% -cp out com.shapeville.ui.Main
pause