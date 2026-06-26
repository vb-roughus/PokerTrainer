# PokerTrainer auf dem Smartphone installieren (Windows)

So bekommst du die App per USB direkt auf dein Android-Handy – ohne Android
Studio öffnen zu müssen.

## Kurzfassung

1. Telefon per USB-Kabel an den PC anschließen
2. USB-Debugging am Telefon aktivieren (siehe unten)
3. **`install.bat` doppelklicken**
4. Warten, bis „FERTIG!" erscheint – die App liegt dann im App-Menü

---

## Einmalige Vorbereitung

### 1. USB-Debugging am Telefon aktivieren

1. **Einstellungen → Über das Telefon**
2. 7× hintereinander auf **Build-Nummer** tippen
   (Meldung „Du bist jetzt Entwickler" erscheint)
3. Zurück → **System → Entwickleroptionen**
4. **USB-Debugging** einschalten
5. Telefon per USB anschließen → am Telefon erscheint
   **„USB-Debugging zulassen?"** → auf **Zulassen** tippen
   (Häkchen „Immer von diesem Computer zulassen" setzen)

> Hinweis: Bei manchen Herstellern heißt der Menüpunkt leicht anders
> (z. B. Samsung: Einstellungen → Telefoninfo → Softwareinformationen →
> Build-Nummer).

### 2. Voraussetzungen auf dem PC

Das Skript baut die App mit Gradle. Dafür wird benötigt:

- **JDK 17 oder neuer** – ist bereits enthalten, wenn **Android Studio**
  installiert ist. Andernfalls Temurin/OpenJDK 17 installieren.
- **Android SDK** – ebenfalls Teil von Android Studio. Falls Gradle das SDK
  nicht findet, lege im Projektordner eine Datei **`local.properties`** an mit:
  ```
  sdk.dir=C:\\Users\\DEINNAME\\AppData\\Local\\Android\\Sdk
  ```
  (oder setze die Umgebungsvariable `ANDROID_HOME` auf diesen Pfad)

> Der erste Lauf lädt Gradle 8.9 herunter und kann einige Minuten dauern.
> Danach geht es deutlich schneller.

---

## Installieren

Doppelklick auf **`install.bat`**. Das Skript:

1. prüft, ob ein Telefon verbunden ist,
2. baut die App und
3. installiert sie auf dem Telefon.

Bei Erfolg erscheint „**FERTIG!**". Öffne die App im App-Menü
(Icon **PokerTrainer**).

---

## Wenn etwas nicht klappt

| Meldung | Lösung |
|---|---|
| „Kein Telefon erkannt" | Kabel prüfen (Datenkabel!), USB-Debugging an, Dialog am Telefon bestätigen |
| „USB-Debugging zulassen?" bleibt offen | Am Telefon auf „Zulassen" tippen, dann Skript neu starten |
| Build-Fehler, JDK | Android Studio installieren oder JDK 17 einrichten |
| SDK nicht gefunden | `local.properties` mit `sdk.dir=...` anlegen (siehe oben) |

Die App wird als **Debug-Version** installiert – ideal zum Ausprobieren,
keine Anmeldung oder Store nötig.
