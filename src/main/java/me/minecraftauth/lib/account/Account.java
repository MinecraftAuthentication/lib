package me.minecraftauth.lib.account;

import me.minecraftauth.lib.AuthService;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.lib.account.platform.discord.DiscordAccount;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.lib.account.platform.patreon.PatreonAccount;
import me.minecraftauth.lib.account.platform.twitch.TwitchAccount;

import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@SuppressWarnings("unchecked")
public abstract class Account {

    public static <T extends Account> T from(AccountType type, String identifier) {
        switch (type) {
            case DISCORD:
                return (T) new DiscordAccount(identifier);
            case MINECRAFT:
                return (T) new MinecraftAccount(UUID.fromString(identifier));
            case PATREON:
                return (T) new PatreonAccount(Integer.parseInt(identifier));
            case TWITCH:
                return (T) new TwitchAccount(Integer.parseInt(identifier));
            default:
                throw new RuntimeException("Unknown account type " + type.name().toLowerCase());
        }
    }
    public <T extends Account> Optional<T> getLinkedAccount(AccountType type) throws LookupException {
        return (Optional<T>) AuthService.lookup(getType(), getIdentifier(), type);
    }

    public abstract String getIdentifier();
    public abstract AccountType getType();

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add(getIdentifier())
                .toString();
    }

}
