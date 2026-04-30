# SMP Plugin

Ein modulares Paper-Plugin fuer SMP-Server mit Admin-Menue, Vanish, Spectator-Toggle, Teleport-System, End-/Nether-Steuerung und Rocket-Kontrolle.

## Features

- Admin-Menue mit Random Teleport, Spielerliste, God Mode, Vanish, Spectator und Blitzmodus
- Vanish-System mit eigener Permission und Menu-Eintrag
- Spectator-Toggle direkt im Admin-Menue
- TPA-System mit `/tpa`, `/tpahere`, `/tpaccept` und `/tpdeny`
- Rockets global aktivierbar oder deaktivierbar
- End und Nether per Command steuerbar

## Installation

1. Plugin bauen: `mvn clean package`
2. JAR in den Server kopieren
3. Server starten

## Konfiguratio

`src/main/resources/config.yml`

```yaml
rockets-enabled: true
cooldown-seconds: 5
end-enabled: true
nether-enabled: true
```

## Commands

| Command | Beschreibung | Permission |
| --- | --- | --- |
| `/admin` | Oeffnet das Admin-Menue | `fog.admin` |
| `/vanish` | Schaltet Vanish an oder aus | `fog.vanish` |
| `/help` | Gibt das Willkommens-Buch | - |
| `/tpa <spieler>` | Sendet eine Teleport-Anfrage | - |
| `/tpahere <spieler>` | Fragt an, ob ein Spieler zu dir teleportiert werden soll | - |
| `/tpaccept` | Akzeptiert eine Teleport-Anfrage | - |
| `/tpdeny` | Lehnt eine Teleport-Anfrage ab | - |
| `/rockets` | Aktiviert oder deaktiviert Rockets | `fog.admin.rockets` |
| `/end` | Oeffnet oder schliesst das End | `fog.admin.end` |
| `/opennether` | Oeffnet oder schliesst den Nether | `fog.admin.nether` |

## Permissions

| Permission | Beschreibung | Default |
| --- | --- | --- |
| `fog.admin` | Zugriff auf das Admin-Menue mit `/admin` | `op` |
| `fog.admin.item` | Gibt beim Join das Admin-Item | `op` |
| `fog.admin.tpr` | Erlaubt Random-Teleport im Admin-Menue | `op` |
| `fog.admin.players` | Zugriff auf die Spieler-Liste im Admin-Menue | `op` |
| `fog.admin.players.tpt` | Erlaubt Teleport zu einem Spieler aus der Spieler-Liste | `op` |
| `fog.admin.players.god` | Erlaubt God Mode fuer Spieler aus dem Admin-Menue | `op` |
| `fog.vanish` | Erlaubt `/vanish` und zeigt den Vanish-Toggle im Admin-Menue | `op` |
| `fog.spectator` | Erlaubt den Spectator-Toggle und zeigt den Spectator-Button im Admin-Menue | `op` |
| `fog.tpa.cooldown` | Reduziert den TPA-Cooldown auf 45 Sekunden | `op` |
| `fog.admin.rockets` | Erlaubt `/rockets` | `op` |
| `fog.admin.end` | Erlaubt `/end` | `op` |
| `fog.admin.nether` | Erlaubt `/opennether` | `op` |

## Hinweise

- API-Version: `1.21`
- Java-Version laut `pom.xml`: `21`
- Die Permissions stammen direkt aus `src/main/resources/plugin.yml`
