package me.devcom.pdrill;

public class Fuel {
    public Integer block_id;
    public Integer drillAirSpeed;
    public Integer drillBlockSpeed;
    public Integer fuelConsumptionBlockCount;
    public Integer fuelConsumptionFuelCount;
    public String configName;

    public Fuel(Integer bid, Integer das, Integer dbs, Integer bc, Integer fc,
            String name) {
        block_id = bid;
        drillAirSpeed = das;
        drillBlockSpeed = dbs;
        fuelConsumptionBlockCount = bc;
        fuelConsumptionFuelCount = fc;
        configName = name;
    }

    public Integer getBlock_id() {
        return block_id;
    }

    public Integer getDrillAirSpeed() {
        return drillAirSpeed;
    }

    public Integer getDrillBlockSpeed() {
        return drillBlockSpeed;
    }

    public Integer getFuelConsumptionBlockCount() {
        return fuelConsumptionBlockCount;
    }

    public Integer getFuelConsumptionFuelCount() {
        return fuelConsumptionFuelCount;
    }

    public String getConfigName() {
        return configName;
    }
}
