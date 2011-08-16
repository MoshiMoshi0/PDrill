package me.devcom.pdrill;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LinkDrill extends Drill {

	public ArrayList< Drill > DrillDB;
	public LinkDrill(PDrill instance, Player player, ArrayList< Drill > blocks, Integer id, Fuel fuel) {
		super(instance, player, null , id);
		this.FuelMG = null;
		
		DrillDB = blocks;
		
		for( Drill entry : DrillDB){
			entry.parent = this;
			if( entry.enabled == false )
				entry.enable();
		}
		
		isVirtual = true;
		virtualFuel = fuel;
	}
	
	@Override
	public boolean moveInDirection(String direction) {
		if( allHasFuel() ){
			for( Drill entry : DrillDB){
				entry.moveInDirection(direction);
			}
			
			blockCount++;
			return true;
		}
		return false;
	}

	private boolean allHasFuel() {
		for( Drill entry : DrillDB){
			if( entry.FuelMG.getFuelLevel() <= 0 || ((int)entry.FuelMG.furnaceFuelId() != (int)this.virtualFuel.block_id )){
				owner.sendMessage( prefix + "No fuel in drill with id [" + entry.id + "]");
				disable();
				return false;
			}
		}
		return true;
	}

	
	@Override
	public Block getNextBlock(String direction) {
		Block block = null;
		for( Drill entry : DrillDB){
			Block getBlock = entry.getNextBlock( direction );
			if( getBlock.getTypeId() != Material.AIR.getId()){
				return getBlock;
			}else{
				block = getBlock;
			}
		}
		return block;
	}

	@Override
	public boolean canMoveInDirection(String direction) {
		for( Drill entry : DrillDB){
			if( !entry.canMoveInDirection(direction) ){
				Location nextLoc = entry.block.getLocation().add( getDirection(direction) );
				Block nextBlock = entry.block.getWorld().getBlockAt( nextLoc );
				Drill nextDrill = plugin.drillManager.getDrillFromBlock( nextBlock );
				if(nextDrill != null){
					if( !DrillDB.contains( nextDrill )){
						return false;
					}
				}else{
					return false;
				}
			}
		}
		return true;
	}


	public void checkDatabase() {
		if(DrillDB.size() == 1){
			Drill entry = DrillDB.get(0);
			entry.linked = false;
			entry.parent = null;
			
			DrillDB.clear();
			
			enabled = false;
		}
	}
}
