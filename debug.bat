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
Start java Server
Start java Server
Start java Server
timeout /t 5 /nobreak  >nul

rem z.B. 5 Client
Start java Client
Start java Client
Start java Client
Start java Client
Start java Client

rem pause