package me.lavainmc.enderhunt.managers;

import fr.mrmicky.fastboard.FastBoard;

import me.lavainmc.enderhunt.EnderHunt;

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

    private final EnderHunt plugin;
    private final GameManager gameManager;

    public ScoreboardManager(EnderHunt plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        FastBoard board = new FastBoard(player);

        board.updateTitle(plugin.getConfig().getString("scoreboard.title"));

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

        switch (gameManager.getGameState()) {
            case WAITING:
                board.updateLines(
                        plugin.getConfig().getStringList("scoreboard.on-waiting")
                );
                /*
                board.updateLines(
                        ChatColor.GRAY + "-----------------",
                        ChatColor.RED + "IN TESTING VERSION",
                        ChatColor.YELLOW + "⌚ 等待游戏开启...",
                        "",
                        ChatColor.GRAY + "-----------------",
                        ChatColor.YELLOW + "MC.LavaCube.Top"
                );
                 */
                break;
            case RUNNING:
                board.updateLines(
                        plugin.getConfig().getStringList("scoreboard.on-running")
                );
                /*
                board.updateLines(
                        ChatColor.GRAY + "-----------------",
                        ChatColor.RED + "IN TESTING VERSION",
                        ChatColor.YELLOW + "⛏ 速通者: " + ChatColor.WHITE + gameManager.getSpeedrunner().getName(),
                        ChatColor.YELLOW + "🏹 猎人: " + ChatColor.WHITE + gameManager.getHunters().size(),
                        "",
                        ChatColor.GRAY + "-----------------",
                        ChatColor.YELLOW + "MC.LavaCube.Top"
                );
                 */
                break;
            case ENDED:
                board.updateLines(
                        plugin.getConfig().getStringList("scoreboard.on-ended")
                );
                /*
                board.updateLines(
                        ChatColor.GRAY + "-----------------",
                        ChatColor.RED + "IN TESTING VERSION",
                        ChatColor.YELLOW + "游戏结束!",
                        ChatColor.GRAY + "-----------------",
                        ChatColor.YELLOW + "MC.LavaCube.Top"
                );
                 */
                break;
            default:
                board.updateLines(
                        ChatColor.GRAY + "-----------------",
                        ChatColor.RED + "IN TESTING VERSION",
                        ChatColor.RED + "[错误] 插件空闲或加载失败",
                        ChatColor.GRAY + "-----------------",
                        ChatColor.YELLOW + "MC.LavaCube.Top"
                );
                break;
        }
    }
}
