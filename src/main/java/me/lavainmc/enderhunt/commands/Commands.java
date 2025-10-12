package me.lavainmc.enderhunt.commands;

import me.lavainmc.enderhunt.EnderHunt;
import me.lavainmc.enderhunt.managers.GameManager;
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
    private final EnderHunt plugin;
    private final GameManager gameManager;

    public Commands(EnderHunt plugin) {
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
            case "reset":
                handleResetCommand(sender);
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
        if (!sender.hasPermission("enderhunt.command.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限使用此命令!");
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
        if (!sender.hasPermission("enderhunter.admin")) {
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

    private void help(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== EnderHunt 命令帮助 ===");
        sender.sendMessage(ChatColor.YELLOW + "/enderhunt start - 开始游戏");
        sender.sendMessage(ChatColor.YELLOW + "/enderhunt stop - 结束游戏");
        sender.sendMessage(ChatColor.YELLOW + "/enderhunt status - 查看游戏状态");
        sender.sendMessage(ChatColor.YELLOW + "/enderhunt reset - 重置末地世界(admin权限)");
    }
}
