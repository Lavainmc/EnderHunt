package me.lavainmc.zerohunt.managers;

import fr.mrmicky.fastboard.FastBoard;

import me.lavainmc.zerohunt.ZeroHunt;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ScoreboardManager{

    public final Map<UUID, FastBoard> boards = new HashMap<>();

    private final ZeroHunt plugin;
    private final GameManager gameManager;

    public ScoreboardManager(ZeroHunt plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        FastBoard board = new FastBoard(player);

        board.updateTitle(ChatColor.LIGHT_PURPLE + "终末 " + ChatColor.YELLOW + "猎人游戏");

        this.boards.put(player.getUniqueId(), board);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        FastBoard board = this.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }

    public void updateBoard(FastBoard board) {

        if (gameManager.getGameState().equals(GameManager.GameState.RUNNING)) {
            board.updateLines(
                    ChatColor.GRAY + "-----------------",
                    ChatColor.RED + "IN TESTING VERSION",
                    ChatColor.YELLOW + "⛏ 速通者: " + ChatColor.WHITE + gameManager.getSpeedrunner().getName(),
                    ChatColor.YELLOW + "🏹 猎人: " + ChatColor.WHITE + gameManager.getHunters().size(),
                    "",
                    ChatColor.GRAY + "-----------------",
                    ChatColor.YELLOW + "MC.LavaCube.Top"
            );
        } else {
            board.updateLines(
                    ChatColor.GRAY + "-----------------",
                    ChatColor.RED + "IN TESTING VERSION",
                    ChatColor.YELLOW + "🗡 游戏未开启",
                    "",
                    ChatColor.GRAY + "-----------------",
                    ChatColor.YELLOW + "MC.LavaCube.Top"
            );
        }
    }
}
