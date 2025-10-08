package me.lavainmc.zerohunter.listeners;

import me.lavainmc.zerohunter.ZeroHunter;
import me.lavainmc.zerohunter.managers.GameManager;
import me.lavainmc.zerohunter.managers.RespawnManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final ZeroHunter plugin;
    private final GameManager gameManager;
    private final RespawnManager respawnManager;

    public PlayerDeathListener(ZeroHunter plugin, GameManager gameManager, RespawnManager respawnManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.respawnManager = respawnManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!gameManager.isGameRunning()) {
            return;
        }

        event.setDeathMessage(null);
        gameManager.onPlayerDeath(player);

        // Kill Message
        if (gameManager.isSpeedrunner(player)) {
            event.setDeathMessage(ChatColor.RED + "🏹 速通者 " + player.getName() + " 已死亡!");
        } else if (gameManager.isHunter(player)) {
            respawnManager.startRespawn(player);

            Player killer = player.getKiller();
            if (killer != null && gameManager.isSpeedrunner(killer)) {
                event.setDeathMessage(ChatColor.GREEN + "🗡 速通者击杀了猎人 " + player.getName());
            } else {
                event.setDeathMessage(ChatColor.GRAY + "☠ 猎人 " + player.getName() + " 已死亡!");
            }
        }
    }
}
