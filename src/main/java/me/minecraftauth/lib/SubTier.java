package me.minecraftauth.lib;

public enum SubTier {

    LEVEL1(1000),
    LEVEL2(2000),
    LEVEL3(3000);

    private final int tier;

    SubTier(int tier) {
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

}
