package jp.houlab.mochidsuki.armorshield;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    static public Plugin plugin;
    static public FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        //Config
        saveDefaultConfig();
        config = getConfig();

        //EveryTicks
        new EveryTicks().runTaskTimer(this,0,1L);

        //EventRegister
        getServer().getPluginManager().registerEvents(new Listener(),this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}


class V{

}