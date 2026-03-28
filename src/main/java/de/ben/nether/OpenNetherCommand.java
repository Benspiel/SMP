package de.ben.nether;

import de.ben.smp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenNetherCommand implements CommandExecutor {

    private final smp plugin;

    public OpenNetherCommand(smp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player p) {
            if (!p.hasPermission("fog.admin.nether")) {
                p.sendMessage(ChatColor.RED + "Keine Rechte!");
                return true;
            }
        }

        boolean current = plugin.getConfig().getBoolean("nether-enabled");
        boolean newState = !current;

        plugin.getConfig().set("nether-enabled", newState);
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GOLD + "Nether ist jetzt: " +
                (newState ? ChatColor.GREEN + "GEÖFFNET" : ChatColor.RED + "GESCHLOSSEN"));

        return true;
    }
}
