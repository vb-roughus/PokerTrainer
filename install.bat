@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

rem ===========================================================================
rem  PokerTrainer - Installation auf angeschlossenem Android-Smartphone
rem  Doppelklick genuegt. Baut die App und installiert sie per USB aufs Telefon.
rem ===========================================================================

rem In den Ordner dieses Skripts wechseln (auch bei Doppelklick korrekt)
cd /d "%~dp0"

echo.
echo ===========================================================
echo   PokerTrainer wird auf dein Smartphone installiert
echo ===========================================================
echo.

rem --- ADB suchen (zum Pruefen, ob ein Telefon verbunden ist) -----------------
set "ADB="
where adb >nul 2>nul && set "ADB=adb"
if not defined ADB if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
if not defined ADB if defined ANDROID_HOME if exist "%ANDROID_HOME%\platform-tools\adb.exe" set "ADB=%ANDROID_HOME%\platform-tools\adb.exe"
if not defined ADB if defined ANDROID_SDK_ROOT if exist "%ANDROID_SDK_ROOT%\platform-tools\adb.exe" set "ADB=%ANDROID_SDK_ROOT%\platform-tools\adb.exe"

rem --- Geraete-Check (nur wenn adb gefunden wurde) ---------------------------
if defined ADB (
    echo [1/2] Pruefe, ob ein Telefon verbunden ist...
    "%ADB%" start-server >nul 2>nul

    set "DEVICE_COUNT=0"
    set "UNAUTH=0"
    for /f "skip=1 tokens=1,2" %%a in ('"%ADB%" devices') do (
        if "%%b"=="device" set /a DEVICE_COUNT+=1
        if "%%b"=="unauthorized" set /a UNAUTH+=1
    )

    if !UNAUTH! gtr 0 (
        echo.
        echo   [!] Auf dem Telefon erscheint ein Dialog "USB-Debugging zulassen?".
        echo       Bitte mit "Zulassen" / "OK" bestaetigen und Skript neu starten.
        echo.
        goto :fail
    )

    if !DEVICE_COUNT! equ 0 (
        echo.
        echo   [!] Kein Telefon erkannt. Bitte pruefen:
        echo       1. Telefon per USB-Kabel verbunden ^(Datenkabel, kein reines Ladekabel^)
        echo       2. USB-Debugging aktiviert
        echo          ^(Einstellungen ^> Entwickleroptionen ^> USB-Debugging^)
        echo       3. Den Dialog "USB-Debugging zulassen?" am Telefon bestaetigen
        echo.
        echo       Details siehe INSTALL.md
        echo.
        goto :fail
    )

    echo       OK - !DEVICE_COUNT! Geraet^(e^) gefunden.
) else (
    echo [1/2] Hinweis: adb nicht gefunden - ueberspringe Geraete-Check.
    echo       Gradle versucht die Installation direkt ueber das Android SDK.
)

rem --- Android SDK suchen und local.properties anlegen (falls noetig) --------
if not exist "local.properties" (
    set "SDK="
    if defined ANDROID_HOME if exist "%ANDROID_HOME%\platform-tools" set "SDK=%ANDROID_HOME%"
    if not defined SDK if defined ANDROID_SDK_ROOT if exist "%ANDROID_SDK_ROOT%\platform-tools" set "SDK=%ANDROID_SDK_ROOT%"
    if not defined SDK if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools" set "SDK=%LOCALAPPDATA%\Android\Sdk"
    if not defined SDK if exist "%USERPROFILE%\AppData\Local\Android\Sdk\platform-tools" set "SDK=%USERPROFILE%\AppData\Local\Android\Sdk"
    if not defined SDK if exist "C:\Android\Sdk\platform-tools" set "SDK=C:\Android\Sdk"

    if not defined SDK (
        echo.
        echo   [!] Android SDK nicht automatisch gefunden.
        echo       Den Pfad findest du in Android Studio unter
        echo       Settings ^> Languages ^& Frameworks ^> Android SDK ^("Android SDK Location"^).
        echo.
        set /p "SDK=   Bitte den SDK-Pfad hier einfuegen und Enter druecken: "
    )

    if defined SDK (
        set "SDKFWD=!SDK:\=/!"
        > "local.properties" echo sdk.dir=!SDKFWD!
        echo       local.properties angelegt mit: !SDK!
    ) else (
        echo   Kein Pfad angegeben - Abbruch. Siehe INSTALL.md
        goto :fail
    )
)

echo.
echo [2/2] App wird gebaut und installiert ^(beim ersten Mal dauert das einige Minuten^)...
echo.

call gradlew.bat installDebug
set "RESULT=%ERRORLEVEL%"

echo.
if "%RESULT%"=="0" (
    echo ===========================================================
    echo   FERTIG! Die App "PokerTrainer" ist auf deinem Telefon.
    echo   Oeffne sie im App-Menue ^(Icon "PokerTrainer"^).
    echo ===========================================================
) else (
    echo ===========================================================
    echo   [Fehler] Die Installation ist fehlgeschlagen.
    echo   Siehe die Meldungen oben. Haeufige Ursachen:
    echo     - JDK 17+ nicht installiert ^(Android Studio bringt es mit^)
    echo     - Android SDK-Pfad unbekannt ^(Datei local.properties oder
    echo       Umgebungsvariable ANDROID_HOME setzen^)
    echo   Mehr dazu in INSTALL.md
    echo ===========================================================
)

echo.
pause
exit /b %RESULT%

:fail
echo.
pause
exit /b 1
