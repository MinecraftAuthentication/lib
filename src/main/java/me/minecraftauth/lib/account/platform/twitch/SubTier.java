package me.minecraftauth.lib.account.platform.twitch;

public class SubTier {

    /**
     * Constructs a SubTier for the given tier level.
     * @param level the sub tier level
     * @return a SubTier with the given level
     */
    public static SubTier level(int level) {
        return new SubTier(level * 1000);
    }

    /**
     * Constructs a SubTier for the given raw value. Twitch expects this to be in thousands, i.e., level 3 is 3000.
     * @param value the raw tier value
     * @return a SubTier with the given value
     */
    public static SubTier raw(int value) {
        return new SubTier(value);
    }

    private final int value;

    private SubTier(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
