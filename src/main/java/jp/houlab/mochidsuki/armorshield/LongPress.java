package jp.houlab.mochidsuki.armorshield;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;


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

                    ShieldUtil shieldUtil = new ShieldUtil(player.getInventory().getItem(EquipmentSlot.CHEST));


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

                        if (player.getInventory().getItem(EquipmentSlot.CHEST) != null) {
                            Damageable damageable = (Damageable) player.getInventory().getItem(EquipmentSlot.CHEST).getItemMeta();
                            switch (type) {
                                case "shieldmini":
                                    double d = damageable.getDamage() - (2 / shieldUtil.getShieldMax() * shieldUtil.getShieldMaxDurability());
                                    damageable.setDamage((int) d);
                                    player.getInventory().getItem(EquipmentSlot.CHEST).setItemMeta(damageable);
                                    use = 0;
                                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                                    cancel();
                                    break;

                                case "shieldmax":
                                    damageable.setDamage(0);
                                    player.getInventory().getItem(EquipmentSlot.CHEST).setItemMeta(damageable);
                                    use = 0;
                                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                                    cancel();
                                    break;
                            }
                        }
                    }
                } else {
                    use = 0;
                    cancel();
                }
                break;
            case "fenix":
                if (player.isSneaking()) {
                    use = use + 1;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 10, true, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2, 200, true, false));
                    fenixPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 10, true, false));
                    fenixPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2, 200, true, false));


                    String bar = String.join("", Collections.nCopies((int) (use / time * 10), "■"));
                    String barM = String.join("", Collections.nCopies((int) ((time - use) / time * 10), "-"));
                    String half;

                    if (use % (time / 10) != 0) {
                        half = "□";
                    } else {
                        half = "";
                    }
                    player.sendTitle("", "["+ ChatColor.LIGHT_PURPLE + bar + half + barM + ChatColor.RESET + "]", 0, 2, 20);
                    fenixPlayer.sendTitle("", "["+ ChatColor.LIGHT_PURPLE + bar + half + barM + ChatColor.RESET + "]", 0, 2, 20);

                    fenixPlayer.getWorld().spawnParticle(Particle.COMPOSTER,fenixPlayer.getLocation(),10,0.5,1,0.5,0);

                    if (use >= time) {
                        use = 0;
                        fenixPlayer.removePotionEffect(PotionEffectType.UNLUCK);
                        fenixPlayer.removePotionEffect(PotionEffectType.HEALTH_BOOST);
                        fenixPlayer.setHealth(2);
                        fenixPlayer.setFoodLevel(10);

                        for(int i =0;i<=2;i++){
                            for(int ii = 0;ii<=2;ii++){
                                for(int iii = 0; iii <= 2; iii++) {
                                    fenixPlayer.sendBlockChange(player.getLocation().add(i - 1, iii, ii - 1), player.getLocation().add(i - 1, iii, ii - 1).getBlock().getBlockData());
                                }
                            }
                        }
                        cancel();
                    }
                } else {
                    use = 0;
                    cancel();
                }
                break;
        }
    }
}
