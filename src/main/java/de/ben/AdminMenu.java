package de.ben;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class AdminMenu implements Listener, CommandExecutor {

    private static final String MAIN_MENU_TITLE = "FOG Admin Menu";
    private static final String PLAYER_LIST_TITLE = "FOG Player List";
    private static final String PLAYER_OPTIONS_PREFIX = "FOG Player Options: ";

    private static final Set<UUID> lightningMode = new HashSet<>();

    private final smp plugin;
    private final Map<UUID, GameMode> previousGameModes = new HashMap<>();
    private final Random random = new Random();

    public AdminMenu(smp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("[FOG] Nur Spieler koennen /admin nutzen.");
            return true;
        }
        if (!p.hasPermission("fog.admin")) {
            Messages.send(p, ChatColor.RED + "Keine Berechtigung.");
            return true;
        }
        openMainMenu(p);
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("fog.admin.item")) {
            ItemStack star = new ItemStack(Material.NETHER_STAR);
            ItemMeta meta = star.getItemMeta();
            if (meta == null) {
                return;
            }
            meta.setDisplayName(ChatColor.AQUA + "Admin Menu");
            star.setItemMeta(meta);
            p.getInventory().setItem(8, star);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (!p.hasPermission("fog.admin")) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.NETHER_STAR || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.getDisplayName().contains("Admin Menu")) {
            event.setCancelled(true);
            openMainMenu(p);
        }
    }

    public void openMainMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_BLUE + MAIN_MENU_TITLE);

        if (p.hasPermission("fog.spectator")) {
            boolean active = p.getGameMode() == GameMode.SPECTATOR;
            inv.setItem(4, createItem(
                    Material.ENDER_EYE,
                    ChatColor.LIGHT_PURPLE + "Spectator Toggle",
                    "Wechselt in den Spectator-Modus",
                    "Status: " + (active ? "aktiv" : "inaktiv"),
                    "Permission: fog.spectator"
            ));
        }

        inv.setItem(11, createItem(
                Material.ENDER_PEARL,
                ChatColor.GREEN + "Random Teleport",
                "Teleport zu einem zufaelligen Spieler",
                "Permission: fog.admin.tpr"
        ));

        inv.setItem(15, createItem(
                Material.PLAYER_HEAD,
                ChatColor.GOLD + "Spieler Liste",
                "Zeigt alle Spieler mit Koepfen an",
                "Permission: fog.admin.players"
        ));

        if (p.hasPermission("fog.vanish")) {
            inv.setItem(13, createItem(
                    Material.FEATHER,
                    ChatColor.DARK_GRAY + "Vanish Toggle",
                    "Macht dich unsichtbar oder sichtbar",
                    "Permission: fog.vanish"
            ));
        }

        if (p.isOp()) {
            boolean active = lightningMode.contains(p.getUniqueId());
            inv.setItem(22, createItem(
                    Material.TRIDENT,
                    ChatColor.AQUA + "Blitzmodus: " + (active ? ChatColor.GREEN + "AN" : ChatColor.RED + "AUS"),
                    "Wenn aktiv, schlaegt bei Teleports oder Vanish ein Blitz ein"
            ));
        }

        if (p.hasPermission("fog.admin.trading")) {
            boolean active = plugin.getConfig().getBoolean("infinite-trading-enabled");
            inv.setItem(24, createItem(
                    Material.EMERALD,
                    ChatColor.GREEN + "Infinite Trading: " + (active ? ChatColor.GREEN + "AN" : ChatColor.RED + "AUS"),
                    "Villager-Trades verbrauchen sich nicht",
                    "Permission: fog.admin.trading"
            ));
        }

        p.openInventory(inv);
    }

    private ItemStack createItem(Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        for (String line : loreLines) {
            lore.add(ChatColor.GRAY + line);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) {
            return;
        }

        String title = ChatColor.stripColor(event.getView().getTitle());
        if (!isManagedMenu(title)) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String name = ChatColor.stripColor(meta.getDisplayName());

        if (MAIN_MENU_TITLE.equals(title)) {
            handleMainMenuClick(p, name);
            return;
        }

        if (PLAYER_LIST_TITLE.equals(title)) {
            handlePlayerListClick(p, clicked);
            return;
        }

        if (title.startsWith(PLAYER_OPTIONS_PREFIX)) {
            handlePlayerOptionsClick(p, title, name);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (isManagedMenu(title)) {
            event.setCancelled(true);
        }
    }

    private void handleMainMenuClick(Player player, String name) {
        if (name.equalsIgnoreCase("Spectator Toggle") && player.hasPermission("fog.spectator")) {
            toggleSpectator(player);
            strikeLightningIfEnabled(player);
            player.closeInventory();
            return;
        }

        if (name.equalsIgnoreCase("Random Teleport") && player.hasPermission("fog.admin.tpr")) {
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            players.remove(player);
            if (players.isEmpty()) {
                Messages.send(player, ChatColor.RED + "Keine Spieler online!");
                return;
            }

            Player target = players.get(random.nextInt(players.size()));
            player.teleport(target.getLocation());
            Messages.send(player, ChatColor.GREEN + "Teleportiert zu " + target.getName());
            strikeLightningIfEnabled(player);
            player.closeInventory();
            return;
        }

        if (name.equalsIgnoreCase("Spieler Liste") && player.hasPermission("fog.admin.players")) {
            openPlayerList(player);
            return;
        }

        if (name.equalsIgnoreCase("Vanish Toggle") && player.hasPermission("fog.vanish")) {
            player.performCommand("vanish");
            strikeLightningIfEnabled(player);
            player.closeInventory();
            return;
        }

        if (name.startsWith("Blitzmodus") && player.isOp()) {
            if (lightningMode.contains(player.getUniqueId())) {
                lightningMode.remove(player.getUniqueId());
                Messages.send(player, ChatColor.YELLOW + "Blitzmodus deaktiviert.");
            } else {
                lightningMode.add(player.getUniqueId());
                Messages.send(player, ChatColor.GREEN + "Blitzmodus aktiviert.");
            }
            player.closeInventory();
            return;
        }

        if (name.startsWith("Infinite Trading") && player.hasPermission("fog.admin.trading")) {
            toggleInfiniteTrading(player);
            player.closeInventory();
        }
    }

    private void handlePlayerListClick(Player player, ItemStack clicked) {
        if (clicked.getType() != Material.PLAYER_HEAD) {
            return;
        }

        SkullMeta skull = (SkullMeta) clicked.getItemMeta();
        if (skull == null || skull.getOwningPlayer() == null) {
            return;
        }

        Player target = skull.getOwningPlayer().getPlayer();
        if (target != null) {
            openPlayerOptions(player, target);
        }
    }

    private void handlePlayerOptionsClick(Player player, String title, String name) {
        String targetName = title.substring(PLAYER_OPTIONS_PREFIX.length());
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            return;
        }

        if (name.equalsIgnoreCase("Teleport") && player.hasPermission("fog.admin.players.tpt")) {
            player.teleport(target.getLocation());
            Messages.send(player, ChatColor.GREEN + "Teleportiert zu " + target.getName());
            strikeLightningIfEnabled(player);
            player.closeInventory();
            return;
        }

        if (name.equalsIgnoreCase("God Mode") && player.hasPermission("fog.admin.players.god")) {
            target.setInvulnerable(!target.isInvulnerable());
            Messages.send(
                    player,
                    ChatColor.YELLOW + "God Mode fuer " + target.getName()
                            + (target.isInvulnerable() ? " aktiviert" : " deaktiviert")
            );
            player.closeInventory();
        }
    }

    private void openPlayerList(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + PLAYER_LIST_TITLE);
        int slot = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta == null) {
                continue;
            }
            meta.setOwningPlayer(target);
            meta.setDisplayName(ChatColor.YELLOW + target.getName());
            head.setItemMeta(meta);
            inv.setItem(slot++, head);
        }
        player.openInventory(inv);
    }

    private void openPlayerOptions(Player admin, Player target) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + PLAYER_OPTIONS_PREFIX + target.getName());

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(target);
            meta.setDisplayName(ChatColor.YELLOW + target.getName());
            head.setItemMeta(meta);
        }
        inv.setItem(0, head);

        inv.setItem(11, createItem(
                Material.ENDER_PEARL,
                ChatColor.GREEN + "Teleport",
                "Teleportiere dich zu diesem Spieler"
        ));

        inv.setItem(15, createItem(
                Material.NETHERITE_CHESTPLATE,
                ChatColor.RED + "God Mode",
                "Aktiviere oder deaktiviere Unverwundbarkeit"
        ));

        admin.openInventory(inv);
    }

    private void toggleSpectator(Player player) {
        UUID uuid = player.getUniqueId();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            GameMode previousMode = previousGameModes.getOrDefault(uuid, GameMode.SURVIVAL);
            previousGameModes.remove(uuid);
            player.setGameMode(previousMode);
            Messages.send(player, ChatColor.YELLOW + "Spectator deaktiviert.");
            return;
        }

        previousGameModes.put(uuid, player.getGameMode());
        player.setGameMode(GameMode.SPECTATOR);
        Messages.send(player, ChatColor.GREEN + "Spectator aktiviert.");
    }

    private void strikeLightningIfEnabled(Player player) {
        if (lightningMode.contains(player.getUniqueId())) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }
    }

    private void toggleInfiniteTrading(Player player) {
        boolean newState = !plugin.getConfig().getBoolean("infinite-trading-enabled");
        plugin.getConfig().set("infinite-trading-enabled", newState);
        plugin.saveConfig();

        Messages.send(
                player,
                ChatColor.GREEN + "Infinite Trading ist jetzt "
                        + (newState ? ChatColor.GREEN + "aktiviert" : ChatColor.RED + "deaktiviert")
                        + ChatColor.GREEN + "."
        );
    }

    private boolean isManagedMenu(String title) {
        return MAIN_MENU_TITLE.equals(title)
                || PLAYER_LIST_TITLE.equals(title)
                || title.startsWith(PLAYER_OPTIONS_PREFIX);
    }

    public static boolean isLightningActive(Player p) {
        return lightningMode.contains(p.getUniqueId());
    }
}
