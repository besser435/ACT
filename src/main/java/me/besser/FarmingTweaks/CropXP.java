package me.besser.FarmingTweaks;

import me.besser.ACT;
import org.bukkit.Location;
import org.bukkit.Material;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.besser.BesserLogger.*;


public class CropXP implements Listener {
    private final int cropXP;
    private final Set<Material> crops = new HashSet<>();

    public CropXP(ACT plugin) {
        FileConfiguration config = plugin.getConfig();
        cropXP = config.getInt("farming_tweaks.crop_xp", 5);

        List<String> cropList = config.getStringList("farming_tweaks.xp_crops");
        for (String name : cropList) {
            try {
                crops.add(Material.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log(WARNING, "Invalid crop material in config: '" + name + "'");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST) // Run before AutoReplantCrops cancels the event.
    public void onCropBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        BlockData data = block.getBlockData();

        if (!(data instanceof Ageable ageable)) return;
        if (!crops.contains(block.getType())) return;
        if (ageable.getAge() != ageable.getMaximumAge()) return;

        World world = block.getWorld();
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        ExperienceOrb orb = world.spawn(loc, ExperienceOrb.class);
        orb.setExperience(cropXP);
    }
}
