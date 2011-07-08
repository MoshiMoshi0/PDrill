package me.devcom.pdrill;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Job {
	public boolean valid = false;
	public String direction;
	public Integer length;
	Drill drill;
	public final Logger logger = Logger.getLogger("Minecraft");

	public HashMap< Integer, Block > DrillDB;
	
	public Job(Drill drill, String dir, Integer len){
		this.direction = dir;
		this.length = len;
		this.drill = drill;
		
		valid = true;
	}
	
	public boolean process(){
		if( !valid ){
			return false;
		}

		moveBlock( direction );
		
		if(length <= 0) valid = false;
		
		return true;
	}
	
	private void moveBlock( String direction ){
		
		if( direction.matches("f|b|r|l|u|d") ){
			
			Block drillBlock = drill.block;
			Location location = drillBlock.getLocation();
			World world = drillBlock.getWorld();
			
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			
			Location nextLoc = new Location(world, x, y, z);
			nextLoc.add( drill.getDirection( direction ));

			if( drill.moveToLocation( nextLoc ) ){
				length--;
			}
		}else{
			valid = false;
		}
	}
	
}
