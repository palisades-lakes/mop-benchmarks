@echo off
:: palisades.lakes (at) gmail (dot) com
:: 2026-05-04

set THRUPUT=-server -Xbatch -XX:+UseFMA

::set XMX=-Xms29g -Xmx29g -Xmn11g
::set XMX=-Xms12g -Xmx12g -Xmn5g
set XMX=

set OPENS=--add-opens java.base/java.lang=ALL-UNNAMED --enable-native-access=ALL-UNNAMED
set ALLOW=--sun-misc-unsafe-memory-access=allow --illegal-native-access=allow
set CP=-cp target\benchmarks.jar

::set JAVA_HOME=%JAVA26%
set JAVA="%JAVA_HOME%\bin\java"

set CMD=%JAVA% %THRUPUT% -ea -dsa %XMX% %OPENS% %ALLOW% %CP% %*
echo %CMD%
%CMD%
