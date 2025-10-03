package me.besser;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static me.besser.BesserLogger.*;

public final class ACT extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        BesserLogger.initialize(this);

        getServer().getPluginManager().registerEvents(this, this);

        log(INFO, "ACT v" + getPluginMeta().getVersion() + " started!");
    }

    @Override
    public void onDisable() {
        log(INFO, "ACT v" + getPluginMeta().getVersion() + " stopped!");
    }

    @EventHandler
    public void onPlayerHarvest(BlockBreakEvent event) {
        // Check if not a crop, break out fast
        Block crop = event.getBlock();
        if (! (Tag.CROPS.isTagged(crop.getType()) || crop.getType() == Material.NETHER_WART) ) {
            return;
        }

        // Only allow hoe for functionality
        ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();
        if (! (Tag.ITEMS_HOES.isTagged(heldItem.getType())) ) {
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
        }
    }




    @EventHandler(ignoreCancelled = true)
    public void onPlayerTrample(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();

            if (block != null && block.getType() == Material.FARMLAND) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTrample(EntityInteractEvent event) {
        if (event.getBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }
}
