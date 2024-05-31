# Anleitung für den Mandelbrot-Server

## Automatisch ausführen

Um den Mandelbrot-Server im Debug-Modus auszuführen, führen Sie das `debug.bat`-Skript aus:

1. Öffnen Sie ein Befehlsfenster oder Terminal.
2. Navigieren Sie zum Verzeichnis, das `debug.bat` enthält.
3. Führen Sie das Skript aus:

```bash
debug.bat
```

## Manuell ausführen
### Starten des Lastenausgleichs

Der Lastenausgleich verteilt Client-Anfragen auf mehrere Serverinstanzen. Führen Sie die folgenden Schritte aus, um den Lastenausgleich zu starten:

1. Öffnen Sie ein Befehlsfenster oder Terminal.
2. Navigieren Sie zum Verzeichnis, das `LoadBalancer.java` enthält.
3. Kompilieren Sie den Lastenausgleich (falls noch nicht kompiliert):

```bash
javac LoadBalancer.java
```

4. Starten Sie den Lastenausgleich:

```bash
java LoadBalancer
```

### Starten der Serverinstanzen

Sie können mehrere Serverinstanzen starten, um Client-Anfragen zu bearbeiten. Jeder Server wird auf einem anderen Port ausgeführt. Führen Sie die folgenden Schritte aus:

1. Öffnen Sie für jede zu startende Serverinstanz ein Befehlsfenster oder Terminal.
2. Navigieren Sie zum Verzeichnis, das `Server.java` enthält.
3. Kompilieren Sie den Server (falls noch nicht kompiliert):

```bash
javac Server.java
```

4. Starten Sie den Server:

```bash
java Server <LoadBalancer_IP>
```

Ersetzen Sie `<LoadBalancer_IP>` durch die tatsächliche IP-Adresse des Computers, auf dem der Lastenausgleich ausgeführt wird.

Wiederholen Sie die oben genannten Schritte, um weitere Serverinstanzen zu starten.

### Ausführen der Client-Anwendung

Um die Client-Anwendung auszuführen, die eine Verbindung zum Lastenausgleich herstellt und Mandelbrot-Set-Berechnungen anfordert, befolgen Sie diese Schritte:

1. Öffnen Sie für jede zu startende Clientinstanz ein Befehlsfenster oder Terminal.
2. Navigieren Sie zum Verzeichnis, das `Client.java` enthält.
3. Kompilieren Sie den Client (falls noch nicht kompiliert):

```bash
javac Client.java
```

4. Starten Sie den Client mit der IP-Adresse des Lastenausgleichs als Parameter:

```bash
java Client <LoadBalancer_IP>
```

Ersetzen Sie `<LoadBalancer_IP>` durch die tatsächliche IP-Adresse des Computers, auf dem der Lastenausgleich ausgeführt wird.

Wiederholen Sie die oben genannten Schritte, um weitere Clientinstanzen zu starten.

## Beispiel

Hier ist ein Beispiel, wie Sie das gesamte Setup starten:

1. Starten Sie den Lastenausgleich:

```bash
javac LoadBalancer.java
java LoadBalancer
```

2. Starten Sie mehrere Serverinstanzen und stellen Sie eine Verbindung zum Lastenausgleich unter `192.168.1.100` her:

```bash
javac Server.java
java Server 192.168.1.100
```

3. Starten Sie mehrere Clientinstanzen und stellen Sie eine Verbindung zum Lastenausgleich unter `192.168.1.100` her:

```bash
javac Client.java
java Client 192.168.1.100
```

## Weitere Informationen

- Sie können Java-Quelldateien (`LoadBalancer.java`, `Server.java` und `Client.java`) auch trennen. Allerdings muss man auf die angeschlossene Schnittstelle achten.
- Stellen Sie sicher, dass der Lastenausgleich und die Serverinstanzen ausgeführt werden, bevor Sie die Client-Anwendungen starten.
- Überprüfen Sie Ihre Firewall-Einstellungen, um sicherzustellen, dass die erforderlichen Ports geöffnet und zugänglich sind.
