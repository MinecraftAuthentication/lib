package me.minecraftauth.lib.account.platform.twitch;

import me.minecraftauth.lib.account.Account;
import me.minecraftauth.lib.account.AccountType;

public class TwitchAccount extends Account {

    private final int uid;

    public TwitchAccount(int uid) {
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
        return AccountType.TWITCH;
    }

}
