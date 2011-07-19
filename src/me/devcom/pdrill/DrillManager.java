package me.devcom.pdrill;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.block.Block;

public class DrillManager {
	public PDrill plugin;
	
	public final ArrayList< Drill > DrillDB = new ArrayList< Drill >();
	//public final ArrayList< LinkManager > LinkDB = new ArrayList< LinkManager >();
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
		
		//for( LinkManager entry: LinkDB){
		//	entry.update();
		//}
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
		for (Drill entry : DrillDB) {
		    if( entry.id.equals( drillId )){
		    	return entry;
		    }
		}
		return null;
	}

	public void remove(Drill drill) {
		if( DrillDB.contains( drill ))
			DrillDB.remove( drill );
	}

}