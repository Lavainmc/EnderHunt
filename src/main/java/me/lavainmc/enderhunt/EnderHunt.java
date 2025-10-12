package me.lavainmc.enderhunt;

import fr.mrmicky.fastboard.FastBoard;
import me.lavainmc.enderhunt.commands.Commands;
import me.lavainmc.enderhunt.listeners.EntityDeathListener;
import me.lavainmc.enderhunt.listeners.PlayerDeathListener;
import me.lavainmc.enderhunt.listeners.PlayerListener;
import me.lavainmc.enderhunt.listeners.PlayerRespawnListener;
import me.lavainmc.enderhunt.managers.*;
import me.lavainmc.enderhunt.utils.Placeholders;
import me.lavainmc.enderhunt.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class EnderHunt extends JavaPlugin {

    // Manager
    private GameManager gameManager;
    private Commands commandManager;
    private ScoreboardManager scoreboardManager;
    private WorldManager worldManager;
    private KitManager kitManager;
    private RespawnManager respawnManager;
    private Placeholders placeholders;

    // Listener
    private PlayerDeathListener playerDeathListener;
    private PlayerRespawnListener playerRespawnListener;
    private PlayerListener playerListener;
    private EntityDeathListener entityDeathListener;

    // Utils
    private Utils utils;

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Initialize >
        // Manager & Utils
        this.gameManager = new GameManager(this);
        this.commandManager = new Commands(this);
        this.worldManager = new WorldManager(this);
        this.scoreboardManager = new ScoreboardManager(this, gameManager);
        this.kitManager = new KitManager(this);
        this.respawnManager = new RespawnManager(this, gameManager, kitManager);
        this.utils = new Utils(this);
        this.placeholders = new Placeholders(this, gameManager);

        // Listener
        this.playerDeathListener = new PlayerDeathListener(this, gameManager, respawnManager);
        this.playerListener = new PlayerListener(this, gameManager, scoreboardManager);
        this.playerRespawnListener = new PlayerRespawnListener(this, gameManager);
        this.entityDeathListener = new EntityDeathListener(this, gameManager);
        // <Initialize

        // Do reset The End
        worldManager.initializeWorld();

        // Command reg
        getCommand("enderhunt").setExecutor(commandManager);

        // Listener reg
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(playerDeathListener, this);
        getServer().getPluginManager().registerEvents(playerRespawnListener, this);
        getServer().getPluginManager().registerEvents(entityDeathListener, this);

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (FastBoard board : scoreboardManager.boards.values()) {
                scoreboardManager.updateBoard(board);
            }
        }, 0, 20);

        // Placeholder Reg
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new Placeholders(this, gameManager).register(); //
        }

        // File
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        generateFile("config.yml");

        // Info
        getLogger().info("EnderHunt has Enabled!");
        getLogger().info("EnderHunt by Lavainmc");
        getLogger().info("Thank you for using!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (gameManager.isGameRunning()) {
            gameManager.endGame();
            getLogger().warning("Plugin Disabling! Stopped All Games");
        }
        getLogger().info("EnderHunt has Disabled!");
    }


    // File
    private void generateFile(String fileName) {
        File file = new File(getDataFolder(), fileName);

        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                // 复制文件
                InputStream inputStream = getResource(fileName);
                if (inputStream != null) {
                    Files.copy(inputStream, file.toPath());
                } else {
                    file.createNewFile();
                }
                if (fileName.equals("config.yml")) {
                    if (!getConfig().getStringList("config-version").contains("1.0")) {
                        if (inputStream != null) {
                            Files.copy(inputStream, file.toPath());
                        }
                    }
                }
            } catch (IOException e) {
                getLogger().severe("无法创建文件 " + fileName + ": " + e.getMessage());
            }
        }
    }

    // Getters
    public GameManager getGameManager() {
        return gameManager;
    }
    public WorldManager getWorldManager() {
        return worldManager;
    }
    public KitManager getKitManager() {
        return kitManager;
    }
    public Utils getUtils() {
        return utils;
    }
    public Placeholders getPlaceholders() {
        return placeholders;
    }
}
