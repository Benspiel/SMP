package de.ben;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class VanishManager implements CommandExecutor, TabCompleter, Listener {

    private final Set<UUID> vanished = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("[OUH] Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        if (!p.hasPermission("ouh.vanish")) {
            Messages.send(p, ChatColor.RED + "Keine Berechtigung!");
            return true;
        }

        boolean isVanished = vanished.contains(p.getUniqueId());
        vanished.remove(p.getUniqueId());

        if (isVanished) {
            // Vanish deaktivieren
            for (Player other : Bukkit.getOnlinePlayers()) {
                other.showPlayer(p);
            }
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            Messages.send(p, ChatColor.YELLOW + "Vanish deaktiviert.");

            // Blitz, falls Blitzmodus aktiv
            if (isLightningEnabled(p)) {
                p.getWorld().strikeLightningEffect(p.getLocation());
            }

        } else {
            // Vanish aktivieren
            vanished.add(p.getUniqueId());
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(p)) other.hidePlayer(p);
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,
                    Integer.MAX_VALUE, 0, true, false, false));
            Messages.send(p, ChatColor.GREEN + "Vanish aktiviert.");
        }

        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player joiner = event.getPlayer();
        for (UUID id : vanished) {
            Player v = Bukkit.getPlayer(id);
            if (v != null && v.isOnline()) {
                joiner.hidePlayer(v);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    public boolean isVanished(Player p) {
        return vanished.contains(p.getUniqueId());
    }

    // Prüft ob Blitzmodus aktiv (von AdminMenu)
    private boolean isLightningEnabled(Player p) {
        // Zugriff auf AdminMenu-Instanz
        if (Bukkit.getPluginManager().getPlugin("OUH") instanceof smp ouh) {
            // AdminMenu hat static Zugriff
            return AdminMenu.isLightningActive(p);
        }
        return false;
    }
}
