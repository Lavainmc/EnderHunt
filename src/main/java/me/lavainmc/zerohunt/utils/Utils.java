package me.lavainmc.zerohunt.utils;

import me.lavainmc.zerohunt.ZeroHunt;
import net.md_5.bungee.api.ChatColor;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public Utils(ZeroHunt zeroHunt) {
    }

    public static String chat(String s) {
        String result = s;
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        result = result.replace("&#", "#");
        Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            String color = result.substring(matcher.start(), matcher.end());
            result = result.replace(color, String.valueOf(ChatColor.of(color)));
            matcher = pattern.matcher(result);
        }

        return org.bukkit.ChatColor.translateAlternateColorCodes('&', result);
    }

    public static int getRandomInt(int i) {
        return new Random().nextInt(i) + 1;
    }

    public static int getRandomInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}