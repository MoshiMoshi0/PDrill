package me.devcom.pdrill;


import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

	public final ConfigurationManager configManager = new ConfigurationManager(this, "config.yml", "plugins/PDrill/");

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
		
		configManager.load();
		
		Runnable runnable = new Runnable(){
			public void run(){
				plugin.drillManager.updateDrills();
			}
		};
		sheduler.scheduleSyncRepeatingTask( plugin, runnable, (long)(configManager.blockSpeed * 20), (long)(configManager.blockSpeed * 20));
		
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info( prefix + pdfFile.getName() + " [ver: " + pdfFile.getVersion() + "] is enabled!");
	}
	
	//@Override
	public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args){
		return commandProcessor.process( sender, commandLabel, args);
	}
}