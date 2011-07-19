package me.devcom.pdrill;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Job {
	public boolean valid = false;
	public String direction;
	public Integer length;
	Drill drill;
	public Integer tickCounter = 0;
	public final Logger logger = Logger.getLogger("Minecraft");

	public HashMap< Integer, Block > DrillDB;
	//private LinkManager linkManager;
	
	public Job(Drill drill, String dir, Integer len){
		this.direction = dir;
		this.length = len;
		this.drill = drill;
		
		valid = true;
	}
	
/*	public Job(LinkManager linkManager, String dir, Integer len) {
		this.direction = dir;
		this.length = len;
		this.linkManager = linkManager;
		
		valid = true;
	}
*/
	public boolean process(){
		if( !valid || drill.linked ){
			return false;
		}
		
		tickCounter++;
		//if(linkManager != null){
		//	for( Drill entry : linkManager.DrillDB){
		//		moveBlock( entry, direction );
		//	}
		//}else{
			moveBlock( drill, direction );
		//}
		
		if(length <= 0) valid = false;
		
		return true;
	}
	
	private void moveBlock( Drill drill, String direction ){
		
		if( direction.matches("f|b|r|l|u|d") ){
			
			boolean canMove = false;
			Fuel fuel = drill.FuelMG.fuel();
			
			if(fuel != null){

				Block nextBlock = getNextBlock();
				if(nextBlock.getTypeId() ==  Material.AIR.getId() ){
					if( tickCounter >= fuel.drillAirSpeed){
						tickCounter = 0;
						canMove = true;
					}
				}else{
					if( tickCounter >= fuel.drillBlockSpeed){
						tickCounter = 0;
						canMove = true;
					}
				}
				
				if(canMove){
					if( drill.moveToLocation( nextBlock.getLocation() ) ){
						length--;
					}
				}
			}else{
				drill.owner.sendMessage("No fuel in drill [" + drill.id + "]!");
				drill.enabled = false;
			}
			
		}else{
			valid = false;
		}
	}

	public Block getNextBlock() {
		Block drillBlock = drill.block;
		Location location = drillBlock.getLocation();
		World world = drillBlock.getWorld();
		
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		
		Location nextLoc = new Location(world, x, y, z);
		nextLoc.add( drill.getDirection( direction ));
		
		return world.getBlockAt( nextLoc );
	}
	
}
