package de.ben;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class FirstJoin implements CommandExecutor {

    public FirstJoin(smp plugin) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.setAuthor("Serverteam");
        meta.setTitle("Willkommen!");

        TextComponent page1 = new TextComponent("Hallo " + player.getName() + ",\n\n");
        page1.addExtra("Hier sind die ");

        TextComponent rules = new TextComponent("Regeln");
        rules.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                "https://cdn.discordapp.com/attachments/1361061127408062726/1363484251538067597/Regeln__Mods_OA2.pdf"));
        rules.setUnderlined(true);
        rules.setBold(true);

        page1.addExtra(rules);
        page1.addExtra(".");

        String page2 =
                "Hier ein paar grundlegende Befehle:\n" +
                        " - /spawn: Teleportiert dich zum Spawn\n" +
                        " - /tpa: Teleportiere dich zu Spielern\n" +
                        " - /co inspect: Zeigt dir wer an deinen Kisten war\n" +
                        " - /help: Gibt dir dieses Buch erneut";

        meta.spigot().addPage(new ComponentBuilder(page1).create());
        meta.addPage(page2);

        book.setItemMeta(meta);
        player.getInventory().addItem(book);

        return true;
    }
}
