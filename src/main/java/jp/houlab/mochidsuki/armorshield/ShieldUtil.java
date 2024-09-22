package jp.houlab.mochidsuki.armorshield;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Objects;

import static jp.houlab.mochidsuki.armorshield.Main.config;

/**
 * 与えられたチェストプレートからアーマー状況を算出するユーティリティクラス
 * @author Mochidsuki
 */
public class ShieldUtil {


    ItemStack chest;

    /**
     * コンストラクタ
     * @param itemStack チェストプレート
     */
    public ShieldUtil(ItemStack itemStack){
        chest = itemStack;
    }

    /**
     * シールド最大値を取得
     * @return シールド最大値
     */
    public double getShieldMax(){
        if(chest != null) {
            switch (Objects.requireNonNull(chest).getType()) {
                case CHAINMAIL_CHESTPLATE:
                    return config.getInt("Armor.CHAINMAIL");
                case IRON_CHESTPLATE:
                    return config.getInt("Armor.IRON");
                case GOLDEN_CHESTPLATE:
                    return config.getInt("Armor.GOLDEN");
                case DIAMOND_CHESTPLATE:
                    return config.getInt("Armor.DIAMOND");
                case NETHERITE_CHESTPLATE:
                    return config.getInt("Armor.NETHERITE");
                default:
                    return 0;
            }
        }return 0;
    }

    /**
     * 現在のシールド残量を取得
     * @return シールド残量
     */
    public int getShieldNow(){
        if(chest != null && (chest.getItemMeta()) != null) {
            Damageable d = (Damageable) chest.getItemMeta();

            double dDamage = Objects.requireNonNull(d).getDamage();
            double durableValue = getShieldMaxDurability() - dDamage;
            return (int) (durableValue / getShieldMaxDurability() * getShieldMax());
        }else {
            return 0;
        }
    }

    /**
     * 現在のシールドのUI上での色を取得
     * @return 色
     */
    public ChatColor getShieldColor(){
        if(chest != null) {
            switch (Objects.requireNonNull(chest).getType()) {
                case CHAINMAIL_CHESTPLATE:
                    return ChatColor.GRAY;
                case IRON_CHESTPLATE:
                    return ChatColor.DARK_GRAY;
                case GOLDEN_CHESTPLATE:
                    return ChatColor.YELLOW;
                case DIAMOND_CHESTPLATE:
                    return ChatColor.AQUA;
                case NETHERITE_CHESTPLATE:
                    return ChatColor.DARK_RED;
                default:
                    return ChatColor.RESET;
            }
        }else {
            return ChatColor.RESET;
        }
    }

    /**
     * シールドの最大耐久値を取得
     * @return
     */
    public double getShieldMaxDurability(){
        if(chest != null) {
            return chest.getType().getMaxDurability();
        }else {
            return 0;
        }
    }
}

