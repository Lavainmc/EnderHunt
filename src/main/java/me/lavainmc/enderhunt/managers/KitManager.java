package me.lavainmc.enderhunt.managers;

import me.lavainmc.enderhunt.EnderHunt;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class KitManager {
    
    private final EnderHunt plugin;
    private final Random random;
    
    // 速通者装备池 - 更好的装备
    private final List<EquipmentSet> speedrunnerEquipmentPool;
    // 追杀者装备池 - 较差的装备
    private final List<EquipmentSet> hunterEquipmentPool;
    
    public KitManager(EnderHunt plugin) {
        this.plugin = plugin;
        this.random = ThreadLocalRandom.current();
        this.speedrunnerEquipmentPool = createSpeedrunnerEquipmentPool();
        this.hunterEquipmentPool = createHunterEquipmentPool();
    }
    
    /**
     * 为玩家分配随机装备
     */
    public void giveRandomEquipment(Player player, boolean isSpeedrunner) {
        clearPlayerInventory(player);
        
        List<EquipmentSet> pool = isSpeedrunner ? speedrunnerEquipmentPool : hunterEquipmentPool;
        EquipmentSet equipmentSet = pool.get(random.nextInt(pool.size()));
        
        equipmentSet.applyToPlayer(player);
        
        String role = isSpeedrunner ? "速通者" : "追杀者";
        player.sendMessage("§a你获得了 " + equipmentSet.getName() + " §a套装 (" + role + ")");
    }
    
    /**
     * 清空玩家背包
     */
    private void clearPlayerInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(5.0f);
        player.getActivePotionEffects().forEach(effect -> 
            player.removePotionEffect(effect.getType()));
    }
    
    /**
     * 创建速通者装备池
     */
    private List<EquipmentSet> createSpeedrunnerEquipmentPool() {
        List<EquipmentSet> pool = new ArrayList<>();
        
        // 套装1:
        pool.add(new EquipmentSet("§e#1", Arrays.asList(
            createEnchantedItem(Material.DIAMOND_SWORD, 1, 
                new EnchantmentData(Enchantment.SHARPNESS, 2),
                new EnchantmentData(Enchantment.UNBREAKING, 2)),
            createEnchantedItem(Material.BOW, 1,
                new EnchantmentData(Enchantment.POWER, 2)),
                createItem(Material.ENDER_PEARL, 4),
                createItem(Material.COBWEB, 8),
                createItem(Material.ARROW, 32),
            createItem(Material.GOLDEN_APPLE, 4),
            createItem(Material.COOKED_BEEF, 32)
        ), Arrays.asList(
            createEnchantedItem(Material.DIAMOND_HELMET, 1,
                new EnchantmentData(Enchantment.PROTECTION, 1)),
            createEnchantedItem(Material.IRON_CHESTPLATE, 1,
                new EnchantmentData(Enchantment.PROTECTION, 2)),
            createEnchantedItem(Material.DIAMOND_LEGGINGS, 1,
                new EnchantmentData(Enchantment.PROTECTION, 1)),
            createItem(Material.NETHERITE_BOOTS, 1)
        )));
        
        // 套装2:
        pool.add(new EquipmentSet("§e#2", Arrays.asList(
            createEnchantedItem(Material.IRON_SWORD, 1,
                new EnchantmentData(Enchantment.SHARPNESS, 2),
                new EnchantmentData(Enchantment.FIRE_ASPECT, 1)),
            createEnchantedItem(Material.BOW, 1,
                new EnchantmentData(Enchantment.POWER, 3)),
                createItem(Material.ARROW, 16),
            createItem(Material.ENDER_PEARL, 8),
            createItem(Material.GOLDEN_APPLE, 5),
                createItem(Material.COOKED_BEEF, 12)
        ), Arrays.asList(
            createEnchantedItem(Material.IRON_HELMET, 1,
                new EnchantmentData(Enchantment.PROTECTION, 3)),
            createEnchantedItem(Material.DIAMOND_CHESTPLATE, 1,
                new EnchantmentData(Enchantment.PROTECTION, 2)),
            createEnchantedItem(Material.DIAMOND_LEGGINGS, 1,
                new EnchantmentData(Enchantment.PROTECTION, 2)),
            createEnchantedItem(Material.IRON_BOOTS, 1,
                new EnchantmentData(Enchantment.FEATHER_FALLING, 2))
        )));

        pool.add(new EquipmentSet("§e#3", Arrays.asList(
            createItem(Material.NETHERITE_SWORD, 1),
            createItem(Material.ENDER_PEARL, 8),
                createItem(Material.GOLDEN_APPLE, 2),
                createItem(Material.COOKED_BEEF, 20),
                createItem(Material.COBWEB, 8)
        ), Arrays.asList(
            createEnchantedItem(Material.DIAMOND_HELMET, 1,
                new EnchantmentData(Enchantment.PROTECTION, 2)),
            createEnchantedItem(Material.DIAMOND_CHESTPLATE, 1,
                new EnchantmentData(Enchantment.PROTECTION, 2)),
            createEnchantedItem(Material.IRON_LEGGINGS, 1,
                new EnchantmentData(Enchantment.PROTECTION, 3)),
            createEnchantedItem(Material.DIAMOND_BOOTS, 1,
                new EnchantmentData(Enchantment.PROTECTION, 2)
            )
        )));

        return pool;
    }
    
    /**
     * 创建追杀者装备池
     */
    private List<EquipmentSet> createHunterEquipmentPool() {
        List<EquipmentSet> pool = new ArrayList<>();
        
        // 套装1: 基础
        pool.add(new EquipmentSet("§b#1", Arrays.asList(
            createEnchantedItem(Material.DIAMOND_SWORD, 1,
                new EnchantmentData(Enchantment.SHARPNESS, 1)),
            createItem(Material.IRON_AXE, 1),
            createItem(Material.BOW, 1),
            createItem(Material.ARROW, 12),
                createItem(Material.GOLDEN_APPLE, 4),
                createItem(Material.COOKED_BEEF, 20),
                createItem(Material.ENDER_PEARL, 4),
            createEnchantedItem(Material.IRON_AXE, 1,
                new EnchantmentData(Enchantment.EFFICIENCY, 3))
        ), Arrays.asList(
            createEnchantedItem(Material.IRON_HELMET, 1,
        new EnchantmentData(Enchantment.PROTECTION, 1)),
            createItem(Material.DIAMOND_CHESTPLATE, 1),
            createEnchantedItem(Material.DIAMOND_LEGGINGS, 1,
                    new EnchantmentData(Enchantment.PROTECTION, 1)),
            createEnchantedItem(Material.IRON_BOOTS, 1,
                    new EnchantmentData(Enchantment.PROTECTION, 1))
        )));
        
        // 套装2: 基础
        pool.add(new EquipmentSet("§b#2", Arrays.asList(
            createEnchantedItem(Material.IRON_SWORD, 1,
                new EnchantmentData(Enchantment.SHARPNESS, 3)),
            createItem(Material.DIAMOND_AXE, 1),
            createEnchantedItem(Material.BOW, 1,
                new EnchantmentData(Enchantment.POWER, 1)),
            createItem(Material.ARROW, 24),
            createItem(Material.COOKED_PORKCHOP, 24),
            createItem(Material.ENDER_PEARL, 4),
            createItem(Material.SNOWBALL, 16)
        ), Arrays.asList(
            createItem(Material.IRON_HELMET, 1),
            createEnchantedItem(Material.IRON_CHESTPLATE, 1,
                    new EnchantmentData(Enchantment.PROTECTION, 2)),
            createItem(Material.DIAMOND_LEGGINGS, 1),
            createEnchantedItem(Material.DIAMOND_BOOTS, 1,
                    new EnchantmentData(Enchantment.PROTECTION, 2))
        )));
        
        // 套装3
        pool.add(new EquipmentSet("§b#3", Arrays.asList(
            createEnchantedItem(Material.IRON_SWORD, 1,
                new EnchantmentData(Enchantment.SHARPNESS, 1)),
            createEnchantedItem(Material.IRON_AXE, 1,
                new EnchantmentData(Enchantment.SHARPNESS, 1)),
            createItem(Material.BOW, 1),
            createItem(Material.COBWEB, 16),
            createItem(Material.BREAD, 32)
            ,createItem(Material.ARROW, 8)
        ), Arrays.asList(
            createEnchantedItem(Material.GOLDEN_HELMET, 1,
                new EnchantmentData(Enchantment.PROTECTION, 4),
                new EnchantmentData(Enchantment.UNBREAKING, 3)),
            createEnchantedItem(Material.DIAMOND_CHESTPLATE, 1,
                new EnchantmentData(Enchantment.PROTECTION, 2)),
            createItem(Material.DIAMOND_LEGGINGS, 1),
            createItem(Material.IRON_BOOTS, 1)
        )));

        // 套装4
        pool.add(new EquipmentSet("§b#4", Arrays.asList(
            createItem(Material.DIAMOND_SWORD, 1),
            createEnchantedItem(Material.BOW, 1,
                new EnchantmentData(Enchantment.POWER, 1)),
            createItem(Material.DIAMOND_AXE, 1),
            createItem(Material.ARROW, 20),
            createItem(Material.COOKED_COD, 16),
            createItem(Material.GOLDEN_APPLE, 8)
        ), Arrays.asList(
            createItem(Material.DIAMOND_HELMET, 1),
            createItem(Material.IRON_CHESTPLATE, 1),
            createItem(Material.IRON_LEGGINGS, 1),
            createItem(Material.DIAMOND_BOOTS, 1)
        )));
        
        return pool;
    }
    
    /**
     * 创建普通物品
     */
    private ItemStack createItem(Material material, int amount) {
        return new ItemStack(material, amount);
    }
    
    /**
     * 创建附魔物品
     */
    private ItemStack createEnchantedItem(Material material, int amount, EnchantmentData... enchantments) {
        ItemStack item = new ItemStack(material, amount);
        for (EnchantmentData enchantment : enchantments) {
            item.addUnsafeEnchantment(enchantment.enchantment, enchantment.level);
        }
        return item;
    }
    
    /**
     * 附魔数据类
     */
    private static class EnchantmentData {
        Enchantment enchantment;
        int level;
        
        EnchantmentData(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }
    }
    
    /**
     * 装备套装类
     */
    private static class EquipmentSet {
        private String name;
        private List<ItemStack> inventoryItems;
        private List<ItemStack> armorItems;
        
        public EquipmentSet(String name, List<ItemStack> inventoryItems, List<ItemStack> armorItems) {
            this.name = name;
            this.inventoryItems = inventoryItems;
            this.armorItems = armorItems;
        }
        
        public void applyToPlayer(Player player) {
            PlayerInventory inventory = player.getInventory();

            for (ItemStack item : inventoryItems) {
                if (item != null) {
                    inventory.addItem(item);
                }
            }

            if (armorItems.size() >= 4) {
                ItemStack[] armor = new ItemStack[4];
                armor[3] = armorItems.get(0);
                armor[2] = armorItems.get(1);
                armor[1] = armorItems.get(2);
                armor[0] = armorItems.get(3);
                inventory.setArmorContents(armor);
            }
            inventory.setItemInOffHand(new ItemStack(Material.SHIELD));
        }
        
        public String getName() {
            return name;
        }
    }
}