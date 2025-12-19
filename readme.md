# 📄 **README.md – SMP Plugin**

```markdown
# SMP – Advanced Minecraft Server Plugin

Ein umfangreiches und modular aufgebautes Paper 1.21.x Plugin für SMP-Server.  
Es bietet Admin-Tools, Qualitätsverbesserungen, Teleportsystem, Vanish, End-Steuerung, Elytra-Rocket-Kontrolle und vieles mehr.

---

## ✨ Features

### 🛠️ Admin-Menü
- `/admin`
- GUI für Serververwaltung
- Optionen für Teleport, God Mode, Spieler-Übersicht, Vanish, Admin-Item usw.
- Permissions fein granular steuerbar (`ouh.admin.*`)

### 👻 Vanish-System
- `/vanish`
- Unsichtbarkeit inkl. Night-Vision
- Vanish-Item im Admin-Menü
- Mit Tab-Completer

### 📖 First Join / Hilfe
- `/help`
- Gibt ein Willkommens-/Info-Buch
- Individuelle Join-Events für neue Spieler

### 💬 Nachrichten-System
- Eigener Prefix  
- Custom Death Messages  
- System-Nachrichten  
- Join-/Leave-Handling

### 🔁 Teleport-System
- `/tpa` – Anfrage senden  
- `/tpahere` – Spieler zu dir bitten  
- `/tpaccept` – Anfrage annehmen  
- `/tpdeny` – Anfrage ablehnen  
- Mit Timeout, Actionbar-Nachrichten & Tab-Completer  
- Optional reduzierter Cooldown per Permission

### 🎆 Elytra Rocket Control
- `/rockets` – Rockets global aktivieren/deaktivieren
- Wenn deaktiviert:
  - Spieler können keine Elytra-Raketen zünden
  - 5-Sekunden-Cooldown für die Systemnachricht
- Zustand wird automatisch in `config.yml` gespeichert

### 🟣 End Welt Steuerung
- `/openend` – End öffnen oder schließen
- Wenn das End geschlossen ist:
  - Kein Teleport durch End-Portale
  - Kein Wechsel in die End-Dimension
- Zustand wird in `config.yml` gespeichert

---

## 📁 Installation

1. Plugin kompilieren (`mvn clean package`)
2. `SMP.jar` in den Server-Ordner kopieren:

```

/plugins/SMP.jar

```

3. Server starten  
4. Die Config wird automatisch erstellt:

```

/plugins/smp/config.yml

````

---

## ⚙️ Konfiguration

`config.yml`:

```yaml
rockets-enabled: true
cooldown-seconds: 5
end-enabled: true
````

---

## 🧭 Commands

| Command     | Beschreibung                 | Permission          |
| ----------- | ---------------------------- | ------------------- |
| `/admin`    | Admin GUI öffnen             | `ouh.admin`         |
| `/vanish`   | Unsichtbarkeit an/aus        | `ouh.vanish`        |
| `/help`     | Willkommensbuch              | -                   |
| `/tpa`      | Teleport-Anfrage senden      | -                   |
| `/tpahere`  | Spieler zu dir teleportieren | -                   |
| `/tpaccept` | Anfrage annehmen             | -                   |
| `/tpdeny`   | Anfrage ablehnen             | -                   |
| `/rockets`  | Rockets ein/aus              | `ouh.admin.rockets` |
| `/openend`  | End öffnen/schließen         | `ouh.admin.end`     |

---

## 🔐 Permissions

```yaml
ouh.admin               - Zugang zum Admin-Menü
ouh.admin.item          - Admin-Item erhalten
ouh.admin.tpr           - Random Teleport aus Admin-Menü
ouh.admin.players       - Zugriff auf Spieler-Liste
ouh.admin.players.tpt   - Teleport zu Spielern
ouh.admin.players.god   - God-Mode in Admin-Menü

ouh.vanish              - Darf /vanish benutzen
ouh.vanish.item         - Vanish-Item anzeigen

ouh.tpa.cooldown        - TPA Cooldown reduzieren

ouh.admin.rockets       - Darf Rockets toggeln
ouh.admin.end           - Darf End öffnen/schließen
```

---

## 🧩 Kompatibilität

* **Paper 1.21+**
* Java **17–21**
* Getestet mit LuckPerms
* Funktioniert mit allen gängigen SMP-Serverstrukturen

---

## 📂 Projektstruktur (empfohlen)

```
src/
 ├── main/
 │    ├── java/de/ben/
 │    │      ├── smp.java
 │    │      ├── AdminMenu.java
 │    │      ├── VanishManager.java
 │    │      ├── FirstJoin.java
 │    │      ├── Messages.java
 │    │      ├── TeleportRequest.java
 │    │      ├── rockets/
 │    │      │      ├── RocketCommand.java
 │    │      │      └── RocketListener.java
 │    │      └── end/
 │    │             ├── OpenEndCommand.java
 │    │             └── EndBlocker.java
 │    └── resources/
 │           └── plugin.yml
 ├── pom.xml
```

---

## ❤️ Support & Erweiterungen

Dieses Plugin ist modular aufgebaut und lässt sich leicht erweitern:

* Extra Menüs
* Scoreboards
* Bossbars
* Timer-Systeme
* World-Management
* Custom Items

Wenn du neue Features brauchst → einfach melden!

---

## 📜 Lizenz

Dieses Plugin darf frei genutzt, angepasst und erweitert werden.

---

**Viel Spaß mit deinem SMP Plugin! 🚀**

```

---

Wenn du willst, mache ich dir jetzt:

🔥 Ein **README Banner**  
🔥 Ein **Plugin Icon (PNG)**  
🔥 Eine **Spigot/MC-Market Beschreibung im HTML-Stil**  
🔥 Ein **GitHub Release Template**

Sag einfach:  
👉 **„mach Banner“** oder **„mach Plugin Icon“**
```
