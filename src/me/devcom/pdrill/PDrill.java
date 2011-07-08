package me.devcom.pdrill;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class PDrill extends JavaPlugin {
	public static PDrill plugin;
	
	public BukkitScheduler sheduler;
	public final DrillManager drillManager = new DrillManager( this );
	public final DrillBlockListener blockListener = new DrillBlockListener( this );
	public final DrillPlayerListener playerListener = new DrillPlayerListener( this );
	public final Logger logger = Logger.getLogger("Minecraft");
	public final String prefix = "[PDrill] ";
	
	public File scriptFile;
	public File configFile;
	public Properties scriptCfg = new Properties();
	public Properties configCfg = new Properties();

	@Override 
	public void onDisable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info( pdfFile.getName() + " [ver: " + pdfFile.getVersion() + "] is disabled!");
	}
	
	@Override 
	public void onEnable(){
		plugin = this;
		
		sheduler = plugin.getServer().getScheduler();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent( Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent( Event.Type.BLOCK_BREAK, this.blockListener, Event.Priority.Normal, this);
		
		File dir = new File( "plugins/PDrill" );
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		scriptFile = loadFile("scripts.yml", "plugins/PDrill/");
		configFile = loadFile("config.yml", "plugins/PDrill/");
		
		loadCfg( scriptCfg, scriptFile);
		loadCfg( configCfg, configFile);
		
		Runnable runnable = new Runnable(){
			public void run(){
				plugin.drillManager.updateDrills();
			}
		};
		sheduler.scheduleSyncRepeatingTask( plugin, runnable, 4, 4);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info( prefix + pdfFile.getName() + " [ver: " + pdfFile.getVersion() + "] is enabled!");
	}
	
	//@Override
	public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args){
		Integer ret = -1;

		if( commandLabel.equalsIgnoreCase("pdscript") || commandLabel.equalsIgnoreCase("pdload")){
			ArrayList<Integer> drillIds = new ArrayList<Integer>();
			ArrayList<String> script = new ArrayList<String>();
			
			String[] idArgs = args[0].split( "," );

			for(Integer i = 0; i < idArgs.length; i++){
				Integer id = Integer.parseInt( idArgs[i] );
				drillIds.add( id  );
			}
			
			if(commandLabel.equalsIgnoreCase("pdscript")){
				for(Integer i = 1; i < args.length; i++){
					script.add( args[i] );
				}
			}else if(commandLabel.equalsIgnoreCase("pdload")){
				String scriptName = args[1];
					
				String scriptString = scriptCfg.getProperty( scriptName );
				String[] scriptArgs = scriptString.split( " " );

				for(Integer i = 0; i < scriptArgs.length; i++){
					script.add( scriptArgs[i] );
				}
			}
			
			for(Integer drillId : drillIds){
				Drill drill = drillManager.getDrillFromId( drillId );
				if( drill != null){
					if(drill.enabled){
						ret = drill.JobMG.addScript( script );
					} else {
						ret = -1;
						sender.sendMessage(prefix + "["+ drillId +"] Drill is disabled!");
					}
				} else {
					ret = -1;
					sender.sendMessage(prefix + "["+ drillId +"] No such drill!");
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
		
		return true;
	}
	
	public File loadFile( String name, String dir ){
		File file = new File( dir + name );
		if(!file.exists()){
			
			try {
				file.createNewFile();
			} catch ( IOException e ){
				logger.info(prefix + "File creation failed! (" + e.toString() + ")");
				return null;
			} finally {
				logger.info(prefix + "File creation succeeded!");
			}
			
		}
		return file;
	}
	
	public boolean loadCfg(Properties cfg, File file ){
		if( file != null ){
			FileInputStream in;
			try {
				in = new FileInputStream( file );
				cfg.load(in);
				in.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}