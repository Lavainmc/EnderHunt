package me.lavainmc.zerohunter.managers;

import me.lavainmc.zerohunter.ZeroHunter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RespawnManager {
    
    private final ZeroHunter plugin;
    private final GameManager gameManager;
    private final KitManager kitManager;

    // Respawn Task
    private final Map<Player, BukkitRunnable> respawningPlayers;
    private final Map<Player, Integer> respawnTimes;

    private static final int RESPAWN_TIME = 120; // 2min
    
    public RespawnManager(ZeroHunter plugin, GameManager gameManager, KitManager kitManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.kitManager = kitManager;

        this.respawningPlayers = new ConcurrentHashMap<>();
        this.respawnTimes = new ConcurrentHashMap<>();
    }

    /**
     * 开始玩家重生过程
     */
    public void startRespawn(Player player) {

        if (!gameManager.isHunter(player)) {
            return;
        }

        setupSpectatorMode(player);
        
        // Create Task
        BukkitRunnable respawnTask = new BukkitRunnable() {
            int timeLeft = RESPAWN_TIME;
            
            @Override
            public void run() {
                if (!player.isOnline() || !gameManager.isGameRunning()) {
                    cancelRespawn(player);
                    return;
                }
                
                respawnTimes.put(player, timeLeft);
                
                // 每10秒或最后10秒发送提醒
                if (timeLeft <= 10 || timeLeft % 10 == 0) {
                    sendRespawnMessage(player, timeLeft);
                }
                
                // 播放音效（最后10秒）
                if (timeLeft <= 5) {
                    playCountdownEffect(player);
                }
                
                if (timeLeft <= 0) {
                    cancel();
                    respawningPlayers.remove(player);
                    respawnTimes.remove(player);
                    respawnPlayer(player);
                }
                
                timeLeft--;
            }
        };
        
        respawningPlayers.put(player, respawnTask);
        respawnTask.runTaskTimer(plugin, 0L, 20L); // 每秒执行一次
        
        // 发送初始消息
        player.sendTitle("§c☠ 你已死亡", "§7重生时间: §e" + RESPAWN_TIME + "秒", 10, 60, 20);
        player.sendMessage("§c你已死亡！将在 §e" + RESPAWN_TIME + "秒 §c后重生");
        player.sendMessage("§6使用 §f/manhunt respawninfo §6查看重生信息");
    }
    
    /**
     * 设置玩家为旁观模式
     */
    private void setupSpectatorMode(Player player) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SPECTATOR);

        if (gameManager.getSpeedrunner() != null && gameManager.getSpeedrunner().isOnline()) {
            Player speedrunner = gameManager.getSpeedrunner();
            player.teleport(speedrunner.getLocation().add(0, 5, 0));
        }
    }
    
    /**
     * 重生玩家
     */
    public void respawnPlayer(Player player) {
        if (!player.isOnline()) return;

        player.setGameMode(GameMode.SURVIVAL);

        Location respawnLocation = getHuntersSpawnLocation();
        player.teleport(respawnLocation);

        giveRespawnEquipment(player);

        player.sendTitle("§a重生完成", "§f你已重新加入战斗", 0, 20, 10);
        player.sendMessage("§a你已重生！继续追捕速通者吧！");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
        // 广播给其他玩家
        gameManager.broadcastMessage("§e猎人 §f" + player.getName() + " §e已重生并重新加入战斗");
        
        // 重新添加到追杀者列表（如果因为死亡被移除）
        if (!gameManager.getHunters().contains(player)) {
            gameManager.getHunters().add(player);
        }
        
        // 更新指南针目标
        gameManager.updateCompassTargets();
    }

    private Location getHuntersSpawnLocation() {
        // 直接使用GameManager提供的出生点位置
        return gameManager.getHuntersSpawnLocation();
    }
    
    /**
     * 给予重生装备
     */
    private void giveRespawnEquipment(Player player) {
        // 清空背包
        player.getInventory().clear();

        kitManager.giveRandomEquipment(player, false);

    }
    
    /**
     * 发送重生消息
     */
    private void sendRespawnMessage(Player player, int timeLeft) {
        if (timeLeft == RESPAWN_TIME) {
            player.sendMessage("§c你已死亡！重生时间: §e" + timeLeft + "秒");
        } else if (timeLeft <= 10) {
            player.sendMessage("§6重生倒计时: §c" + timeLeft + "秒");
            player.sendTitle("§e" + timeLeft, "秒 §f后重生", 0, 20, 0);
        } else if (timeLeft % 30 == 0 || timeLeft == 60) {
            player.sendMessage("§7重生时间剩余: §e" + formatTime(timeLeft));
        }
    }
    
    /**
     * 播放倒计时音效
     */
    private void playCountdownEffect(Player player) {
        if (player.isOnline()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.0f);
        }
    }
    
    /**
     * 格式化时间显示
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d分%02d秒", minutes, secs);
    }
    
    /**
     * 取消玩家的重生过程
     */
    public void cancelRespawn(Player player) {
        BukkitRunnable task = respawningPlayers.get(player);
        if (task != null) {
            task.cancel();
            respawningPlayers.remove(player);
        }
        respawnTimes.remove(player);
    }
    
    /**
     * 获取玩家的剩余重生时间
     */
    public int getRespawnTimeLeft(Player player) {
        return respawnTimes.getOrDefault(player, 0);
    }
    
    /**
     * 检查玩家是否正在重生
     */
    public boolean isRespawning(Player player) {
        return respawningPlayers.containsKey(player);
    }
    
    /**
     * 强制立即重生玩家（管理员命令）
     */
    public void forceRespawn(Player player) {
        if (isRespawning(player)) {
            cancelRespawn(player);
            respawnPlayer(player);
        }
    }
    
    /**
     * 游戏结束时清理所有重生任务
     */
    public void cleanupAllRespawns() {
        for (BukkitRunnable task : respawningPlayers.values()) {
            task.cancel();
        }
        respawningPlayers.clear();
        respawnTimes.clear();
    }
}