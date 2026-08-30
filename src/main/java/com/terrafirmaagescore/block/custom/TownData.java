package com.terrafirmaagescore.block.custom;

public class TownData {
    private final String town_name;
    private final int population;

    public TownData(String town_name, int population) {
        this.town_name = town_name;
        this.population = population;
    }

    public String getTownName() { return this.town_name; }
    public int getPopulation() { return this.population; }
}
