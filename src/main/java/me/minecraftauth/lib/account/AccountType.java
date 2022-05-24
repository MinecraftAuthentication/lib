package me.minecraftauth.lib.account;

import java.util.StringJoiner;

public enum AccountType {

    DISCORD,
    MINECRAFT,
    PATREON,
    TWITCH,
    GLIMPSE,
    GOOGLE;

    @Override
    public String toString() {
        return new StringJoiner(", ", AccountType.class.getSimpleName() + "[", "]")
                .add(name().charAt(0) + name().toLowerCase().substring(1))
                .toString();
    }

}
