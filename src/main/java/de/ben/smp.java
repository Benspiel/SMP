package de.ben;

import de.ben.rockets.RocketCommand;
import de.ben.rockets.RocketListener;

import de.ben.end.OpenEndCommand;
import de.ben.end.EndBlocker;

import de.ben.nether.OpenNetherCommand;
import de.ben.nether.NetherBlocker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class smp extends JavaPlugin {

    @Override
    public void onEnable() {

        // ------------------ CONFIG (lädt + erstellt) ------------------
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        // ------------------ ADMIN MENU (/admin) ------------------
        AdminMenu adminMenu = new AdminMenu(this);
        Bukkit.getPluginManager().registerEvents(adminMenu, this);
        getCommand("admin").setExecutor(adminMenu);

        // ------------------ VANISH SYSTEM (/vanish) ------------------
        VanishManager vanishManager = new VanishManager();
        Bukkit.getPluginManager().registerEvents(vanishManager, this);
        getCommand("vanish").setExecutor(vanishManager);
        getCommand("vanish").setTabCompleter(vanishManager);

        // ------------------ FIRST JOIN + HELP ------------------
        FirstJoin firstJoin = new FirstJoin(this);
        getCommand("help").setExecutor(firstJoin);

        // ------------------ MESSAGES SYSTEM ------------------
        Bukkit.getPluginManager().registerEvents(new Messages(), this);

        // ------------------ TELEPORT SYSTEM (/tpa usw.) ------------------
        TeleportRequest tpa = new TeleportRequest();
        tpa.init(this);
        Bukkit.getPluginManager().registerEvents(tpa, this);

        getCommand("tpa").setExecutor(tpa);
        getCommand("tpahere").setExecutor(tpa);
        getCommand("tpaccept").setExecutor(tpa);
        getCommand("tpdeny").setExecutor(tpa);

        getCommand("tpa").setTabCompleter(tpa);
        getCommand("tpahere").setTabCompleter(tpa);

        // ------------------ ROCKETS SYSTEM (/rockets) ------------------
        getCommand("rockets").setExecutor(new RocketCommand(this));
        Bukkit.getPluginManager().registerEvents(new RocketListener(this), this);

        // ------------------ END SYSTEM (/end) ------------------
        getCommand("end").setExecutor(new OpenEndCommand(this));
        Bukkit.getPluginManager().registerEvents(new EndBlocker(this), this);

        // ------------------ NETHER SYSTEM (/opennether) ------------------
        getCommand("opennether").setExecutor(new OpenNetherCommand(this));
        Bukkit.getPluginManager().registerEvents(new NetherBlocker(this), this);

        // ------------------ LOG ------------------
        getLogger().info("=======================================");
        getLogger().info(" SMP Plugin gestartet!");
        getLogger().info(" Rockets: " + getConfig().getBoolean("rockets-enabled"));
        getLogger().info(" End:     " + getConfig().getBoolean("end-enabled"));
        getLogger().info(" Nether:  " + getConfig().getBoolean("nether-enabled"));
        getLogger().info("=======================================");
    }

    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("SMP Plugin gestoppt.");
    }
}
