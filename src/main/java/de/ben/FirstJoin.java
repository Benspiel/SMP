package de.ben;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FirstJoin implements Listener, CommandExecutor {

    private final smp plugin;
    private final File dataFile;
    private final Set<UUID> joinedPlayers = new HashSet<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public FirstJoin(smp plugin) {
        this.plugin = plugin;

        // Plugin-Datenordner verwenden (funktioniert in 1.21)
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) pluginFolder.mkdirs();

        dataFile = new File(pluginFolder, "players.json");

        // Bestehende Spieler laden
        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile)) {
                Type type = new TypeToken<Set<UUID>>() {}.getType();
                Set<UUID> loaded = gson.fromJson(reader, type);
                if (loaded != null) joinedPlayers.addAll(loaded);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveData() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(joinedPlayers, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!joinedPlayers.contains(player.getUniqueId())) {
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();

            meta.setAuthor("Serverteam");
            meta.setTitle("Willkommen!");

            // Seite 1 mit klickbarem Link
            TextComponent page1 = new TextComponent("Hallo " + player.getName() + ",\n\n");
            page1.addExtra("Vielen Dank dir, dass du das Buch öffnest.\n\n");
            page1.addExtra("Hier sind die ");

            TextComponent rules = new TextComponent("Regeln");
            rules.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                    "https://wiki.xn--oberberhausen-zob.de/oberuberhausen-wiki/oberattack/oberattack-regelwerk"));
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

            joinedPlayers.add(player.getUniqueId());
            saveData();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(item ->
                item.getType() == Material.WRITTEN_BOOK &&
                        item.getItemMeta() instanceof BookMeta &&
                        ((BookMeta) item.getItemMeta()).getTitle().equals("Willkommen!"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

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
