package me.lavainmc.zerohunter.managers;

import me.lavainmc.zerohunter.ZeroHunter;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class WorldManager {

    private final ZeroHunter plugin;
    private World endWorld;
    private static final String WORLD_NAME = "manhunt_end";

    public WorldManager(ZeroHunter plugin) {
        this.plugin = plugin;
    }

    /**
     * 初始化末地世界
     */
    public void initializeWorld() {
        plugin.getLogger().info("正在准备末地世界...");

        deleteWorld();

        createNewWorld();

        plugin.getLogger().info("末地世界准备完成!");
    }

    /**
     * 删除末地世界
     */
    private void deleteWorld() {
        World existingWorld = Bukkit.getWorld(WORLD_NAME);
        if (existingWorld != null) {

            teleportPlayersToOverworld(existingWorld);

            if (!Bukkit.unloadWorld(existingWorld, false)) {
                plugin.getLogger().warning("无法卸载世界: " + WORLD_NAME);
                return;
            }
        }

        // 删除世界文件夹
        File worldFolder = new File(Bukkit.getWorldContainer(), WORLD_NAME);
        if (worldFolder.exists()) {
            deleteFolder(worldFolder);
            plugin.getLogger().info("已删除世界文件夹: " + WORLD_NAME);
        }

        endWorld = null;
    }

    /**
     * 将玩家传送到主世界
     */
    private void teleportPlayersToOverworld(World worldToUnload) {
        World overworld = Bukkit.getWorlds().get(0);
        if (overworld == null) return;

        for (Player player : worldToUnload.getPlayers()) {
            player.teleport(overworld.getSpawnLocation());
            player.sendMessage(ChatColor.YELLOW + "你已被传送到主世界，末地世界正在重置...");
        }
    }

    /**
     * 创建新的末地世界 - 修复生成问题
     */
    private void createNewWorld() {
        try {
            WorldCreator creator = new WorldCreator(WORLD_NAME);
            creator.environment(Environment.THE_END);
            creator.type(WorldType.NORMAL);

            creator.generateStructures(true);
            creator.generator((ChunkGenerator) null);

            endWorld = creator.createWorld();

            if (endWorld != null) {
                setupWorldProperties(endWorld);
                plugin.getLogger().info("成功创建末地世界: " + WORLD_NAME);
            } else {
                plugin.getLogger().severe("创建末地世界失败!");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("创建末地世界时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 设置世界属性
     */
    private void setupWorldProperties(World world) {
        // 设置游戏规则
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.NATURAL_REGENERATION, true);

        // 设置固定时间
        world.setTime(6000);
        world.setStorm(false);
        world.setThundering(false);
    }

    /**
     * 递归删除文件夹
     */
    private void deleteFolder(File folder) {
        if (!folder.exists()) return;

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    /**
     * 获取末地世界
     */
    public World getEndWorld() {
        if (endWorld == null) {
            endWorld = Bukkit.getWorld(WORLD_NAME);
        }
        return endWorld;
    }

    /**
     * 检查末地世界是否已加载
     */
    public boolean isWorldLoaded() {
        return getEndWorld() != null;
    }

    /**
     * 重置末地世界
     */
    public void resetWorld() {
        plugin.getLogger().info("重置末地世界...");

        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "🔄 正在重置末地世界...");

        new BukkitRunnable() {
            @Override
            public void run() {
                deleteWorld();
                createNewWorld();
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "✅ 末地世界重置完成!");
            }
        }.runTaskAsynchronously(plugin);
    }
}