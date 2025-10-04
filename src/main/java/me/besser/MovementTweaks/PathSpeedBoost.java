package me.besser.MovementTweaks;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.besser.ACT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.configuration.file.FileConfiguration;

import static me.besser.BesserLogger.*;

public class PathSpeedBoost {
    private ScheduledTask task;
    private final Material speedBlock;
    private final int speedAmplifier;

    public PathSpeedBoost(ACT plugin) {
        FileConfiguration config = plugin.getConfig();

        String blockName = config.getString("movement_tweaks.path_speed_boost.block", "DIRT_PATH");
        Material configBlock = Material.getMaterial(blockName);

        if (configBlock == null || !configBlock.isBlock()) {
            log(WARNING, "Invalid block '" + blockName + "' in config. Using DIRT_PATH as default.");
            speedBlock = Material.DIRT_PATH;
        } else {
            speedBlock = configBlock;
        }


        int configAmplifier = config.getInt("movement_tweaks.path_speed_boost.speed_level", 1);

        if (configAmplifier < 1 || configAmplifier > 10) {
            log(WARNING, "Invalid speed level " + configAmplifier + " in config (must be 1-10). Using 1 as default.");
            speedAmplifier = 0; // Amplifier 0 = Speed I
        } else {
            speedAmplifier = configAmplifier - 1; // Convert to amplifier (Speed I = 0, Speed II = 1)
        }
    }


    public void start(ACT plugin) {
        // Run every 10 ticks using Paper's scheduler.
        // This is much faster than using a PlayerMove event lister, as it doesn't get fired all the time from everyone.
        task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, (scheduledTask) -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Location playerLoc = player.getLocation();

                // Won't work if you have Jump Boost II, but if you jump with that with speed you will be slower,
                // so it doesn't really matter.
                Material blockBelow1 = playerLoc.clone().add(0, -0.5, 0).getBlock().getType();
                Material blockBelow2 = playerLoc.clone().add(0, -1.2, 0).getBlock().getType();

                if (blockBelow1 == speedBlock || blockBelow2 == speedBlock) {
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.SPEED,
                            40,
                            speedAmplifier,
                            false,   // No ambient particles
                            false           // No particles
                    ));
                }
            }
        }, 20L, 10L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }
}
