package me.minecraftauth.lib.account.platform.patreon;

import me.minecraftauth.lib.account.Account;
import me.minecraftauth.lib.account.AccountType;

public class PatreonAccount extends Account {

    private final int uid;

    public PatreonAccount(int uid) {
        this.uid = uid;
    }

    @Override
    public String getIdentifier() {
        return String.valueOf(uid);
    }

    public int getUid() {
        return uid;
    }

    @Override
    public AccountType getType() {
        return AccountType.PATREON;
    }

}
