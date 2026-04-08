package de.ben.trading;

import de.ben.smp; // Importiert deine Hauptklasse für den Config-Zugriff
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.PlayerTradeEvent;

/**
 * Dieser Listener steuert zwei Hauptfunktionen:
 * 1. Infinite Trading: Stellt sicher, dass Angebote nie ausgehen.
 * 2. XP-Limit: Verhindert, dass Master-Kartographen dem Spieler XP geben.
 */
public class InfiniteTradingListener implements Listener {

    private final smp plugin;

    /**
     * Konstruktor zur Übergabe der Plugin-Instanz.
     * @param plugin Die Hauptklasse deines Plugins.
     */
    public InfiniteTradingListener(smp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTrade(PlayerTradeEvent event) {
        // Prüft, ob das Feature in der config.yml aktiviert ist
        if (!plugin.getConfig().getBoolean("infinite-trading-enabled", true)) {
            return;
        }

        // Wir arbeiten nur mit Villagern (nicht Wandering Traders etc.)
        if (!(event.getVillager() instanceof Villager villager)) {
            return;
        }

        // LOGIK 1: Unendliches Handeln
        // restock() setzt den Benutzungszähler aller Trades zurück
        villager.restock();

        // LOGIK 2: XP-Sperre für Kartographen
        if (villager.getProfession() == Villager.Profession.CARTOGRAPHER) {
            // Level 5 entspricht dem "Master" Rang
            if (villager.getVillagerLevel() >= 5) {
                // Deaktiviert die XP-Belohnung für diesen spezifischen Trade
                event.setRewardExp(false);
            }
        }
    }
} // WICHTIG: Diese Klammer schließt die Klasse!
