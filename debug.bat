@echo off
javac Client.java
javac Server.java
javac LoadBalancer.java

if %errorlevel% neq 0 (
    echo Error!
    pause
    exit /b
)

Start java LoadBalancer
timeout /t 5 /nobreak  >nul

rem z.B. 3 Server (Worker)
Start java Server localhost
Start java Server 127.0.0.1
Start java Server localhost
timeout /t 5 /nobreak  >nul

rem z.B. 5 Client
Start java Client localhost
Start java Client 127.0.0.1
Start java Client localhost
Start java Client 127.0.0.1
Start java Client localhost

rem pause