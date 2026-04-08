package de.ben;

import de.ben.rockets.RocketCommand;
import de.ben.rockets.RocketListener;
import de.ben.trading.InfiniteTradingListener;
import de.ben.end.OpenEndCommand;
import de.ben.end.EndBlocker;
import de.ben.nether.OpenNetherCommand;
import de.ben.nether.NetherBlocker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hauptklasse des SMP-Plugins.
 * Verantwortlich für das Laden der Konfiguration und die Registrierung aller Module.
 */
public final class smp extends JavaPlugin {

    @Override
    public void onEnable() {
        // 1. KONFIGURATION
        // Erstellt die config.yml aus den Ressourcen, falls sie noch nicht existiert.
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        // 2. ADMIN & VANISH SYSTEM
        // Das Admin-Menü und der Vanish-Manager werden initialisiert und registriert.
        AdminMenu adminMenu = new AdminMenu(this);
        Bukkit.getPluginManager().registerEvents(adminMenu, this);
        getCommand("admin").setExecutor(adminMenu);

        VanishManager vanishManager = new VanishManager();
        Bukkit.getPluginManager().registerEvents(vanishManager, this);
        getCommand("vanish").setExecutor(vanishManager);
        getCommand("vanish").setTabCompleter(vanishManager);

        // 3. JOIN & HELP SYSTEM
        FirstJoin firstJoin = new FirstJoin(this);
        getCommand("help").setExecutor(firstJoin);

        // 4. MESSAGES SYSTEM
        Bukkit.getPluginManager().registerEvents(new Messages(), this);
        Messages.disableVanillaAdvancementAnnouncements();

        // 5. TELEPORT SYSTEM (TPA)
        TeleportRequest tpa = new TeleportRequest();
        tpa.init(this);
        Bukkit.getPluginManager().registerEvents(tpa, this);

        getCommand("tpa").setExecutor(tpa);
        getCommand("tpahere").setExecutor(tpa);
        getCommand("tpaccept").setExecutor(tpa);
        getCommand("tpdeny").setExecutor(tpa);
        getCommand("tpa").setTabCompleter(tpa);
        getCommand("tpahere").setTabCompleter(tpa);

        // 6. ROCKETS SYSTEM
        getCommand("rockets").setExecutor(new RocketCommand(this));
        Bukkit.getPluginManager().registerEvents(new RocketListener(this), this);

        // 7. INFINITE TRADING (NEU)
        // Registriert den Listener für unendliches Handeln und XP-Filter.
        Bukkit.getPluginManager().registerEvents(new InfiniteTradingListener(this), this);

        // 8. DIMENSIONS SYSTEM (END & NETHER)
        getCommand("end").setExecutor(new OpenEndCommand(this));
        Bukkit.getPluginManager().registerEvents(new EndBlocker(this), this);

        getCommand("opennether").setExecutor(new OpenNetherCommand(this));
        Bukkit.getPluginManager().registerEvents(new NetherBlocker(this), this);

        // 9. STATUS LOG
        printStatusLog();
    }

    @Override
    public void onDisable() {
        // Speichert die aktuelle Konfiguration beim Herunterfahren des Servers.
        saveConfig();
        getLogger().info("SMP Plugin gestoppt.");
    }

    /**
     * Gibt eine Übersicht der aktivierten Module in der Konsole aus.
     */
    private void printStatusLog() {
        getLogger().info("=======================================");
        getLogger().info(" SMP Plugin erfolgreich gestartet!");
        getLogger().info(" Rockets: " + getConfig().getBoolean("rockets-enabled"));
        getLogger().info(" Trading: " + getConfig().getBoolean("infinite-trading-enabled"));
        getLogger().info(" End:     " + getConfig().getBoolean("end-enabled"));
        getLogger().info(" Nether:  " + getConfig().getBoolean("nether-enabled"));
        getLogger().info("=======================================");
    }
}