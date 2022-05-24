package me.minecraftauth.lib.account.platform.glimpse;

import me.minecraftauth.lib.account.Account;
import me.minecraftauth.lib.account.AccountType;

public class GlimpseAccount extends Account {

    private final String username;

    public GlimpseAccount(String username) {
        this.username = username;
    }

    @Override
    public String getIdentifier() {
        return username;
    }

    @Override
    public AccountType getType() {
        return AccountType.GLIMPSE;
    }

}
