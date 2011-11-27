package me.devcom.pdrill;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandProcessor {
	PDrill plugin = null;
	
	public ConfigurationManager configManager = null;
	public DrillManager drillManager = null;
	
	public final Logger logger = Logger.getLogger("Minecraft");
	public final String prefix = "[PDrill] ";
	
	public Integer errorlevel;
	
	public CommandProcessor(PDrill instance){
		plugin = instance;
		
		drillManager = plugin.drillManager;
		configManager = plugin.configManager;
	}
	
	public boolean process(CommandSender sender, String commandLabel, String[] args){
		errorlevel = -1;
		
		if( commandLabel.equalsIgnoreCase("pdscript") || commandLabel.equalsIgnoreCase("pdload")){
			
			ArrayList<String> script = new ArrayList<String>();
			ArrayList<Integer> drillIds = getDrillIdsFromString( sender, args[0] );
			
			if( drillIds.size() == 0 ){
				return false;
			}
			
			if(commandLabel.equalsIgnoreCase("pdscript")){
				for(Integer i = 1; i < args.length; i++){
					script.add( args[i] );
				}
			}else if(commandLabel.equalsIgnoreCase("pdload")){
				String scriptName = args[1];
					
				String scriptString = configManager.getScriptByName( scriptName );
				if(scriptString != ""){
					Integer scriptArgsCount = 0;
					
					for(Integer i = 0; ; i++){
						String index = "(" + i + ")";
						if(scriptString.contains( index )){
							scriptArgsCount++;
						}else{
							break;
						}
					}

					for(Integer i = 0; i < scriptArgsCount; i++){
						if( i + 2 < args.length ){
							String index = "(" + i + ")";
							if(scriptString.contains( index )){
								scriptString = scriptString.replaceAll( "\\(" + i + "\\)", args[i + 2]);
							}
						}else{
							sender.sendMessage(prefix + "This script needs [" + (scriptArgsCount - i) + "] more arguments");
							return true;
						}
					}
					
					if(args.length - 2 > scriptArgsCount){
						sender.sendMessage(prefix + "Too much arguments, ommiting [" + ((args.length - 2) - scriptArgsCount) + "] arguments");
					}
					
					sender.sendMessage( scriptString );
					
					String[] scriptArgs = scriptString.split( " " );
					for(Integer i = 0; i < scriptArgs.length; i++){
						script.add( scriptArgs[i] );
					}
				}else{
					sender.sendMessage( prefix + "No such script [" + scriptName + "]");
				}
			}
			
			sendScripts(sender, drillIds, script);
		}else if( commandLabel.equalsIgnoreCase( "pdlink" ) ){
			if( args.length < 2 ){
				return false;
			}
			ArrayList<Integer> drillIds = getDrillIdsFromString( sender, args[0]);
			
			boolean fail = false;
			for( Integer id : drillIds ){
				Drill drill = drillManager.getDrillFromId( id );
				
				if(drill == null){
					sender.sendMessage( prefix + "Linking failed! No drill with id [" + id + "]");
					fail = true;
				}else if( drill.linked ){
					sender.sendMessage( prefix + "Linking failed! Drill with id [" + id + "] is allready linked");
					fail = true;
				}
			}
			
			if(!fail){			
				ArrayList< Drill > drills = new ArrayList< Drill >();
				for( Integer id : drillIds ){
					Drill drill = drillManager.getDrillFromId( id );
					
					drill.linked = true;
					drills.add( drill );
				}
				
				Player player = (Player)sender;
				Integer id = drillManager.LinkDB.size() + 1;
				Fuel fuel = configManager.getFuelByName( args[1] );
				
				if(fuel != null){
					LinkDrill lDrill = new LinkDrill( plugin, player, drills, id, fuel );
					drillManager.LinkDB.add( lDrill );
				
					sender.sendMessage( prefix + "Linked " + drillIds.toString() + " to [" + -id + "]");
				}else{
					sender.sendMessage( prefix + "No such fuel [" + args[1] + "] in database");
				}
			}
			return true;
		}else if( commandLabel.equalsIgnoreCase( "pddelink" )){
			ArrayList<Integer> drillIds = getDrillIdsFromString( sender, args[0]);
			
			if( drillIds.size() > 0 ){
				for( Integer id : drillIds ){
					LinkDrill drill = plugin.drillManager.getLinkDrillFromId( -id );
					
					if( drill != null ){
						for( Drill subDrill : drill.DrillDB ){
							subDrill.linked = false;
						}
					}else{
						//@TODO
					}
					
					drill.owner.sendMessage( prefix + "Unlinked " + drillIds.toString() + " from [" + -id + "]");
					plugin.drillManager.LinkDB.remove( drill );
				}
			}else{
				return false;
			}
			return true;
		}else if( commandLabel.equalsIgnoreCase( "pdcreate" ) ){
			String what = args[0];
			
			if(args.length < 2){
				return false;
			}
			
			if(what.equalsIgnoreCase( "script" )){
				
				String name = args[1];
				String script = "";
				
				if(configManager.getScriptByName( name ) != ""){
					sender.sendMessage( prefix + "Script with name [" + name + "] allready exists!" );
					return true;
				}
				
				configManager.config.load();
				
				if(args.length > 2){
					for(Integer i = 2; i < args.length; i++){
						script += args[i];
						if( i < args.length - 1 ){
							script += " ";
						}
					}
					
					configManager.config.setProperty("script." + name + ".script", script);
				}else{
					configManager.config.setProperty("script." + name + ".script", "");
				}
				
				configManager.scripts.put(name, script);
				configManager.config.save();
			}else if( what.equalsIgnoreCase("fuel") ){
				String name = args[1];
				Integer id, airSpeed, blockSpeed, blockCount, fuelCount;
				
				if( args.length >= 3){
					id = Integer.parseInt( args[2] );
					
					if(args.length >= 4){
						airSpeed = Integer.parseInt( args[3] );
						
						if( args.length >= 5){
							blockSpeed = Integer.parseInt( args[4] );
							
							if(args.length >= 6){
								blockCount = Integer.parseInt( args[5] );
								
								if(args.length >= 7){
									fuelCount = Integer.parseInt( args[6] );
								}else{
									fuelCount = 1;
								}
							}else{
								blockCount = 1;
								fuelCount = 1;
							}
						}else{
							blockSpeed = 1;
							blockCount = 1;
							fuelCount = 1;
						}
					}else{
						airSpeed = 1;
						blockSpeed = 1;
						blockCount = 1;
						fuelCount = 1;
					}
				}else{
					id = -1;
					airSpeed = 1;
					blockSpeed = 1;
					blockCount = 1;
					fuelCount = 1;
				}
				
				configManager.config.load();
				
				configManager.config.setProperty("fuel." + name + ".fuelId", id);
				configManager.config.setProperty("fuel." + name + ".drillAirSpeed", airSpeed);
				configManager.config.setProperty("fuel." + name + ".drillBlockSpeed", blockSpeed);
				configManager.config.setProperty("fuel." + name + ".fuelConsumptionBlockCount",blockCount);
				configManager.config.setProperty("fuel." + name + ".fuelConsumptionFuelCount", fuelCount);
				
			    Fuel fuel = new Fuel(id, airSpeed, blockSpeed, blockCount, fuelCount, name);
			    configManager.fuels.put(id, fuel);
			    
				configManager.config.save();
			}
		}else if(commandLabel.equalsIgnoreCase( "pdlist" )){
			if(args.length == 0){
				return false;
			}
			String what = args[0];
			
			if(what.equalsIgnoreCase( "drill" )){
				 ArrayList< Drill > ownedDrills = drillManager.getDrillsByOwner( (Player)sender );
				 
				 sender.sendMessage( "Drills: " + ownedDrills.size() );
				 sender.sendMessage( "----------------------------" );
				 for( Drill entry : ownedDrills ){
					 Fuel fuel = entry.FuelMG.fuel();
					 sender.sendMessage( "ID: " +entry.id + " ENABLED: " +entry.enabled+ " FUEL_ID: " +fuel.block_id+ " FUEL_LEFT: " +fuel.block_id);
				 }
				 sender.sendMessage( "----------------------------" );
			}else if(what.equalsIgnoreCase( "fuel" )){
				 sender.sendMessage( "Fuels: " + configManager.fuels.size() );
				 sender.sendMessage( "----------------------------" );
				 int i = 0;
				 for( Entry<Integer, Fuel> entry : configManager.fuels.entrySet()){
					 i++;
					 Fuel fuel = entry.getValue();
					 sender.sendMessage(" " + i + ": [" + fuel.configName + "]");
					 sender.sendMessage("     " +"fuelId: " + fuel.block_id);
					 sender.sendMessage("     " +"drillAirSpeed: " + fuel.drillAirSpeed );
					 sender.sendMessage("     " +"drillBlockSpeed: " + fuel.drillBlockSpeed );
					 sender.sendMessage("     " +"fuelConsumptionBlockCount: " + fuel.fuelConsumptionBlockCount );
					 sender.sendMessage("     " +"fuelConsumptionFuelCount: " + fuel.fuelConsumptionFuelCount );
				 }
				 sender.sendMessage( "----------------------------" );
			}else if(what.equalsIgnoreCase( "script" )){
				 sender.sendMessage( "Scripts: " + configManager.scripts.size() );
				 sender.sendMessage( "----------------------------" );
				 int i = 0;
				 for( Entry<String, String> entry : configManager.scripts.entrySet()){
					 i++;
					 sender.sendMessage(" " + i + ": [" + entry.getKey() + "]");
					 sender.sendMessage("     " +"Script: " + entry.getValue() );
				 }
				 sender.sendMessage( "----------------------------" );
			}else{
				
			}
		}else if(commandLabel.equalsIgnoreCase( "pdmake" )){
			String what = args[0];
			
			Toggler fbToggler = new Toggler( "f", "b");
			Toggler rlToggler = new Toggler( "r", "l");
			Toggler udToggler = new Toggler( "u", "d");
			
			if(what.equalsIgnoreCase( "room" )){
				ArrayList<String> script = new ArrayList<String>();
				ArrayList<Integer> drillIds = getDrillIdsFromString( sender, args[1]);
				
				Integer xS,yS,zS;
				Integer x,y,z;
				
				x = Integer.parseInt( args[2] );
				y = Integer.parseInt( args[3] );
				z = Integer.parseInt( args[4] );
				
				if( x < 0){
					fbToggler.toggle();
					x *= -1;
				}
				if( y < 0){
					udToggler.toggle();
					y *= -1;
				}
				if( z < 0){
					rlToggler.toggle();
					z *= -1;
				}
				
				xS = x - 1;
				yS = y;
				zS = z - 1;
				
				y = 1;
				while( y <= yS ){
					
					z = zS;
					while( z >= 0 ){
						
						script.add( fbToggler.data() + xS);
						if( z > 0 )
							script.add( rlToggler.data() + 1);
						
						fbToggler.toggle();
						z--;
					}
					if( y < yS )
						script.add( udToggler.data() + 1);
					
					rlToggler.toggle();
					y++;
				}
				
				sendScripts(sender, drillIds, script);
			}
		}else if(commandLabel.equalsIgnoreCase( "pdplace" )){
			ArrayList<Integer> drillIds = getDrillIdsFromString( sender, args[0]);
			
			for( Integer id : drillIds ){
				Drill drill = drillManager.getDrillFromId( id );
				
				if(drill != null){
					if( !drill.isVirtual ){
						if(drill.blockPlaceManager == null){
							drill.blockPlaceManager = new BlockPlaceManager( drill, Integer.parseInt(args[1]), args[2]);
							sender.sendMessage(prefix + "Place job created for drill ["+ id +"]");
						}else{
							drill.blockPlaceManager.blockPlaceInterval = Integer.parseInt(args[1]);
							drill.blockPlaceManager.placeDirection = args[2];
							sender.sendMessage(prefix + "Place job updated for drill ["+ id +"]");
						}
					}else{
						sender.sendMessage(prefix + "Drill with id ["+ id +"] is LinkDrill!");
					}
				}else{
					sender.sendMessage(prefix + "No such drill id ["+ id +"]");
				}
			}
		}

		return true;
	}
	
	private ArrayList<Integer> getDrillIdsFromString(CommandSender sender, String string) {
		ArrayList<Integer> drillIds = new ArrayList<Integer>();
		
		String[] idArgs = string.split( "," );
		Integer argCount = idArgs.length;
		for(Integer i = 0; i < argCount; i++){
			if(argCount >= 2 && i <= argCount - 3 && idArgs[i + 1].equals( "..." ) ){
				Integer start = null;
				Integer end = null;
				try {
					start = Integer.parseInt( idArgs[i] );
					end = Integer.parseInt( idArgs[i + 2] );
				} catch (NumberFormatException e) {
			 		sender.sendMessage(prefix + idArgs[i] + " or " + idArgs[i + 2] + " is not a valid number");
			 		break;
				}finally {
					for(Integer j = start; j <= end; j++){
						drillIds.add( j  );
					}
					i += 2;
				}
			}else{
				Integer id = null;
			 	try {
			 		id = Integer.parseInt( idArgs[i] );
			 		drillIds.add( id  );
			 	} catch (NumberFormatException e) {
			 		sender.sendMessage(prefix + idArgs[i] + " is not a valid number");
			 		break;
			 	}
			}
		}
		
		return drillIds;
	}

	private void sendScripts(CommandSender sender, ArrayList<Integer> drillIds, ArrayList<String> script) {
		Integer ret;
		for(Integer drillId : drillIds){
			Drill drill;
			if(drillId < 0){
				drill = drillManager.getLinkDrillFromId( -drillId );
			}else{
				drill = drillManager.getDrillFromId( drillId );
			}

			ret = -1;
			
			if( drill != null){
				if(!drill.linked){
					if(drill.enabled){
						ret = drill.JobMG.addScript( script );
					} else {
						sender.sendMessage(prefix + "["+ drillId +"] Drill is disabled!");
					}
				}else{
					sender.sendMessage(prefix + "["+ drillId +"] Drill is linked!");
				}
			} else {
				sender.sendMessage(prefix + "No such drill id ["+ drillId +"]");
			}
				
			if(ret == 1){
				sender.sendMessage(prefix + "["+ drillId +"] Script compiling passed!");
			}else if(ret == 0){
				sender.sendMessage(prefix + "["+ drillId +"] Script compiling failed!");
			}else if(ret == 2){
				sender.sendMessage(prefix + "["+ drillId +"] Drill is allready running a script!");
			}
		}
	}
}
