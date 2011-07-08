package me.devcom.pdrill;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class DrillBlockListener extends BlockListener {
	public static PDrill plugin;
	
	public final DrillManager drillManager;
	public final ArrayList< Drill > DrillDB;
	
	public DrillBlockListener( PDrill instance ){
		plugin = instance;
		drillManager = plugin.drillManager;
		DrillDB = drillManager.DrillDB;
	}
	
	public void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		if(block.getType().equals( Material.FURNACE )){
			Drill drill = drillManager.getDrillFromBlock(block);
			
			if( drill != null && drill.enabled ){
				drillManager.remove( drill );
				player.sendMessage("Drill removed! [" + drill.id +"]");
			}
		}
	}
}
