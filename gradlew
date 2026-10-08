#!/usr/bin/env bash
#
# Gradle wrapper script - Unix/Linux/Mac
#
export JAVACOPTS="-Dfile.encoding=UTF-8"
export GRADLE_OPTS="-Dorg.gradle.daemon=false"
APP_HOME=$(cd "${0:-}" && pwd) || exit 1
APP_HOME=$(cd "$APP_HOME/.." && pwd)
DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"
GRADLE_USER_HOME=${GRADLE_USER_HOME:-"$HOME/.gradle"}
CLASSPATH="C:\Users\zabdi\AppData\Local\Temp\gradle-8.5\lib\gradle-launcher-8.5.jar"
exec java -classpath "$CLASSPATH" org.gradle.launcher.GradleWrapperMain "$@"
