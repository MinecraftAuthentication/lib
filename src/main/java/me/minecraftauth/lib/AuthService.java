package me.minecraftauth.lib;

import alexh.weak.Dynamic;
import com.github.kevinsawicki.http.HttpRequest;
import me.minecraftauth.lib.account.Account;
import me.minecraftauth.lib.account.AccountType;
import me.minecraftauth.lib.account.Identity;
import me.minecraftauth.lib.exception.LookupException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

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

        if (request.code() / 100 == 2) {
            try {
                Dynamic response = Dynamic.from(JSON_PARSER.parse(request.body()));

                Map<AccountType, Account> identifiers = new HashMap<>();
                response.children().forEach(dynamic -> Arrays.stream(AccountType.values())
                        .filter(accountType -> accountType.name().equalsIgnoreCase(dynamic.key().convert().intoString()))
                        .findFirst().ifPresent(type -> identifiers.put(type, Account.from(type, dynamic.dget("identifier").convert().intoString()))));
                return Optional.of(new Identity(identifiers));
            } catch (HttpRequest.HttpRequestException | ParseException e) {
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
            } catch (HttpRequest.HttpRequestException | ParseException e) {
                throw new LookupException("Failed to parse API response", e);
            }
        } else if (request.code() == 404) {
            return Optional.empty();
        } else {
            throw new LookupException("MinecraftAuth server returned bad code: " + request.code());
        }
    }

    /**
     * Query if the given Discord user ID has the given Discord role
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @param roleId the Discord role ID to query
     * @return if the given Discord user has the given role
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isSubscribedDiscord(String serverToken, UUID minecraftUuid, String roleId) throws LookupException {
        return isSubscribed(serverToken, AccountType.DISCORD, minecraftUuid, roleId);
    }
    /**
     * Query if the given Patreon uid is a patron of the server token's Patreon campaign
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @return if the given Patreon uid is a patron of the server token's Patreon campaign
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isSubscribedPatreon(String serverToken, UUID minecraftUuid) throws LookupException {
        return isSubscribed(serverToken, AccountType.PATREON, minecraftUuid, null);
    }
    /**
     * Query if the given Twitch uid is subbed to the server token's Twitch channel
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @return if the given Twitch uid is subbed to the server token's Twitch channel
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isSubscribedTwitch(String serverToken, UUID minecraftUuid) throws LookupException {
        return isSubscribed(serverToken, AccountType.TWITCH, minecraftUuid, null);
    }
    private static boolean isSubscribed(String serverToken, AccountType platform, UUID minecraftUuid, Object data) throws LookupException {
        HttpRequest request = HttpRequest.get("https://minecraftauth.me/api/subscribed/?platform=" + platform.name().toLowerCase() + "&minecraft=" + minecraftUuid + (data != null ? "&role=" + data : ""))
                .userAgent("MinecraftAuthLib")
                .authorization("Basic " + serverToken);

        if (request.code() / 100 == 2) {
            try {
                Dynamic response = Dynamic.from(JSON_PARSER.parse(request.body()));
                return response.dget("subscribed").convert().intoString().equals("true");
            } catch (HttpRequest.HttpRequestException | ParseException e) {
                throw new LookupException("Failed to parse API response", e);
            }
        } else {
            throw new LookupException("MinecraftAuth server returned bad code: " + request.code());
        }
    }

}
