package jp.houlab.mochidsuki.armorshield;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

import static jp.houlab.mochidsuki.armorshield.Main.config;


public class LongPress extends BukkitRunnable {
    double use = 0;
    Player player;
    String type;
    Material item;
    double time;
    Player fenixPlayer;

    public LongPress(Player p, String t, Material i,double ti,Player player1){
        player = p;
        type = t;
        item = i;
        time = ti;
        fenixPlayer = player1;
    }

    @Override
    public void run() {
        switch (type) {
            case "shieldmini":
            case "shieldmax":
                if (player.getInventory().getItemInMainHand().getType() == item) {
                    use = use + 1;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 4, true, false));
                    if(use%10 == 1) {
                        //player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1, (float) ((use/time)+1));
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, (float) ((use/time)/3+1));
                    }
                    if(use%5 == 1) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.3f, (float) ((use / time) + 1));
                    }
                    ShieldUtil shieldUtil = new ShieldUtil(player.getInventory().getItem(config.getInt("ChestPlateSlot")));


                    String bar = String.join("", Collections.nCopies((int) (use / time * 10), "■"));
                    String barM = String.join("", Collections.nCopies((int) ((time - use) / time * 10), "-"));
                    String half;


                    if (use % (time / 10) != 0) {
                        half = "□";
                    } else {
                        half = "";
                    }
                    //バリアブロック設置
                    for(int i =0;i<=2;i++){
                        for(int ii = 0;ii<=2;ii++){
                            for(int iii = 0; iii <= 2; iii++) {
                                player.sendBlockChange(player.getLocation().add(i - 1, iii+1, ii - 1), player.getLocation().add(i - 1, iii+1, ii - 1).getBlock().getBlockData());
                            }
                        }
                    }

                    if(!player.hasPotionEffect(PotionEffectType.LUCK)) {
                        if (player.getLocation().add(0, 2, 0).getBlock().getType() == Material.AIR) {
                            player.sendBlockChange(player.getLocation().add(0, 2, 0), Material.BARRIER.createBlockData());
                        }
                    }



                    player.sendTitle("", "[" + shieldUtil.getShieldColor() + bar + half + barM + ChatColor.RESET + "]", 0, 2, 20);

                    if (use >= time) {


                        use = 0;

                        for(int i =0;i<=2;i++){
                            for(int ii = 0;ii<=2;ii++){
                                for(int iii = 0; iii <= 2; iii++) {
                                    player.sendBlockChange(player.getLocation().add(i - 1, iii+1, ii - 1), player.getLocation().add(i - 1, iii+1, ii - 1).getBlock().getBlockData());
                                }
                            }
                        }

                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);

                        if (player.getInventory().getItem(config.getInt("ChestPlateSlot")) != null) {
                            Damageable damageable = (Damageable) player.getInventory().getItem(config.getInt("ChestPlateSlot")).getItemMeta();
                            switch (type) {
                                case "shieldmini":
                                    double d = damageable.getDamage() - (2 / shieldUtil.getShieldMax() * shieldUtil.getShieldMaxDurability());
                                    damageable.setDamage((int) d);
                                    player.getInventory().getItem(config.getInt("ChestPlateSlot")).setItemMeta(damageable);
                                    use = 0;
                                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                                    cancel();
                                    break;

                                case "shieldmax":
                                    damageable.setDamage(0);
                                    player.getInventory().getItem(config.getInt("ChestPlateSlot")).setItemMeta(damageable);
                                    use = 0;
                                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                                    cancel();
                                    break;
                            }
                        }
                    }
                } else {
                    use = 0;
                    for(int i =0;i<=2;i++){
                        for(int ii = 0;ii<=2;ii++){
                            for(int iii = 0; iii <= 2; iii++) {
                                player.sendBlockChange(player.getLocation().add(i - 1, iii+1, ii - 1), player.getLocation().add(i - 1, iii+1, ii - 1).getBlock().getBlockData());
                            }
                        }
                    }
                    cancel();
                }
                break;
        }
    }
}
