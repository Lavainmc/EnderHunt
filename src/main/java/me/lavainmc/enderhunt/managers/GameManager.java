package me.lavainmc.enderhunt.managers;

import me.lavainmc.enderhunt.EnderHunt;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager {

    private final EnderHunt plugin;
    private RespawnManager respawnManager;
    private WorldManager worldManager;

    private GameState gameState;
    private Player speedrunner;
    private List<Player> hunters;
    private List<Player> allPlayers;

    private World endWorld;
    private Location lobbyLocation;

    public GameManager(EnderHunt plugin) {
        this.plugin = plugin;
        this.respawnManager = new RespawnManager(plugin, this, new KitManager(plugin));
        this.worldManager = new WorldManager(plugin);

        this.gameState = GameState.WAITING;
        this.hunters = new ArrayList<>();
        this.allPlayers = new ArrayList<>();
    }

    public enum GameState {
        WAITING, STARTING, RUNNING, ENDED
    }

    public boolean startGame(Player speedrunner, List<Player> hunters) {
        if (gameState != GameState.WAITING) {
            return false;
        }

        this.speedrunner = speedrunner;
        this.hunters.addAll(hunters);
        this.allPlayers.add(speedrunner);
        this.allPlayers.addAll(hunters);

        // Set GameState
        gameState = GameState.STARTING;

        // Game Begin
        teleportPlayersToEnd(); // Players gamemode and Properties
        giveStarterItems();

        new BukkitRunnable() {
            @Override
            public void run() {
                gameState = GameState.RUNNING;
                Bukkit.broadcastMessage(ChatColor.GOLD + "=== ⚔ 猎人游戏 游戏开始 ⚔ ===");
                Bukkit.broadcastMessage(ChatColor.GREEN + "⛏ 速通者: §f" + speedrunner.getName());
                Bukkit.broadcastMessage(ChatColor.RED + "🗡 追杀者: §f" + getHunters());
                setHunterCompass();
            }
        }.runTaskLater(plugin, 100L);
        return true;
    }


    public List<Player> getAllHunters() {
        return new ArrayList<>(hunters);
    }

    public void endGame() {
        if (gameState != GameState.RUNNING) {
            return;
        }

        gameState = GameState.ENDED;

        // Clean Data
        respawnManager.respawnPlayer((Player) getAllHunters());
        respawnManager.cleanupAllRespawns();
        speedrunner = null;
        hunters.clear();
        allPlayers.clear();
        worldManager.initializeWorld();

        new BukkitRunnable() {
            @Override
            public void run() {
                gameState = GameState.WAITING;
            }
        }.runTaskLater(plugin, 200L);
    }

    private void teleportPlayersToEnd() {
        // 使用WorldManager获取末地世界
        endWorld = plugin.getWorldManager().getEndWorld();

        if (endWorld == null) {
            plugin.getLogger().warning("末地世界未找到!");
            return;
        }

        Location centerLocation = endWorld.getSpawnLocation();

        Location speedrunnerSpawn = centerLocation.clone().add(20, 0, 20);
        speedrunner.teleport(speedrunnerSpawn);
        speedrunner.setGameMode(GameMode.SURVIVAL);
        speedrunner.setHealth(20.0);
        speedrunner.setFoodLevel(20);
        speedrunner.sendMessage("§a⛏ §a§l你是速通者! 目标: 击杀末影龙!");

        Location huntersSpawn = centerLocation.clone().add(-20, 0, -20);
        for (Player hunter : hunters) {
            hunter.teleport(huntersSpawn);
            hunter.setGameMode(GameMode.SURVIVAL);
            hunter.setHealth(20.0);
            hunter.setFoodLevel(20);
            hunter.sendMessage("§c🏹 §c§l你是猎人! 目标: 消灭速通者!");
        }

    }

    public Location getHuntersSpawnLocation() {
        if (endWorld == null) {
            endWorld = plugin.getWorldManager().getEndWorld();
        }

        Location centerLocation = endWorld.getSpawnLocation();
        return centerLocation.clone().add(-20, 0, -20);
    }

    //  Set Kit
    private void giveStarterItems() {
        plugin.getKitManager().giveRandomEquipment(speedrunner, true);

        for (Player hunter : hunters) {
            plugin.getKitManager().giveRandomEquipment(hunter, false);
        }

        speedrunner.getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE));
        speedrunner.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
        speedrunner.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
        speedrunner.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 64));
        speedrunner.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 64));
        speedrunner.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
        speedrunner.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
        speedrunner.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
        speedrunner.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 16));
        speedrunner.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 64));
        for (Player hunter : hunters) {
            hunter.getInventory().addItem(new ItemStack(Material.COMPASS));
            hunter.getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE));
            hunter.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
            hunter.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
            hunter.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 64));
            hunter.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 64));
            hunter.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
            hunter.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
            hunter.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
            hunter.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 32));
            hunter.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 64));
        }
        setHunterCompass();
    }

    private void setHunterCompass() {
        for (Player hunter : hunters) {
            hunter.setCompassTarget(speedrunner.getLocation());
        }
    }

    public void updateCompassTargets() {
        if (gameState == GameState.RUNNING && speedrunner != null) {
            for (Player hunter : hunters) {
                hunter.setCompassTarget(speedrunner.getLocation());
            }
        }
    }

    public void onPlayerDeath(Player player) {
        if (gameState != GameState.RUNNING) return;

        if (player.equals(speedrunner)) {
            onHunterWin();
            return;
        }
        if (player.equals(hunters)) {

            // 广播死亡消息
            Player killer = player.getKiller();
            if (killer != null && isSpeedrunner(killer)) {
                broadcastMessage("§e☠ 速通者反杀了猎人 §f" + player.getName() + "§e! 猎人将在两分钟后复活.");
            } else {
                broadcastMessage("§e☠ 猎人 §f" + player.getName() + " §e被淘汰了，将在两分钟后复活.");
            }
            checkHunterStatus();
        }
    }

    private void checkHunterStatus() {
        boolean anyHunterAlive = hunters.stream().anyMatch(Player::isOnline);
        boolean anyHunterRespawning = getAllHunters().stream()
                .anyMatch(hunter -> respawnManager.isRespawning(hunter));

        if (!anyHunterAlive && !anyHunterRespawning) {
            broadcastMessage("§e所有猎人都被淘汰了，等待复活...");
        }
    }

    public void onHunterWin() {
        playHunterVictoryEffects();
        broadcastMessage(ChatColor.RED + "🏹 猎人消灭了所有速通者，获得胜利!");
        new BukkitRunnable() {
            @Override
            public void run() {
                endGame();
            }
        }.runTaskLater(plugin, 200L);
    }

    public void onSpeedrunnerWin() {
        playSpeedrunnerVictoryEffects();
        broadcastMessage(ChatColor.GREEN + "⛏ 速通者 " + speedrunner.getName() + " 击杀了末影龙，获得胜利!");

        new BukkitRunnable() {
            @Override
            public void run() {
                endGame();
            }
        }.runTaskLater(plugin, 200L);
    }

    /*
    =================EFFECT==================
     */

    /**
     * 播放追杀者胜利效果
     */
    private void playHunterVictoryEffects() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isHunter(player)) {
                HunterWinEffects(player);
            } else if (isSpeedrunner(player)) {
                SpeedrunnerLoseEffects(player);
            } else {
                SpectatorHunterWinEffects(player);
            }
        }
    }

    /**
     * 为追杀者播放胜利效果
     */
    private void HunterWinEffects(Player hunter) {
        hunter.sendTitle(
                "§c§l胜利!",
                "§c追杀者 §f 团队获得胜利",
                10,
                60,
                20
        );
        new BukkitRunnable() {
            @Override
            public void run() {
                hunter.playSound(hunter.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                hunter.playSound(speedrunner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.3f);
            }
        };

        hunter.sendMessage("§7=================================");
        hunter.sendMessage("§6             胜利！");
        hunter.sendMessage("§e         成功杀死速通者");
        hunter.sendMessage("§7=================================");
    }

    /**
     * 为速通者播放失败效果
     */
    private void SpeedrunnerLoseEffects(Player speedrunner) {
        // 失败Title
        speedrunner.sendTitle(
                "§c§l失败",
                "§8你被猎人杀死",
                10,
                60,
                20
        );

        speedrunner.playSound(speedrunner.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.5f);
        speedrunner.playSound(speedrunner.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.8f, 1.0f);

        speedrunner.sendMessage("§7=================================");
        speedrunner.sendMessage("§c            游戏结束");
        speedrunner.sendMessage("§f      你被追杀者团队击败了");
        speedrunner.sendMessage("§7=================================");
    }

    /**
     * 旁观者 - 效果
     */
    private void SpectatorHunterWinEffects(Player spectator) {
        spectator.sendTitle(
                "§e§l游戏结束",
                "§c猎人 §f获得胜利",
                10, 60, 20
        );

        spectator.playSound(spectator.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 0.7f, 0.8f);
    }

    /**
     * 速通者 - 胜利效果
     */
    private void playSpeedrunnerVictoryEffects() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isSpeedrunner(player)) {
                playSpeedrunnerWinEffects(player);
            } else if (isHunter(player)) {
                playHunterLoseEffects(player);
            } else {
                playSpectatorSpeedrunnerWinEffects(player);
            }
        }
    }

    /**
     * 速通者 - 胜利效果
     */
    private void playSpeedrunnerWinEffects(Player speedrunner) {
        // 胜利Title
        speedrunner.sendTitle(
                "§a§l胜利",
                "§2成功击败末影龙",
                10, 60, 20
        );

        // 胜利音效
        speedrunner.playSound(speedrunner.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f);
        speedrunner.playSound(speedrunner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.3f);
        speedrunner.playSound(speedrunner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

        // 发送个人祝贺消息
        speedrunner.sendMessage("§7=================================");
        speedrunner.sendMessage("§6§l             胜利！");
        speedrunner.sendMessage("§e   你在追杀者的围捕下击败了末影龙");
        speedrunner.sendMessage("§f       真正的速通大师！");
        speedrunner.sendMessage("§7=================================");
    }

    /**
     * 猎人 - 失败效果
     */
    private void playHunterLoseEffects(Player hunter) {
        hunter.sendTitle(
                "§c§l失败",
                "§7速通者击败了末影龙",
                10, 60, 20
        );

        hunter.playSound(hunter.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 0.8f, 0.5f);

        hunter.sendMessage("§7=================================");
        hunter.sendMessage("§c              失败");
        hunter.sendMessage("§f      速通者成功击败了末影龙");
        hunter.sendMessage("§7=================================");
    }

    /**
     * 旁观者 - 速通者胜利效果
     */
    private void playSpectatorSpeedrunnerWinEffects(Player spectator) {
        spectator.sendTitle(
                "§e§l游戏结束",
                "§a速通者 §f取得胜利",
                10, 60, 20
        );

        spectator.playSound(spectator.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 0.7f, 1.0f);
    }

    public void broadcastMessage(String message) {
        for (Player player : allPlayers) {
            player.sendMessage(message);
        }
        Bukkit.getConsoleSender().sendMessage(message);
    }

    // Getters
    public boolean isGameRunning() {
        return gameState == GameState.RUNNING;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Player getSpeedrunner() {
        return speedrunner;
    }

    public List<Player> getHunters() {
        return hunters;
    }

    public boolean isSpeedrunner(Player player) {
        return player.equals(speedrunner);
    }

    public boolean isHunter(Player player) {
        return hunters.contains(player);
    }

    public RespawnManager getRespawnManager() {
        return respawnManager;
    }
}