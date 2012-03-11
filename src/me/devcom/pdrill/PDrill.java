package me.devcom.pdrill;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class PDrill extends JavaPlugin {

    public BukkitScheduler sheduler;

    public ConfigurationManager configManager;
    public final DrillManager drillManager = new DrillManager(this);
    private final CommandProcessor commandProcessor = new CommandProcessor(this);

    public final DrillBlockListener blockListener = new DrillBlockListener(this);
    public final DrillPlayerListener playerListener = new DrillPlayerListener(
            this);
    public final Logger logger = Logger.getLogger("Minecraft");
    public final String prefix = "[PDrill] ";

    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        this.logger.info(pdfFile.getName() + " [ver: " + pdfFile.getVersion()
                + "] is disabled!");
    }

    @Override
    public void onEnable() {
        final PDrill plugin = this;
        sheduler = this.getServer().getScheduler();
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);

        File dir = new File("plugins/PDrill");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        configManager = new ConfigurationManager(this, getConfig());
        configManager.load();

        Runnable runnable = new Runnable() {
            public void run() {
                plugin.drillManager.updateDrills();
            }
        };
        sheduler.scheduleSyncRepeatingTask(plugin, runnable,
                (long) (configManager.blockSpeed * 20),
                (long) (configManager.blockSpeed * 20));

        PluginDescriptionFile pdfFile = this.getDescription();
        logger.info(prefix + pdfFile.getName() + " [ver: "
                + pdfFile.getVersion() + "] is enabled!");
    }

    // @Override
    public boolean onCommand(CommandSender sender, Command cmd,
            String commandLabel, String[] args) {
        return commandProcessor.process(sender, commandLabel, args);
    }
}