package me.besser.FarmingTweaks;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class AutoReplantCrops implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHarvest(BlockBreakEvent event) {
        // Break out fast if not a crop
        Block crop = event.getBlock();
        Material type = crop.getType();
        if (! (Tag.CROPS.isTagged(type) ||
                type == Material.NETHER_WART ||
                type == Material.COCOA) ) {
            return;
        }

        // Check if holding the correct tool
        ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();
        Material heldType = heldItem.getType();

        boolean hasCorrectTool = (type == Material.COCOA && Tag.ITEMS_AXES.isTagged(heldType)) ||
                (type != Material.COCOA && Tag.ITEMS_HOES.isTagged(heldType));

        if (!hasCorrectTool) {
            return;
        }

        if (crop.getBlockData() instanceof Ageable ageable) {
            // Only replant if the crop is mature
            // Also prevents breaking immature crops when holding a hoe
            if (! (ageable.getAge() == ageable.getMaximumAge()) ){
                event.setCancelled(true);
                return;
            }

            // Drop items manually (supposed to apply Fortune enchants, but that seems to not work)
            // This will give one extra seed crop, as we replant it automatically in the next step
            event.setCancelled(true);
            crop.getDrops(heldItem).forEach(item ->
                    crop.getWorld().dropItemNaturally(crop.getLocation(), item)
            );

            // Replace the crop
            ageable.setAge(0);
            crop.setBlockData(ageable);

            heldItem.damage(1, event.getPlayer());
        }
    }
}
