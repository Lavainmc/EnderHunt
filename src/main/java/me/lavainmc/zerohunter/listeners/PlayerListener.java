package me.lavainmc.zerohunter.listeners;

import fr.mrmicky.fastboard.FastBoard;
import me.lavainmc.zerohunter.ZeroHunter;
import me.lavainmc.zerohunter.managers.GameManager;
import me.lavainmc.zerohunter.managers.ScoreboardManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

    private final ZeroHunter plugin;
    private final GameManager gameManager;
    private final ScoreboardManager scoreboardManager;

    public PlayerListener(ZeroHunter plugin, GameManager gameManager, ScoreboardManager scoreboardManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.scoreboardManager = scoreboardManager;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Scoreboard
        FastBoard board = new FastBoard(player);
        board.updateTitle(ChatColor.LIGHT_PURPLE + "终末" + ChatColor.YELLOW + "猎人游戏");
        scoreboardManager.boards.put(player.getUniqueId(), board);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!gameManager.isGameRunning()) {
            return;
        }
        // Set player is death
        if (gameManager.getRespawnManager().isRespawning(player)) {
            gameManager.getRespawnManager().cancelRespawn(player);
        } else {
            // 如果玩家没有在重生，视为永久死亡
            gameManager.onPlayerDeath(player);
        }

        // Scoreboard
        FastBoard board = scoreboardManager.boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!gameManager.isGameRunning()) {
            return;
        }

        // Update compass
        Player player = event.getPlayer();
        if (System.currentTimeMillis() % 5000 < 50) {
            gameManager.updateCompassTargets();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!gameManager.isGameRunning()) {
            return;
        }

        if (gameManager.getHunters().contains(player)) {
            event.setRespawnLocation(player.getWorld().getSpawnLocation());
        }
    }

}
