package me.minecraftauth.lib;

public enum SubTier {

    LEVEL1(1000),
    LEVEL2(2000),
    LEVEL3(3000);

    private final int value;

    SubTier(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
