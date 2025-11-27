package de.ben.end;

import de.ben.smp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenEndCommand implements CommandExecutor {

    private final smp plugin;

    public OpenEndCommand(smp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player p) {
            if (!p.hasPermission("ouh.admin.end")) {
                p.sendMessage(ChatColor.RED + "Keine Rechte!");
                return true;
            }
        }

        boolean current = plugin.getConfig().getBoolean("end-enabled");
        boolean newState = !current;

        plugin.getConfig().set("end-enabled", newState);
        plugin.saveConfig();

        sender.sendMessage(ChatColor.DARK_PURPLE + "End ist jetzt: " +
                (newState ? ChatColor.GREEN + "GEÖFFNET" : ChatColor.RED + "GESCHLOSSEN"));

        return true;
    }
}
