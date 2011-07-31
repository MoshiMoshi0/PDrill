package me.devcom.pdrill;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class DrillManager {
	public PDrill plugin;
	
	public final ArrayList< Drill > DrillDB = new ArrayList< Drill >();
	public final ArrayList< LinkDrill > LinkDB = new ArrayList< LinkDrill >();
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public DrillManager(PDrill instance){
		plugin = instance;
	}
	
	public void updateDrills(){
		for (Drill entry : DrillDB) {
			if(entry.enabled && !entry.linked){
				entry.update();
			}
		}
		
		for( LinkDrill entry: LinkDB){
			if(entry.enabled && entry.isVirtual){
				entry.update();
			}
		}
	}

	public Drill getDrillFromBlock(Block block) {
		for (Drill entry : DrillDB) {
		    if( entry.block.equals( block )){
		    	return entry;
		    }
		}
		return null;
	}

	public Drill getDrillFromId(Integer drillId) {
		if( drillId >= 0){
			for (Drill entry : DrillDB) {
			    if( entry.id.equals( drillId )){
			    	return entry;
			    }
			}
		}else{
			drillId *= -1;
			for (Drill entry : LinkDB) {
			    if( entry.id.equals( drillId )){
			    	return entry;
			    }
			}
		}
		return null;
	}

	public void remove(Drill drill) {
		if( DrillDB.contains( drill ))
			DrillDB.remove( drill );
	}

	public ArrayList<Drill> getDrillsByOwner(Player player) {
		ArrayList<Drill> drills = new ArrayList<Drill>();
		for( Drill entry : DrillDB ){
			if(entry.owner.equals( player ) ){
				drills.add( entry );
			}
		}
		return (drills.size() > 0 ? drills : null);
	}

}