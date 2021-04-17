package me.minecraftauth.lib.account;

import me.minecraftauth.lib.account.platform.discord.DiscordAccount;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.lib.account.platform.patreon.PatreonAccount;
import me.minecraftauth.lib.account.platform.twitch.TwitchAccount;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public TwitchAccount getTwitchAccount() {
        return (TwitchAccount) accounts.get(AccountType.TWITCH);
    }

    @Override
    public String toString() {
        List<String> list = new LinkedList<>();
        if (accounts.containsKey(AccountType.DISCORD)) list.add(accounts.get(AccountType.DISCORD).toString());
        if (accounts.containsKey(AccountType.MINECRAFT)) list.add(accounts.get(AccountType.MINECRAFT).toString());
        if (accounts.containsKey(AccountType.PATREON)) list.add(accounts.get(AccountType.PATREON).toString());
        if (accounts.containsKey(AccountType.TWITCH)) list.add(accounts.get(AccountType.TWITCH).toString());
        return "Identity{" + String.join(", ", list) + "}";
    }

}
