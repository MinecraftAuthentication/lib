package me.minecraftauth.lib.account.platform.google;

import me.minecraftauth.lib.account.Account;
import me.minecraftauth.lib.account.AccountType;

public class GoogleAccount extends Account {

    private final String id;

    public GoogleAccount(String id) {
        this.id = id;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public AccountType getType() {
        return AccountType.GOOGLE;
    }

}
