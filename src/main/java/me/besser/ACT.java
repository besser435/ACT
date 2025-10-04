package me.besser;

import me.besser.FarmingTweaks.AutoReplantCrops;
import me.besser.FarmingTweaks.NoTrampleCrops;
import me.besser.MovementTweaks.PathSpeedBoost;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static me.besser.BesserLogger.*;

public final class ACT extends JavaPlugin implements Listener {
    private PathSpeedBoost pathSpeedBoost;

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
        boolean enableMovementTweaks = config.getBoolean("movement_tweaks.enable", true);

        // Farming improvements
        if (enableFarmingTweaks) {
            AutoReplantCrops replantCrops = new AutoReplantCrops();
            NoTrampleCrops noTrampleCrops = new NoTrampleCrops();
            getServer().getPluginManager().registerEvents(replantCrops, this);
            getServer().getPluginManager().registerEvents(noTrampleCrops, this);

            log(INFO, "ACT Farming Tweaks enabled");
        }

        // Movement tweaks
        if (enableMovementTweaks) {
            pathSpeedBoost = new PathSpeedBoost(this);
            pathSpeedBoost.start(this);
        }


        log(INFO, "ACT v" + getPluginMeta().getVersion() + " started!");
    }

    @Override
    public void onDisable() {
        // Cleanup
        if (pathSpeedBoost != null) {
            pathSpeedBoost.stop();
        }

        log(INFO, "ACT v" + getPluginMeta().getVersion() + " stopped!");
    }
}
