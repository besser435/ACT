package me.besser;

import me.besser.FarmingTweaks.AutoReplantCrops;
import me.besser.FarmingTweaks.NoTrampleCrops;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static me.besser.BesserLogger.*;

public final class ACT extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        BesserLogger.initialize(this);
        saveResource("config.yml", false);

        // Check if enabled
        boolean isEnabledInConfig = getConfig().getBoolean("act.enable", true);
        if (!isEnabledInConfig) {
            log(WARNING, "ACT is disabled in config.yml and will not start.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Configs
        FileConfiguration config = this.getConfig();
        boolean enableFarmingTweaks = config.getBoolean("farming_tweaks.enable", true);

        // Farming improvements
        if (enableFarmingTweaks) {
            AutoReplantCrops replantCrops = new AutoReplantCrops();
            NoTrampleCrops noTrampleCrops = new NoTrampleCrops();
            getServer().getPluginManager().registerEvents(replantCrops, this);
            getServer().getPluginManager().registerEvents(noTrampleCrops, this);

            log(INFO, "ACT Farming Tweaks enabled");
        }


        log(INFO, "ACT v" + getPluginMeta().getVersion() + " started!");
    }

    @Override
    public void onDisable() {
        log(INFO, "ACT v" + getPluginMeta().getVersion() + " stopped!");
    }
}
