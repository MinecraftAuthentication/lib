package me.minecraftauth.lib.account.platform.minecraft;

import me.minecraftauth.lib.account.Account;
import me.minecraftauth.lib.account.AccountType;

import java.util.UUID;

public class MinecraftAccount extends Account {

    private final UUID uuid;
    private final String name;

    public MinecraftAccount(UUID uuid) {
        this(uuid, null);
    }
    public MinecraftAccount(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public String getIdentifier() {
        return uuid.toString();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public AccountType getType() {
        return AccountType.MINECRAFT;
    }

    @Override
    public String toString() {
        return "MinecraftAccount{" +
                (name != null ? "name='" + name + "', " : "") +
                "uuid=" + uuid +
                '}';
    }

}
