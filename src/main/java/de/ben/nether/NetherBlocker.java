package de.ben.nether;

import de.ben.smp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class NetherBlocker implements Listener {

    private final smp plugin;

    public NetherBlocker(smp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {

        Player p = e.getPlayer();

        // Nether offen? -> nichts blocken
        if (plugin.getConfig().getBoolean("nether-enabled"))
            return;

        // Zielwelt checken
        Location to = e.getTo();
        if (to != null && to.getWorld() != null &&
                to.getWorld().getEnvironment() == Environment.NETHER) {

            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "Der Nether ist derzeit geschlossen!");
        }
    }
}
