package me.devcom.pdrill;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceManager {
	Drill drill;
	
	boolean enabled = false;
	Integer tickCount = 0;
	Integer blockPlaceInterval;
	String placeDirection;
	
	public BlockPlaceManager( Drill instance, Integer blockPlaceInterval, String placeDirection ){
		this.drill = instance;
		this.blockPlaceInterval = blockPlaceInterval;
		this.placeDirection = placeDirection;
		
		enabled = true;
	}
	
	public void tick(){
		if( !enabled ) return;
		
		tickCount++;
		if( tickCount >= blockPlaceInterval){
			placeBlock();
			tickCount = 0;
		}
	}

	private void placeBlock() {
		Furnace furnace = (Furnace)drill.block.getState();
		Inventory furnaceInv = furnace.getInventory();
		ItemStack placeStack = furnaceInv.getItem(0);
		
		if(placeStack.getAmount() >= 1){
			Integer currentPlaceId = placeStack.getTypeId();
			ItemStack newStack = new ItemStack( currentPlaceId, placeStack.getAmount() - 1);
			furnaceInv.remove(currentPlaceId);
			furnaceInv.setItem(0,newStack);
			
			Location direction = drill.getDirection( placeDirection );
			Location placeLocation = drill.block.getLocation().add( direction );
			
			Block placeBlock = drill.block.getWorld().getBlockAt( placeLocation );
			placeBlock.setTypeId( currentPlaceId );
			
			Integer fuelCost = drill.plugin.configManager.placeCosts.get( furnaceInv.getItem(1).getTypeId() );
			if(fuelCost > 0)
				drill.FuelMG.consumeFuel( fuelCost );
		}else{
		}	
	}
}
