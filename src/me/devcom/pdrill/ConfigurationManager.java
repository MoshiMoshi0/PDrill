package me.devcom.pdrill;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigurationManager {
    // private static final String CONFIG_HEADER = "";

    public File configFile;

    public final Logger logger = Logger.getLogger("Minecraft");
    public final String prefix = "[PDrill] ";
    String fileName;
    String fileDir;

    // Scripts< name, script>
    public HashMap<String, String> scripts = new HashMap<String, String>();
    public HashMap<Integer, Fuel> fuels = new HashMap<Integer, Fuel>();
    public HashMap<Integer, Integer> drops = new HashMap<Integer, Integer>();
    public HashMap<Integer, Integer> placeCosts = new HashMap<Integer, Integer>();

    public double blockSpeed = 0.1;

    public boolean dropItemNaturally;
    public List<Integer> dropItemList;
    public List<Integer> stopblocks = new ArrayList<Integer>();

    public boolean checkItemChange;

    private FileConfiguration config;

    private JavaPlugin plugin;

    public ConfigurationManager(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void load() {

        dropItemNaturally = config
                .getBoolean("config.dropItemNaturally", false);
        dropItemList = config.getIntegerList("config.dropItemList");
        checkItemChange = config.getBoolean("config.checkItemChange", false);
        blockSpeed = config.getDouble("config.speed", 0.1);
        stopblocks = config.getIntegerList("config.stopBlocks");

        loadPlaceCosts();
        loadDrops();
        loadScripts();
        loadFuels();

        logger.info(prefix + "Config loaded!");
    }

    private void loadPlaceCosts() {
        List<String> costList = config.getStringList("config.itemPlaceCost");
        if (costList != null) {
            for (String dropNode : costList) {
                String[] splitNode = dropNode.split(";");
                Integer fuel = Integer.parseInt(splitNode[0]);
                Integer cost = Integer.parseInt(splitNode[1]);

                placeCosts.put(fuel, cost);
            }
            logger.finer(prefix + "Drops loaded!");
        }
    }

    public void loadDrops() {
        List<String> dropList = config.getStringList("config.dropItemChange");
        if (dropList != null) {
            for (String dropNode : dropList) {
                String[] splitNode = dropNode.split(";");
                Integer sourceId = Integer.parseInt(splitNode[0]);
                Integer targetId = Integer.parseInt(splitNode[1]);

                logger.finer(prefix + "Id [" + sourceId + "] converts to ["
                        + targetId + "]");
                drops.put(sourceId, targetId);
            }
            logger.finer(prefix + "Drops loaded!");
        }
    }

    public void loadScripts() {
        ConfigurationSection section = config.getConfigurationSection("script");
        Set<String> scriptList = section.getKeys(false);
        if (scriptList != null) {
            for (String scriptNode : scriptList) {
                String path = "script." + scriptNode + ".";
                String name = scriptNode;
                String script = config.getString(path + "script");

                logger.finest(prefix + "New script added! [" + name + "]["
                        + script + "]");
                scripts.put(name, script);
            }
            logger.finer(prefix + "Scripts loaded!");
        }
    }

    public void loadFuels() {
        ConfigurationSection section = config.getConfigurationSection("fuel");
        Set<String> fuelList = section.getKeys(false);
        if (fuelList != null) {
            for (String fuelNode : fuelList) {
                String path = "fuel." + fuelNode + ".";
                Integer fuelId = config.getInt(path + "fuelId", -1);
                Integer drillAirSpeed = config.getInt(path + "drillAirSpeed",
                        -1);
                Integer drillBlockSpeed = config.getInt(path
                        + "drillBlockSpeed", -1);
                Integer fuelConsumptionBlockCount = config.getInt(path
                        + "fuelConsumptionBlockCount", -1);
                Integer fuelConsumptionFuelCount = config.getInt(path
                        + "fuelConsumptionFuelCount", -1);
                Fuel fuel = new Fuel(fuelId, drillAirSpeed, drillBlockSpeed,
                        fuelConsumptionBlockCount, fuelConsumptionFuelCount,
                        fuelNode);

                logger.finest(prefix + "New fuel added! [" + fuelId + " "
                        + drillAirSpeed + " " + drillBlockSpeed + " "
                        + fuelConsumptionBlockCount + " "
                        + fuelConsumptionFuelCount + "]");
                fuels.put(fuelId, fuel);
            }
            logger.finer(prefix + "Fuels loaded!");
        }
    }

    public String getScriptByName(String scriptName) {
        if (scripts.containsKey(scriptName)) {
            return scripts.get(scriptName);
        } else {
            return "";
        }
    }

    public Fuel getFuelByName(String string) {
        for (Entry<Integer, Fuel> entry : fuels.entrySet()) {
            if (entry.getValue().configName.equalsIgnoreCase(string)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void saveScript(String name, String content) {
        config.set("script." + name + ".script", content);
        this.scripts.put(name, content);
        this.plugin.saveConfig();
    }

    public void saveFuel(Fuel f) {
        config.set("fuel." + f.getConfigName() + ".fuelId", f.getBlock_id());
        config.set("fuel." + f.getConfigName() + ".drillAirSpeed",
                f.getDrillAirSpeed());
        config.set("fuel." + f.getConfigName() + ".drillBlockSpeed",
                f.getDrillBlockSpeed());
        config.set("fuel." + f.getConfigName() + ".fuelConsumptionBlockCount",
                f.getFuelConsumptionBlockCount());
        config.set("fuel." + f.getConfigName() + ".fuelConsumptionFuelCount",
                f.getFuelConsumptionFuelCount());
        this.fuels.put(f.getBlock_id(), f);
        this.plugin.saveConfig();
    }

}
