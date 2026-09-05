package com.terrafirmaagescore.block.custom;

public class TownData {
    private final String town_name;
    private final int population;
    private final String road1;
    private final String road2;
    private final String road3;
    private final String road4;
    private final String road5;

    public TownData(String town_name, int population, String road1, String road2, String road3, String road4, String road5) {
        this.town_name = town_name;
        this.population = population;
        this.road1 = road1;
        this.road2 = road2;
        this.road3 = road3;
        this.road4 = road4;
        this.road5 = road5;
    }

    public String getTownName() { return this.town_name; }
    public String getRoad1() { return this.road1; }
    public String getRoad2() { return this.road2; }
    public String getRoad3() { return this.road3; }
    public String getRoad4() { return this.road4; }
    public String getRoad5() { return this.road5; }
    public int getPopulation() { return this.population; }
}
