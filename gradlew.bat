@echo off
setlocal
set GRADLE_USER_HOME=C:\Users\zabdi\.gradle
set DEFAULT_JVM_OPTS=-Xmx64m -Xms64m
set CLASSPATH=C:\Users\zabdi\AppData\Local\Temp\gradle-8.5\lib\gradle-launcher-8.5.jar
java -classpath "%CLASSPATH%" org.gradle.launcher.GradleWrapperMain %*
