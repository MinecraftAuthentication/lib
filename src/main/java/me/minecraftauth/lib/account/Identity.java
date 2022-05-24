package me.minecraftauth.lib.account;

import me.minecraftauth.lib.account.platform.discord.DiscordAccount;
import me.minecraftauth.lib.account.platform.glimpse.GlimpseAccount;
import me.minecraftauth.lib.account.platform.google.GoogleAccount;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.lib.account.platform.patreon.PatreonAccount;
import me.minecraftauth.lib.account.platform.twitch.TwitchAccount;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Identity {

    private final Map<AccountType, Account> accounts;

    public Identity(Map<AccountType, Account> accounts) {
        this.accounts = accounts;
    }

    public Map<AccountType, Account> getAccounts() {
        return accounts;
    }
    public DiscordAccount getDiscordAccount() {
        return (DiscordAccount) accounts.get(AccountType.DISCORD);
    }
    public MinecraftAccount getMinecraftAccount() {
        return (MinecraftAccount) accounts.get(AccountType.MINECRAFT);
    }
    public PatreonAccount getPatreonAccount() {
        return (PatreonAccount) accounts.get(AccountType.PATREON);
    }
    public GoogleAccount getGoogleAccount() {
        return (GoogleAccount) accounts.get(AccountType.GOOGLE);
    }
    public GlimpseAccount getGlimpseAccount() {
        return (GlimpseAccount) accounts.get(AccountType.GLIMPSE);
    }
    public TwitchAccount getTwitchAccount() {
        return (TwitchAccount) accounts.get(AccountType.TWITCH);
    }

    @Override
    public String toString() {
        return "Identity{" + Arrays.stream(AccountType.values())
                .filter(accounts::containsKey)
                .map(accounts::get)
                .map(Account::toString)
                .collect(Collectors.joining(", "))
            + "}";
    }

}
