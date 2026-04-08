package de.ben;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class Messages implements Listener {

    private static final String PREFIX = ChatColor.DARK_AQUA + "[FOG] " + ChatColor.RESET;

    public static void send(Player p, String msg) {
        p.sendMessage(PREFIX + msg);
    }

    public static void broadcast(String msg) {
        Bukkit.getServer().broadcastMessage(PREFIX + msg);
    }

    public static void disableVanillaAdvancementAnnouncements() {
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        event.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
    }
}
