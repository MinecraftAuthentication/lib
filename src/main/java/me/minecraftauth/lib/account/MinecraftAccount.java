package me.minecraftauth.lib.account;

import java.util.UUID;

public class MinecraftAccount extends Account {

    private final UUID uuid;

    public MinecraftAccount(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getIdentifier() {
        return uuid.toString();
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public AccountType getType() {
        return AccountType.MINECRAFT;
    }

}
