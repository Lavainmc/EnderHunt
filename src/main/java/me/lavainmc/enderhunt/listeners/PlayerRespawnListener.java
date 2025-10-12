package me.lavainmc.enderhunt.listeners;

import me.lavainmc.enderhunt.EnderHunt;
import me.lavainmc.enderhunt.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    private final EnderHunt plugin;
    private final GameManager gameManager;

    public PlayerRespawnListener(EnderHunt plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!gameManager.isGameRunning()) {
            return;
        }

        if (gameManager.getHunters().contains(player)) {
            // Respawn in The End
            event.setRespawnLocation(player.getWorld().getSpawnLocation());
        }
    }
}
