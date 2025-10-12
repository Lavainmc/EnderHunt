package me.lavainmc.enderhunt.listeners;

import me.lavainmc.enderhunt.EnderHunt;
import me.lavainmc.enderhunt.managers.GameManager;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    private final EnderHunt plugin;
    private final GameManager gameManager;

    public EntityDeathListener(EnderHunt plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }


    @EventHandler
    public void onEnderDragonDeath(EntityDeathEvent event) {

        if (!(event.getEntity() instanceof EnderDragon)) {
            return;
        }

        if (!gameManager.isGameRunning()) {
            return;
        }

        gameManager.onSpeedrunnerWin();
    }
}
