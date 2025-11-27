package de.ben.end;

import de.ben.smp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class EndBlocker implements Listener {

    private final smp plugin;

    public EndBlocker(smp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {

        Player p = e.getPlayer();

        // Nur blocken wenn End geschlossen ist
        if (plugin.getConfig().getBoolean("end-enabled"))
            return;

        // Ist Ziel das End?
        Location to = e.getTo();
        if (to != null && to.getWorld() != null &&
                to.getWorld().getEnvironment() == Environment.THE_END) {

            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "Das End ist derzeit geschlossen!");
        }
    }
}
