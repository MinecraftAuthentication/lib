package me.minecraftauth.lib.account.platform.discord;

import me.minecraftauth.lib.account.Account;
import me.minecraftauth.lib.account.AccountType;

public class DiscordAccount extends Account {

    private final String userId;

    public DiscordAccount(String userId) {
        this.userId = userId;
    }

    @Override
    public String getIdentifier() {
        return userId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public AccountType getType() {
        return AccountType.DISCORD;
    }

}
