package me.besser.FarmingTweaks;

import me.besser.ACT;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class CropXP implements Listener {
    private final int cropXP;

    public CropXP(ACT plugin) {
        FileConfiguration config = plugin.getConfig();
        cropXP = config.getInt("farming_tweaks.crop_xp", 5);
    }

    // Run before AutoReplantCrops cancels the event.
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCropBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        BlockData data = block.getBlockData();

        if (!(data instanceof Ageable ageable)) return;

        if (ageable.getAge() == ageable.getMaximumAge()) {
            World world = block.getWorld();
            Location loc = block.getLocation().add(0.5, 0.5, 0.5);

            ExperienceOrb orb = world.spawn(loc, ExperienceOrb.class);
            orb.setExperience(cropXP);
        }
    }
}
