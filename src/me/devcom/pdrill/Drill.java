package me.devcom.pdrill;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Drill {
	public Player owner;
	public Location position;
	public Block block;
	public Integer id;
	public boolean enabled;
	public JobManager JobMG;
	public FuelManager FuelMG;
	
	public Location forwardDir;
	public Location backwardDir;
	public Location rightDir;
	public Location leftDir;
	public Location upDir;
	public Location downDir;

	public final Logger logger = Logger.getLogger("Minecraft");
	
	public Drill(Player player, Block block, Integer id){
		this.owner = player;
		this.block = block;
		this.id = id;
		
		enabled = true;
		
		JobMG = new JobManager( this );
		FuelMG = new FuelManager( this );
		
		World world = block.getWorld();
		
		Vector dir = getBlockPlayerDirection( block, player);
		forwardDir = dir.toLocation( world );
		rightDir = dir.crossProduct( new Vector( 0, 1, 0) ).toLocation(world);
		
		dir.multiply( -1 );
		backwardDir = dir.toLocation( world );
		leftDir = dir.crossProduct( new Vector( 0, 1, 0) ).toLocation(world);
		
		upDir = new Location(world, 0,1,0);
		downDir = new Location( world, 0,-1,0);
		
	}
	
	private Vector getBlockPlayerDirection(Block block, Player player) {
		Vector playerVec = player.getLocation().toVector();
		Vector playerBlockVec = toBlockVector( playerVec );
		
		Vector blockVec = block.getLocation().toVector();
		Vector blockBlockVec = toBlockVector( blockVec );
		
		Vector dir = blockBlockVec.subtract( playerBlockVec );
		
		dir.setY( 0 );
		if(dir.getX() > dir.getZ()){
			dir.setZ( 0 );
		}else if(dir.getX() < dir.getZ()){
			dir.setX( 0 );
		}else if(dir.getX() == dir.getZ()){
			dir.setZ( 0 );
		}
		
		return dir.normalize();
	}

	private Vector toBlockVector(Vector v) {
		return new Vector( v.getBlockX(),v.getBlockY(), v.getBlockZ());
	}

	public void update(){
		JobMG.doJob();
	}

	public boolean moveToLocation(Location nextLoc) {
		if( FuelMG.hasFuel() ){
			
			Block nextBlock = block.getWorld().getBlockAt( nextLoc );
			nextBlock.setTypeId( Material.FURNACE.getId() );
			
			FuelMG.swapFuel( nextBlock );
			
			block.setTypeId( 0 );
			block = nextBlock;
			
			FuelMG.consumeFuel();
			
			return true;
		}else{
			enabled = false;
			return false;
		}
	}

	public Location getDirection(String direction) {
		Location dirLoc = null;
		
		if( direction.equals("f") ){
			dirLoc = forwardDir;
		}else if( direction.equals("b") ){
			dirLoc = backwardDir;
		}else if( direction.equals("r") ){
			dirLoc = rightDir;
		}else if( direction.equals("l") ){
			dirLoc = leftDir;
		}else if( direction.equals("u") ){
			dirLoc = upDir;
		}else if( direction.equals("d") ){
			dirLoc = downDir;
		}
		
		return dirLoc;
	}
}
