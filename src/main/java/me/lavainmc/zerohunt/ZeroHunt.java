package me.lavainmc.zerohunt;

import fr.mrmicky.fastboard.FastBoard;
import me.lavainmc.zerohunt.commands.Commands;
import me.lavainmc.zerohunt.listeners.EntityDeathListener;
import me.lavainmc.zerohunt.listeners.PlayerDeathListener;
import me.lavainmc.zerohunt.listeners.PlayerListener;
import me.lavainmc.zerohunt.listeners.PlayerRespawnListener;
import me.lavainmc.zerohunt.managers.*;
import me.lavainmc.zerohunt.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZeroHunt extends JavaPlugin {


    // Manager
    private GameManager gameManager;
    private Commands commandManager;
    private ScoreboardManager scoreboardManager;
    private WorldManager worldManager;
    private KitManager kitManager;
    private RespawnManager respawnManager;

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

        // Listener
        this.playerDeathListener = new PlayerDeathListener(this, gameManager, respawnManager);
        this.playerListener = new PlayerListener(this, gameManager, scoreboardManager);
        this.playerRespawnListener = new PlayerRespawnListener(this, gameManager);
        this.entityDeathListener = new EntityDeathListener(this, gameManager);
        // <Initialize

        // Do reset The End
        worldManager.initializeWorld();

        // Command reg
        getCommand("zerohunt").setExecutor(commandManager);

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

        // Info
        getLogger().info("ZeroHunt has Enabled!");
        getLogger().info("ZeroHunt by Lavainmc");
        getLogger().info("Thank you for using!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (gameManager.isGameRunning()) {
            gameManager.endGame();
            getLogger().warning("Plugin Disabling! Stopped All Games");
        }
        getLogger().info("ZeroHunt has Disabled!");
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
}
