package me.devcom.pdrill;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LinkDrill extends Drill {

	public ArrayList< Drill > DrillDB;
	public LinkDrill(PDrill instance, Player player, ArrayList< Drill > blocks, Integer id, Fuel fuel) {
		super(instance, player, blocks.get( 0 ).block , id);
		DrillDB = blocks;
		isVirtual = true;
		virtualFuel = fuel;
	}

	@Override
	public boolean moveInDirection(String direction) {
		if( allHasFuel() ){
			for( Drill entry : DrillDB){
/*				Fuel fuel = entry.FuelMG.fuel();

				Location nextLoc = entry.block.getLocation().add( getDirection(direction) );
				Block nextBlock = entry.block.getWorld().getBlockAt( nextLoc );
				dropBlock( nextBlock );
				nextBlock.setTypeId( Material.FURNACE.getId() );
				
				if( blockCount >= fuel.fuelConsumptionBlockCount){
					entry.FuelMG.consumeFuel();
					blockCount = 0;
				}
				entry.FuelMG.swapFuel( nextBlock );
				
				entry.block.setTypeId( 0 );
				entry.block = nextBlock;*/
				
				entry.moveInDirection(direction);
			}
			
			blockCount++;
			return true;
		}
		return false;
	}

	private boolean allHasFuel() {
		for( Drill entry : DrillDB){
			if( entry.FuelMG.getFuelLevel() <= 0 ){
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
}
