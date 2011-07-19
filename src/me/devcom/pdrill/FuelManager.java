package me.devcom.pdrill;

import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FuelManager {
	public Drill drill;
	public HashMap< Integer, Fuel > fuels;
	
	public FuelManager( Drill drill ){
		this.drill = drill;
		fuels = drill.plugin.configManager.fuels;
	}

	public Fuel fuel(){
		Integer currentFuelId = furnaceFuelId();
		return getFuelById( currentFuelId );
	}
	
	public ItemStack furnaceFuel(){
		Furnace furnace = (Furnace)drill.block.getState();
		Inventory furnaceInv = furnace.getInventory();
		return furnaceInv.getItem(1);
	}
	
	public Integer furnaceFuelId(){
		return furnaceFuel().getTypeId();
	}
	public boolean furnaceHasFuel() {
		return ( getFuelLevel() > 0 && (fuel() != null) );
	}

	public int getFuelLevel() {
		return furnaceFuel().getAmount();
	}

	public void consumeFuel() {
		Furnace furnace = (Furnace)drill.block.getState();
		Inventory furnaceInv = furnace.getInventory();
		
		Integer currentFuelId = furnaceFuelId();
		Fuel fuel = getFuelById( currentFuelId );
		
		if(fuel != null){
			Integer fuelLeft = getFuelLevel() - fuel.fuelConsumptionFuelCount;
			
			if( fuelLeft <= 0){
				furnaceInv.remove(currentFuelId);
				return;
			}
			
			ItemStack fuelStack = new ItemStack( currentFuelId, fuelLeft);
			furnaceInv.remove(currentFuelId);
			furnaceInv.setItem(1,fuelStack);
		}
	}
	
	private Fuel getFuelById(Integer currentFuelId) {
		if( fuels.containsKey( currentFuelId )){
			return fuels.get( currentFuelId );
		}
		return null;
	}

	public void swapFuel(Block nextBlock) {
		Furnace furnace = (Furnace)drill.block.getState();
		Inventory furnaceInv = furnace.getInventory();
		
		Integer currentFuelId = furnaceFuelId();
		
		Furnace nextBFurnace = (Furnace)nextBlock.getState();
		Inventory nextBFurnaceInv = nextBFurnace.getInventory(); 
	
		ItemStack fuelStack = new ItemStack( currentFuelId, getFuelLevel());
		nextBFurnaceInv.remove(currentFuelId);
		nextBFurnaceInv.setItem(1,fuelStack);
		
		furnaceInv.remove(currentFuelId);
	}

}
