# BrickScan

Android-App zum Scannen von LEGO-Minifiguren und Teilen.

- **Erkennen** mit [Brickognize](https://brickognize.com) (kostenlos, ohne Schlüssel)
- **Prüfen**: Du siehst die Treffer mit Bild und Trefferquote und tippst den richtigen an
- **Liste** in zwei Tabs (Minifigs / Teile) mit Bild, Zustand, Anzahl und **Ø-Verkaufspreis von BrickLink** (letzte 6 Monate)
- **Gesamtwert** oben, plus Summe pro Tab

## 1. APK bauen lassen (einmalig, ca. 5 Minuten)

1. Diesen Ordner als neues Repository auf GitHub hochladen.
2. Im Repository auf **Actions** gehen. Der Bau startet von selbst (grüner Haken = fertig).
3. Unter **Releases** liegt danach `BrickScan.apk`.
   Fester Link für die neueste Version: `https://github.com/DEIN-NAME/DEIN-REPO/releases/latest/download/BrickScan.apk`
4. Am Handy öffnen, herunterladen, antippen. Beim ersten Mal fragt Android, ob du „Apps aus dieser Quelle“ erlauben willst: einmal erlauben.

Jede Änderung im Repository baut automatisch eine neue Version. Sie installiert sich über die alte, deine Liste bleibt erhalten.

## 2. Preise

**Ohne Schlüssel (Standard):** Nach jedem Scan öffnet sich oben in der App ein kleines Fenster mit der BrickLink-Preisseite des Teils. Darunter tippst du den Ø-Preis ein. Die App zeigt die Seite nur an und liest nichts automatisch aus, das wäre gegen die BrickLink-Regeln. Zeigt BrickLink Dollar statt Euro an, meldest du dich im Fenster einmal bei BrickLink an.

**Mit Schlüssel (optional, automatisch):**

Die BrickLink-API gibt es nur für **Verkäufer-Konten**. Du musst aber nichts verkaufen, der Laden kann geschlossen bleiben.

1. Auf bricklink.com anmelden und unter „Sell“ auf **Upgrade to Seller** gehen. Dafür braucht dein Konto mindestens eine positive Bewertung, also mindestens einen abgeschlossenen Kauf.
2. https://www.bricklink.com/v2/api/register_consumer.page öffnen.
3. Einen Zugang anlegen, als **IP-Adresse 0.0.0.0** und **IP-Maske 0.0.0.0** eintragen. Nur so funktioniert er am Handy, egal in welchem Netz.
4. In der App über das Zahnrad die vier Werte eintragen: Consumer Key, Consumer Secret, Token Value, Token Secret. Dann **Verbindung testen**.

Die Schlüssel bleiben nur auf deinem Handy. Gib sie nicht weiter: Mit ihnen hat man Zugriff auf dein BrickLink-Konto.

## Gut zu wissen

- **Teile und Farben:** Brickognize erkennt die Form, nicht die Farbe. Nach dem Scan wählst du die Farbe aus den Farben, die es für dieses Teil gibt. Ohne Farbe gilt der Durchschnitt über alle Farben.
- **Preisquelle (mit Schlüssel):** Gibt es keine Verkäufe in den letzten 6 Monaten, nimmt die App den Ø-Preis der aktuellen Angebote („Ø Angebote“). Du kannst jeden Preis auch selbst setzen („manuell“).
- **Fotos:** Am besten ein Teil pro Foto, auf hellem, ruhigem Untergrund.
- **Testen im Browser:** `www/index.html` mit `?mock` am Ende öffnen (`?mock&nokey` für den Modus ohne Schlüssel). Dann wird nichts echt erkannt, und die Preise sind Zufallswerte.

## Aufbau

- `www/index.html`: die komplette App (HTML/CSS/JS)
- `android/`: Android-Projekt ([Capacitor](https://capacitorjs.com) 8), darin `PriceViewPlugin.java` für das kleine BrickLink-Fenster
- `.github/workflows/apk.yml`: baut die APK auf GitHub
