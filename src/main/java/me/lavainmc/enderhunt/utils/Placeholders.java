package me.lavainmc.enderhunt.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lavainmc.enderhunt.EnderHunt;
import me.lavainmc.enderhunt.managers.GameManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {

    private final EnderHunt plugin;
    private final GameManager gameManager;

    public Placeholders(EnderHunt plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors()); //
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "example";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion(); //
    }

    @Override
    public boolean persist() {
        return true; //
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("hunters")) {
            return String.valueOf(gameManager.getHunters().size());
        }
        if (params.equalsIgnoreCase("speedrunner")) {
            return gameManager.getSpeedrunner().getName();
        }
        if (params.equalsIgnoreCase("hunters_name")) {
            return gameManager.getHunters().toString();
        }
        return null;
    }
}
