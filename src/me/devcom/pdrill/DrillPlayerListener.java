package me.devcom.pdrill;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class DrillPlayerListener extends PlayerListener {	
	public static PDrill plugin;

	public final DrillManager drillManager;
	public final ArrayList< Drill > DrillDB;

	public DrillPlayerListener( PDrill instance ){
		plugin = instance;
		drillManager = plugin.drillManager;
		DrillDB = drillManager.DrillDB;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        
    	if(event.getAction() == Action.LEFT_CLICK_BLOCK && block.getType() == Material.FURNACE && player.getItemInHand().getType() == Material.STICK){
    		Drill drill = drillManager.getDrillFromBlock( block );
    		
    		if(drill != null){
    			Integer id = drill.id;
    			if(drill.enabled){
    				player.sendMessage( "Drill deactivated! [" + id +"]" );
        			drill.enabled = false;
    			}else{
    				player.sendMessage( "Drill activated! [" + id +"]" );
        			drill.enabled = true;
    			}
    		}else{
    			Integer id = DrillDB.size();
    			DrillDB.add( new Drill( player, block, id ));
    			player.sendMessage( "Drill activated! [" + id +"]" );
    		}
    	}
	}
}
