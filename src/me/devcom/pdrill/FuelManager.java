package me.devcom.pdrill;

import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FuelManager {
	public Drill drill;
	
	public FuelManager( Drill drill ){
		this.drill = drill;
	}

	public ItemStack fuel(){
		Furnace furnace = (Furnace)drill.block.getState();
		Inventory furnaceInv = furnace.getInventory();
		return furnaceInv.getItem(1);
	}
	
	public Integer fuelId(){
		return fuel().getTypeId();
	}
	public boolean hasFuel() {
		return ( getFuelLevel() > 0 && isProperFuel( fuelId() ) );
	}

	private int getFuelLevel() {
		return fuel().getAmount();
	}

	public void consumeFuel() {
		Furnace furnace = (Furnace)drill.block.getState();
		Inventory furnaceInv = furnace.getInventory();
		
		Integer currentFuelId = fuelId();
		Integer fuelLeft = getFuelLevel() - 1; // todo
		
		if( fuelLeft <= 0){
			furnaceInv.remove(currentFuelId);
			return;
		}
		
		ItemStack fuelStack = new ItemStack( currentFuelId, fuelLeft);
		furnaceInv.remove(currentFuelId);
		furnaceInv.setItem(1,fuelStack);
	}

	public boolean isProperFuel(Integer currentFuelId) {
		return true;
	}

	public void swapFuel(Block nextBlock) {
		Furnace furnace = (Furnace)drill.block.getState();
		Inventory furnaceInv = furnace.getInventory();
		
		Integer currentFuelId = fuelId();
		
		Furnace nextBFurnace = (Furnace)nextBlock.getState();
		Inventory nextBFurnaceInv = nextBFurnace.getInventory(); 
	
		ItemStack fuelStack = new ItemStack( currentFuelId, getFuelLevel());
		nextBFurnaceInv.remove(currentFuelId);
		nextBFurnaceInv.setItem(1,fuelStack);
		
		furnaceInv.remove(currentFuelId);
	}

}
