package de.ben;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class AdminMenu implements Listener, CommandExecutor {

    private final smp plugin;
    private static final Set<UUID> lightningMode = new HashSet<>();

    public AdminMenu(smp plugin) {
        this.plugin = plugin;
    }

    // /admin Befehl
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("[OUH] Nur Spieler können /admin nutzen.");
            return true;
        }
        if (!p.hasPermission("ouh.admin")) {
            Messages.send(p, ChatColor.RED + "Keine Berechtigung.");
            return true;
        }
        openMainMenu(p);
        return true;
    }

    // Nether Star beim Join (nur mit ouh.admin.item)
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("ouh.admin.item")) {
            ItemStack star = new ItemStack(Material.NETHER_STAR);
            ItemMeta meta = star.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + "Admin Menu");
            star.setItemMeta(meta);
            p.getInventory().setItem(8, star);
        }
    }

    // Klick auf Nether Star öffnet Menü
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (!p.hasPermission("ouh.admin")) return;

        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.NETHER_STAR && item.hasItemMeta()) {
            if (item.getItemMeta().getDisplayName().contains("Admin Menu")) {
                event.setCancelled(true);
                openMainMenu(p);
            }
        }
    }

    // Hauptmenü
    public void openMainMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_BLUE + "OUH Admin Menu");

        // Random TP
        inv.setItem(11, createItem(Material.ENDER_PEARL, ChatColor.GREEN + "Random Teleport",
                "Teleport zu einem zufälligen Spieler", "Permission: ouh.admin.tpr"));

        // Spieler Liste
        inv.setItem(15, createItem(Material.PLAYER_HEAD, ChatColor.GOLD + "Spieler Liste",
                "Zeigt alle Spieler mit Köpfen an", "Permission: ouh.admin.players"));

        // Vanish Feder
        if (p.hasPermission("ouh.vanish.item")) {
            inv.setItem(13, createItem(Material.FEATHER, ChatColor.DARK_GRAY + "Vanish Toggle",
                    "Macht dich unsichtbar/sichtbar", "Permission: ouh.vanish"));
        }

        // Dreizack (Blitzmodus)
        if (p.isOp()) {
            boolean active = lightningMode.contains(p.getUniqueId());
            inv.setItem(22, createItem(Material.TRIDENT,
                    ChatColor.AQUA + "Blitzmodus: " + (active ? ChatColor.GREEN + "AN" : ChatColor.RED + "AUS"),
                    "Wenn aktiv, schlägt bei Teleports oder Vanish ein Blitz ein"));
        }

        p.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        for (String s : loreLines) lore.add(ChatColor.GRAY + s);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // Menü-Klicks
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String title = event.getView().getTitle();
        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        // --- Hauptmenü ---
        if (title.contains("OUH Admin Menu")) {
            event.setCancelled(true);

            // Random Teleport
            if (name.equalsIgnoreCase("Random Teleport") && p.hasPermission("ouh.admin.tpr")) {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                players.remove(p);
                if (players.isEmpty()) {
                    Messages.send(p, ChatColor.RED + "Keine Spieler online!");
                    return;
                }
                Player target = players.get(new Random().nextInt(players.size()));
                p.teleport(target.getLocation());
                Messages.send(p, ChatColor.GREEN + "Teleportiert zu " + target.getName());

                // Blitz
                if (lightningMode.contains(p.getUniqueId())) {
                    p.getWorld().strikeLightningEffect(p.getLocation());
                }
                p.closeInventory();
            }

            // Spieler Liste
            if (name.equalsIgnoreCase("Spieler Liste") && p.hasPermission("ouh.admin.players")) {
                openPlayerList(p);
            }

            // Vanish
            if (name.equalsIgnoreCase("Vanish Toggle") && p.hasPermission("ouh.vanish")) {
                p.performCommand("vanish");
                if (lightningMode.contains(p.getUniqueId())) {
                    p.getWorld().strikeLightningEffect(p.getLocation());
                }
                p.closeInventory();
            }

            // Blitzmodus
            if (name.startsWith("Blitzmodus") && p.isOp()) {
                if (lightningMode.contains(p.getUniqueId())) {
                    lightningMode.remove(p.getUniqueId());
                    Messages.send(p, ChatColor.YELLOW + "Blitzmodus deaktiviert.");
                } else {
                    lightningMode.add(p.getUniqueId());
                    Messages.send(p, ChatColor.GREEN + "Blitzmodus aktiviert.");
                }
                p.closeInventory();
            }
        }

        // --- Spieler Liste ---
        if (title.contains("OUH Player List")) {
            event.setCancelled(true);
            if (clicked.getType() != Material.PLAYER_HEAD) return;

            SkullMeta skull = (SkullMeta) clicked.getItemMeta();
            if (skull != null && skull.getOwningPlayer() != null) {
                Player target = skull.getOwningPlayer().getPlayer();
                if (target != null) {
                    openPlayerOptions(p, target);
                }
            }
        }

        // --- Spieler Optionen ---
        if (title.contains("OUH Player Options")) {
            event.setCancelled(true);
            String[] parts = title.split(": ");
            if (parts.length != 2) return;

            Player target = Bukkit.getPlayer(parts[1]);
            if (target == null) return;

            // Teleport
            if (name.equalsIgnoreCase("Teleport") && p.hasPermission("ouh.admin.players.tpt")) {
                p.teleport(target.getLocation());
                Messages.send(p, ChatColor.GREEN + "Teleportiert zu " + target.getName());
                if (lightningMode.contains(p.getUniqueId())) {
                    p.getWorld().strikeLightningEffect(p.getLocation());
                }
                p.closeInventory();
            }

            // God Mode
            if (name.equalsIgnoreCase("God Mode") && p.hasPermission("ouh.admin.players.god")) {
                target.setInvulnerable(!target.isInvulnerable());
                Messages.send(p, ChatColor.YELLOW + "God Mode für " + target.getName() +
                        (target.isInvulnerable() ? " aktiviert" : " deaktiviert"));
                p.closeInventory();
            }
        }
    }

    private void openPlayerList(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "OUH Player List");
        int slot = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(target);
            meta.setDisplayName(ChatColor.YELLOW + target.getName());
            head.setItemMeta(meta);
            inv.setItem(slot++, head);
        }
        p.openInventory(inv);
    }

    private void openPlayerOptions(Player admin, Player target) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "OUH Player Options: " + target.getName());

        // Kopf
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(target);
        meta.setDisplayName(ChatColor.YELLOW + target.getName());
        head.setItemMeta(meta);
        inv.setItem(0, head);

        // Teleport
        inv.setItem(11, createItem(Material.ENDER_PEARL, ChatColor.GREEN + "Teleport",
                "Teleportiere dich zu diesem Spieler"));

        // God Mode
        inv.setItem(15, createItem(Material.NETHERITE_CHESTPLATE, ChatColor.RED + "God Mode",
                "Aktiviere/Deaktiviere Unverwundbarkeit"));

        admin.openInventory(inv);
    }

    // Zugriff für VanishManager
    public static boolean isLightningActive(Player p) {
        return lightningMode.contains(p.getUniqueId());
    }
}
