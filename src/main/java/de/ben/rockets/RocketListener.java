package de.ben.rockets;

import de.ben.smp;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.UUID;

public class RocketListener implements Listener {

    private final smp plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public RocketListener(smp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRocketUse(PlayerInteractEvent e) {

        if (e.getHand() != EquipmentSlot.HAND) return;

        Player p = e.getPlayer();

        if (plugin.getConfig().getBoolean("rockets-enabled")) return;

        if (p.getInventory().getItemInMainHand().getType() != Material.FIREWORK_ROCKET) return;
        if (!p.isGliding()) return;

        e.setCancelled(true);

        long now = System.currentTimeMillis();
        int cooldownMs = plugin.getConfig().getInt("cooldown-seconds") * 1000;
        UUID id = p.getUniqueId();

        if (cooldowns.containsKey(id) && now - cooldowns.get(id) < cooldownMs) return;

        cooldowns.put(id, now);
        p.sendMessage(ChatColor.RED + "Rockets sind derzeit deaktiviert!");
    }
}
