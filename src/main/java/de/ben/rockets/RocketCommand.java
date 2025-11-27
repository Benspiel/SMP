package de.ben.rockets;

import de.ben.smp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RocketCommand implements CommandExecutor {

    private final smp plugin;

    public RocketCommand(smp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player p) {
            if (!p.hasPermission("ouh.admin.rockets")) {
                p.sendMessage(ChatColor.RED + "Keine Rechte!");
                return true;
            }
        }

        boolean current = plugin.getConfig().getBoolean("rockets-enabled");
        boolean newState = !current;

        plugin.getConfig().set("rockets-enabled", newState);
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GOLD + "Rockets sind jetzt: " +
                (newState ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));

        return true;
    }
}
