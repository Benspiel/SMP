package de.ben;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class TeleportRequest implements Listener, CommandExecutor, TabCompleter {

    private static class Request {
        final UUID sender;
        final boolean here;
        Request(UUID sender, boolean here) { this.sender = sender; this.here = here; }
    }

    private static final Map<UUID, Request> requests = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    private JavaPlugin plugin;

    public void init(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        switch (cmd.getName().toLowerCase()) {
            case "tpa" -> handleRequest(p, args, false);
            case "tpahere" -> handleRequest(p, args, true);
            case "tpaccept" -> handleAccept(p);
            case "tpdeny" -> handleDeny(p);
        }
        return true;
    }

    private void handleRequest(Player sender, String[] args, boolean here) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Benutzung: /" + (here ? "tpahere <Spieler>" : "tpa <Spieler>"));
            return;
        }

        // --- Cooldown prüfen ---
        long now = System.currentTimeMillis();
        long cdMillis = sender.hasPermission("ouh.tpa.cooldown") ? 90_000L : 360_000L;
        long next = cooldowns.getOrDefault(sender.getUniqueId(), 0L);

        if (now < next) {
            long remaining = (next - now) / 1000;
            sender.sendMessage(ChatColor.RED + "[OUH] Du musst noch " + remaining + " Sekunden warten, bevor du erneut teleportieren kannst.");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline() || target.equals(sender)) {
            sender.sendMessage(ChatColor.RED + "Spieler ungültig oder offline.");
            return;
        }

        requests.put(target.getUniqueId(), new Request(sender.getUniqueId(), here));
        cooldowns.put(sender.getUniqueId(), now + cdMillis); // Cooldown setzen

        sender.sendMessage(ChatColor.YELLOW + "[OUH] Anfrage an " + target.getName() + " gesendet.");

        TextComponent base = new TextComponent(ChatColor.GOLD + "[OUH] " + sender.getName() +
                (here ? " möchte dich zu sich teleportieren.\n" : " möchte sich zu dir teleportieren.\n"));
        TextComponent accept = new TextComponent(ChatColor.GREEN + "[Annehmen]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        TextComponent deny = new TextComponent(ChatColor.RED + " [Ablehnen]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        base.addExtra(accept);
        base.addExtra(deny);
        target.spigot().sendMessage(base);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Request r = requests.get(target.getUniqueId());
            if (r != null && r.sender.equals(sender.getUniqueId())) {
                requests.remove(target.getUniqueId());
                sender.sendMessage(ChatColor.GRAY + "[OUH] Anfrage an " + target.getName() + " ist abgelaufen.");
            }
        }, 20L * 60);
    }

    private void handleAccept(Player target) {
        Request r = requests.remove(target.getUniqueId());
        if (r == null) {
            target.sendMessage(ChatColor.RED + "[OUH] Du hast keine Teleport-Anfrage.");
            return;
        }

        Player sender = Bukkit.getPlayer(r.sender);
        if (sender == null || !sender.isOnline()) {
            target.sendMessage(ChatColor.RED + "[OUH] Der Spieler ist nicht mehr online.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "[OUH] Teleport startet in 5 Sekunden – bleib stehen!");
        target.sendMessage(ChatColor.YELLOW + "[OUH] " + sender.getName() + " wird gleich teleportiert.");

        Location start = sender.getLocation().clone();
        Vector startVec = start.toVector();

        new BukkitRunnable() {
            int sec = 5;

            @Override
            public void run() {
                if (!sender.isOnline() || !target.isOnline()) {
                    cancel();
                    return;
                }

                double move = sender.getLocation().toVector().setY(0)
                        .distance(startVec.clone().setY(0));
                if (move > 0.3) {
                    sender.spigot().sendMessage(
                            net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                            new TextComponent(ChatColor.RED + "Teleport abgebrochen – du hast dich bewegt!")
                    );
                    target.sendMessage(ChatColor.GRAY + "[OUH] " + sender.getName() + " hat sich bewegt, Teleport abgebrochen.");
                    cancel();
                    return;
                }

                if (sec <= 0) {
                    if (r.here) target.teleport(sender.getLocation());
                    else sender.teleport(target.getLocation());

                    sender.getWorld().playSound(sender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);

                    sender.spigot().sendMessage(
                            net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                            new TextComponent(ChatColor.GREEN + "Teleport abgeschlossen!")
                    );
                    cancel();
                    return;
                }

                sender.spigot().sendMessage(
                        net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        new TextComponent(ChatColor.GOLD + "Teleport in " + ChatColor.YELLOW + sec + ChatColor.GOLD + " Sekunden...")
                );
                sec--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void handleDeny(Player target) {
        Request r = requests.remove(target.getUniqueId());
        if (r == null) {
            target.sendMessage(ChatColor.RED + "[OUH] Du hast keine Teleport-Anfrage.");
            return;
        }

        Player sender = Bukkit.getPlayer(r.sender);
        if (sender != null && sender.isOnline()) {
            sender.sendMessage(ChatColor.RED + "[OUH] " + target.getName() + " hat deine Anfrage abgelehnt.");
        }
        target.sendMessage(ChatColor.YELLOW + "[OUH] Du hast die Teleport-Anfrage abgelehnt.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1 && (cmd.getName().equalsIgnoreCase("tpa") || cmd.getName().equalsIgnoreCase("tpahere"))) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> !name.equalsIgnoreCase(sender.getName()))
                    .toList();
        }
        return Collections.emptyList();
    }
}
