package jp.houlab.mochidsuki.armorshield;

import jp.houlab.mochidsuki.knockdown.scoreCounterAPI.ScoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

import static jp.houlab.mochidsuki.armorshield.Main.config;
import static jp.houlab.mochidsuki.armorshield.Main.plugin;

/**
 * イベントリスナー
 * @author Mochidsuki
 */
public class Listener implements org.bukkit.event.Listener {
    /**
     * アーマーがダメージを肩代わりする
     * @param event イベント
     */
    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Player damager = null;

        if (event.getDamager().getType().equals(EntityType.PLAYER)) {
            if (((Player) event.getDamager()).hasPotionEffect(PotionEffectType.UNLUCK)) {
                event.setCancelled(true);
                return;
            }
            try {
                ((Player) event.getEntity()).removePotionEffect(PotionEffectType.INVISIBILITY);
            } catch (Exception ignored) {
            }
        }

        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player player = (Player) event.getEntity();
            if (event.getDamager().getType() == EntityType.PLAYER || event.getDamager().getType() == EntityType.ARROW || event.getDamager().getType() == EntityType.FIREBALL) {
                switch (event.getDamager().getType()){
                    case PLAYER:{
                        damager = (Player) event.getDamager();
                        break;
                    }
                    case ARROW:{
                        if(((Arrow) event.getDamager()).getShooter() instanceof Player) {
                            damager = (Player) ((Arrow) event.getDamager()).getShooter();
                        }
                        break;
                    }
                    case FIREBALL:{
                        if(((Fireball) event.getDamager()).getShooter() instanceof Player) {
                            damager = (Player) ((Fireball) event.getDamager()).getShooter();
                        }
                    }
                }
            }


            //シールド
            double damage = event.getFinalDamage();
            if (player.getInventory().getItem(config.getInt("ChestPlateSlot")) != null) {
                if (player.getInventory().getItem(config.getInt("ChestPlateSlot")).getType() == Material.LEATHER_CHESTPLATE || player.getInventory().getItem(config.getInt("ChestPlateSlot")).getType() == Material.CHAINMAIL_CHESTPLATE || player.getInventory().getItem(config.getInt("ChestPlateSlot")).getType() == Material.IRON_CHESTPLATE || player.getInventory().getItem(config.getInt("ChestPlateSlot")).getType() == Material.GOLDEN_CHESTPLATE || player.getInventory().getItem(config.getInt("ChestPlateSlot")).getType() == Material.DIAMOND_CHESTPLATE || player.getInventory().getItem(config.getInt("ChestPlateSlot")).getType() == Material.NETHERITE_CHESTPLATE) {
                    ShieldUtil shieldUtil = new ShieldUtil(player.getInventory().getItem(config.getInt("ChestPlateSlot")));
                    int shieldNow = shieldUtil.getShieldNow();
                    if (shieldNow > 0) {


                        double d = event.getFinalDamage();
                        if(d>shieldNow){
                            d = shieldNow;
                        }
                        if(plugin.getServer().getPluginManager().isPluginEnabled("Knockdown") && damager != null && !damager.getUniqueId().equals(player.getUniqueId())){
                            ScoreProfile.scoreProfiles.get(damager.getUniqueId()).addDamageScore(d);
                        }

                        damage = (int) (event.getFinalDamage() - shieldNow);
                        shieldNow = (int) (shieldNow - event.getFinalDamage());
                        if (shieldNow <= 0) {
                            shieldNow = 0;
                            if (damager != null) {
                                damager.playSound(damager.getLocation(), Sound.BLOCK_GLASS_BREAK, 100, 0);
                            }
                        }

                        if (damage <= 0) {
                            damage = 0;
                        }
                        shieldUtil.setShieldNow(shieldNow);
                        event.setDamage(damage);

                    }
                }
            }
        }
    }

    /**
     * チェストプレートが破壊されないようにする
     * @param event イベント
     */
    @EventHandler
    public void PlayerItemBreakEvent(PlayerItemBreakEvent event) {
        if (event.getBrokenItem().getType() == Material.CHAINMAIL_CHESTPLATE || event.getBrokenItem().getType() == Material.IRON_CHESTPLATE || event.getBrokenItem().getType() == Material.GOLDEN_CHESTPLATE || event.getBrokenItem().getType() == Material.DIAMOND_CHESTPLATE || event.getBrokenItem().getType() == Material.NETHERITE_CHESTPLATE) {
            event.getPlayer().getInventory().setItem(EquipmentSlot.CHEST, event.getBrokenItem());
        }
    }

    /**
     * シールドを回復する
     * @param event イベント
     */
    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            switch (Objects.requireNonNull(event.getMaterial())) {

                case ENDER_PEARL://フルチャージャー
                    if (!(event.getPlayer().hasPotionEffect(PotionEffectType.SLOW))) {
                        if (!(event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST).getType() == Material.AIR || Objects.equals(event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST), new ItemStack(Material.LEATHER_CHESTPLATE)))) {
                            Damageable d = (Damageable) event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST).getItemMeta();
                            if (d.getDamage() != 0) {
                                new LongPress(event.getPlayer(), "shieldmini", event.getMaterial(), 40, null).runTaskTimer(plugin, 0L, 1L);
                            } else {
                                event.getPlayer().sendTitle("", "シールドは新品同様です", 10, 10, 20);
                            }
                        } else {
                            event.getPlayer().sendTitle("", ChatColor.RED + "シールドがありません", 10, 10, 20);
                        }
                    }
                    event.setCancelled(true);
                    break;
                case MUSIC_DISC_5://ミニチャージャー
                    if (!(event.getPlayer().hasPotionEffect(PotionEffectType.SLOW))) {
                        if (!(event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST).getType() == Material.AIR || Objects.equals(event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST), new ItemStack(Material.LEATHER_CHESTPLATE)))) {
                            Damageable d = (Damageable) event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST).getItemMeta();
                            if (d.getDamage() != 0) {
                                new LongPress(event.getPlayer(), "shieldmax", event.getMaterial(), 100, null).runTaskTimer(plugin, 0L, 1L);
                            } else {
                                event.getPlayer().sendTitle("", "シールドは新品同様です", 10, 10, 20);
                            }
                        } else {
                            event.getPlayer().sendTitle("", ChatColor.RED + "シールドがありません", 10, 10, 20);
                        }
                    }
                    event.setCancelled(true);
                    break;

            }

        }
    }
}
