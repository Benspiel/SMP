package de.ben;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;

public class Messages implements Listener {

    private static final String PREFIX = ChatColor.DARK_AQUA + "[OUH] " + ChatColor.RESET;

    public static void send(Player p, String msg) {
        p.sendMessage(PREFIX + msg);
    }

    public static void broadcast(String msg) {
        Bukkit.getServer().broadcastMessage(PREFIX + msg);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        String msg = event.getDeathMessage();
        if (msg != null) {
            event.setDeathMessage(PREFIX + ChatColor.RED + msg);
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Player p = event.getPlayer();
        Advancement adv = event.getAdvancement();
        AdvancementProgress progress = p.getAdvancementProgress(adv);
        if (!progress.isDone()) return;

        String key = adv.getKey().getKey();
        String[] parts = key.split("/");
        String name = parts[parts.length - 1].replace("_", " ");

        broadcast(ChatColor.GOLD + p.getName() +
                ChatColor.YELLOW + " hat das Advancement " +
                ChatColor.AQUA + "\"" + name + "\"" +
                ChatColor.YELLOW + " abgeschlossen!");
    }
}
