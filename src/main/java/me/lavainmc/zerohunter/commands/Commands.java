package me.lavainmc.zerohunter.commands;

import me.lavainmc.zerohunter.ZeroHunter;
import me.lavainmc.zerohunter.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor {

    String ErrorPrefix = "§c[错误] ";
    private final ZeroHunter plugin;
    private final GameManager gameManager;

    public Commands(ZeroHunter plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            help(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                handleStartCommand(sender, args);
                break;
            case "stop":
                handleStopCommand(sender);
                break;
            case "status":
                handleStatusCommand(sender);
                break;
            case "setrole":
                handleSetRoleCommand(sender, args);
                break;
            case "reset":
                handleResetCommand(sender);
                break;
            case "givekit":
                handleTestKitCommand(sender, args);
                break;
            default:
                help(sender);
                break;
        }

        return true;
    }

    private void handleStartCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家可以执行此命令!");
            return;
        }

        Player player = (Player) sender;

        if (gameManager.isGameRunning()) {
            player.sendMessage(ChatColor.RED + "游戏正在进行中!");
            return;
        }

        // 自动选择速通者和追杀者
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (onlinePlayers.size() < 2) {
            player.sendMessage(ChatColor.RED + "需要至少2名玩家才能开始游戏!");
            return;
        }

        // 随机选择速通者
        Player speedrunner = onlinePlayers.get(0);
        List<Player> hunters = new ArrayList<>(onlinePlayers);
        hunters.remove(speedrunner);

        if (gameManager.startGame(speedrunner, hunters)) {
            Bukkit.broadcastMessage(ChatColor.RED + "*已执行 游戏开始");
        } else {
            player.sendMessage(ChatColor.RED + ErrorPrefix + "无法开始游戏!");
        }
    }

    private void handleStopCommand(CommandSender sender) {
        if (gameManager.isGameRunning()) {
            gameManager.endGame();
            sender.sendMessage(ChatColor.GREEN + "游戏已结束!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "当前没有进行中的游戏!");
        }
    }

    private void handleStatusCommand(CommandSender sender) {
        if (!gameManager.isGameRunning()) {
            sender.sendMessage(ChatColor.YELLOW + "当前没有进行中的游戏!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "游戏状态: 进行中");
        sender.sendMessage(ChatColor.YELLOW + "速通者: " + gameManager.getSpeedrunner().getName());
        sender.sendMessage(ChatColor.RED + "追杀者: " + gameManager.getHunters().size() + "人");
    }

    private void handleSetRoleCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zerohunter.command.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限使用此命令!");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "用法: /zerohunter setrole <player> <speedrunner|hunter>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "玩家未找到!");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "角色设置功能开发中...");
    }

    private void handleResetCommand(CommandSender sender) {
        if (!sender.hasPermission("zerohunter.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限使用此命令!");
            return;
        }

        if (gameManager.isGameRunning()) {
            sender.sendMessage(ChatColor.RED + ErrorPrefix + "游戏进行中无法重置世界!");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "正在重置末地世界...");
        plugin.getWorldManager().initializeWorld();
    }

    private void handleTestKitCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家可以执行此命令!");
            return;
        }

        if (!sender.hasPermission("manhunt.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限使用此命令!");
            return;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "用法: /zerohunter givekit <speedrunner|hunter>");
            return;
        }

        boolean isSpeedrunner = args[1].equalsIgnoreCase("speedrunner");
        plugin.getKitManager().giveRandomEquipment(player, isSpeedrunner);

        String role = isSpeedrunner ? "速通者" : "猎人";
        player.sendMessage(ChatColor.GREEN + "已获得随机" + role + "装备!");
    }

    private void help(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== ManHunt 命令帮助 ===");
        sender.sendMessage(ChatColor.YELLOW + "/zerohunter start - 开始游戏");
        sender.sendMessage(ChatColor.YELLOW + "/zerohunter stop - 结束游戏");
        sender.sendMessage(ChatColor.YELLOW + "/zerohunter status - 查看游戏状态");
        sender.sendMessage(ChatColor.YELLOW + "/zerohunter setrole <player> <role> - 设置玩家身份(需要权限)");
        sender.sendMessage(ChatColor.YELLOW + "/zerohunter reset - 重置末地世界(需要权限)");
        sender.sendMessage(ChatColor.YELLOW + "/zerohunter givekit <身份> - 获取对应身份的装备");
    }
}
