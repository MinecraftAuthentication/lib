package me.minecraftauth.lib;

import alexh.weak.Dynamic;
import com.github.kevinsawicki.http.HttpRequest;
import me.minecraftauth.lib.account.Account;
import me.minecraftauth.lib.account.AccountType;
import me.minecraftauth.lib.account.Identity;
import me.minecraftauth.lib.exception.LookupException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthService {

    private static final JSONParser JSON_PARSER = new JSONParser();

    /**
     * Look up all of an identifier's linked accounts
     * @param from the account type to query by
     * @param identifier the identifier for the "from" account to query
     * @return map with entries for each linked account type with the corresponding account
     * @throws LookupException if the API returns abnormal error code
     */
    public static Optional<Identity> lookup(AccountType from, Object identifier) throws LookupException {
        HttpRequest request = HttpRequest.get("https://minecraftauth.me/api/lookup?" + from.name().toLowerCase() + "=" + identifier)
                .userAgent("MinecraftAuthLib");

        if (request.code() >= 200 && request.code() <= 299) {
            try {
                Dynamic response = Dynamic.from(JSON_PARSER.parse(request.body()));

                Map<AccountType, Account> identifiers = new HashMap<>();
                response.children().forEach(dynamic -> Arrays.stream(AccountType.values())
                        .filter(accountType -> accountType.name().equalsIgnoreCase(dynamic.key().convert().intoString()))
                        .findFirst().ifPresent(type -> identifiers.put(type, Account.from(type, dynamic.dget("identifier").convert().intoString()))));
                return Optional.of(new Identity(identifiers));
            } catch (ParseException e) {
                throw new LookupException("Failed to parse API response", e);
            }
        } else if (request.code() == 404) {
            return Optional.empty();
        } else {
            throw new LookupException("MinecraftAuth server returned bad code: " + request.code());
        }
    }

    /**
     * Look up an identifier's linked account for the given type
     * @param from the account type to query by
     * @param identifier the identifier for the "from" account to query
     * @param to the account type desired for the resulting {@link Account}
     * @return {@link Optional<Account>} for the account, empty if non-existent
     * @throws LookupException if the API returns abnormal error code
     */
    public static Optional<Account> lookup(AccountType from, Object identifier, AccountType to) throws LookupException {
        HttpRequest request = HttpRequest.get("https://minecraftauth.me/api/lookup/" + to.name().toLowerCase() + "?" + from.name().toLowerCase() + "=" + identifier)
                .userAgent("MinecraftAuthLib");

        if (request.code() / 100 == 2) {
            try {
                Dynamic response = Dynamic.from(JSON_PARSER.parse(request.body()));
                String id = response.dget(to.name().toLowerCase() + ".identifier").convert().intoString();
                return Optional.of(Account.from(to, id));
            } catch (ParseException e) {
                throw new LookupException("Failed to parse API response", e);
            }
        } else if (request.code() == 404) {
            return Optional.empty();
        } else {
            throw new LookupException("MinecraftAuth server returned bad code: " + request.code());
        }
    }

}
