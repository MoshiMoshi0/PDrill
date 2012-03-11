package me.devcom.pdrill;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DrillBlockListener implements Listener {
    public static PDrill plugin;

    public final DrillManager drillManager;
    public final ArrayList<Drill> DrillDB;

    public DrillBlockListener(PDrill instance) {
        plugin = instance;
        drillManager = plugin.drillManager;
        DrillDB = drillManager.DrillDB;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType().equals(Material.FURNACE)) {
            Drill drill = drillManager.getDrillFromBlock(block);

            if (drill != null) {
                drillManager.remove(drill);
                player.sendMessage("Drill removed! [" + drill.id + "]");

                if (drill.linked) {
                    drill.updateParentOnBreak();
                }
            }
        }
    }
}
