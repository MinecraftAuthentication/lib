package me.minecraftauth.lib;

import alexh.weak.Dynamic;
import com.github.kevinsawicki.http.HttpRequest;
import me.minecraftauth.lib.account.Account;
import me.minecraftauth.lib.account.AccountType;
import me.minecraftauth.lib.account.Identity;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.lib.account.platform.twitch.SubTier;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.function.Predicate;

public class AuthService {

    private static final JSONParser JSON_PARSER = new JSONParser();

    // prevent instantiation
    private AuthService() {}

    private static boolean expectResponseBody(HttpRequest request, Predicate<Dynamic> predicate) throws LookupException {
        int status = request.code();
        String body = request.body();

        if (status / 100 == 2) {
            try {
                Dynamic response = Dynamic.from(JSON_PARSER.parse(body));
                return predicate.test(response);
            } catch (HttpRequest.HttpRequestException | ParseException e) {
                throw new LookupException("Failed to parse API response", e);
            }
        } else {
            throw new LookupException("MinecraftAuth server returned bad response: " + status + " / " + body);
        }
    }
    private static boolean expectTrue(HttpRequest request) throws LookupException {
        return expectResponseBody(request, dynamic -> dynamic.dget("result").convert().intoString().equals("true"));
    }

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
        String body = request.body();

        if (request.code() / 100 == 2) {
            try {
                Dynamic response = Dynamic.from(JSON_PARSER.parse(body));

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
            throw new LookupException("MinecraftAuth server returned bad code: " + request.code() + " / " + body);
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
        String body = request.body();

        if (request.code() / 100 == 2) {
            try {
                Dynamic response = Dynamic.from(JSON_PARSER.parse(body));
                String id = response.dget(to.name().toLowerCase() + ".identifier").convert().intoString();
                return Optional.of(Account.from(to, id));
            } catch (HttpRequest.HttpRequestException | ParseException e) {
                throw new LookupException("Failed to parse API response", e);
            }
        } else if (request.code() == 404) {
            return Optional.empty();
        } else {
            throw new LookupException("MinecraftAuth server returned bad response: " + request.code() + " / " + body);
        }
    }

    private static boolean isFollowing(String serverToken, AccountType platform, UUID minecraftUuid) throws LookupException {
        HttpRequest request = HttpRequest.get("https://minecraftauth.me/api/following?platform=" + platform.name().toLowerCase() + "&minecraft=" + minecraftUuid)
                .userAgent("MinecraftAuthLib")
                .authorization("Basic " + serverToken);
        return expectTrue(request);
    }

    private static boolean isSubscribed(String serverToken, AccountType platform, UUID minecraftUuid, Object data) throws LookupException {
        HttpRequest request = HttpRequest.get("https://minecraftauth.me/api/subscribed?platform=" + platform.name().toLowerCase() + "&minecraft=" + minecraftUuid + (data != null ? "&data=" + data : ""))
                .userAgent("MinecraftAuthLib")
                .authorization("Basic " + serverToken);
        return expectTrue(request);
    }

    /**
     * Query if the given Discord user ID is in the given Discord server
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @param serverId the Discord server ID to query
     * @return if the given Discord user (and the Minecraft Authentication bot) is in the given server
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isDiscordMemberPresent(String serverToken, UUID minecraftUuid, String serverId) throws LookupException {
        HttpRequest request = HttpRequest.get("https://minecraftauth.me/api/discord/present?minecraft=" + minecraftUuid + "&server=" + serverId)
                .userAgent("MinecraftAuthLib")
                .authorization("Basic " + serverToken);
        return expectTrue(request);
    }
    /**
     * Query if the given Discord user ID has the given Discord role
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @param roleId the Discord role ID to query
     * @return if the given Discord user has the given role
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isDiscordRolePresent(String serverToken, UUID minecraftUuid, String roleId) throws LookupException {
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
     * Query if the given Patreon uid is a patron of the server token's Patreon campaign
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @param tierTitle the title of the requested Patreon tier
     * @return if the given Patreon uid is a patron of the server token's Patreon campaign
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isSubscribedPatreon(String serverToken, UUID minecraftUuid, String tierTitle) throws LookupException {
        return isSubscribed(serverToken, AccountType.PATREON, minecraftUuid, tierTitle);
    }

    /**
     * Query if the given Glimpse user is a sponsor of the server token's Glimpse user
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @return if the given Glimpse user is a sponsor of the server token's Glimpse user
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isSubscribedGlimpse(String serverToken, UUID minecraftUuid) throws LookupException {
        return isSubscribed(serverToken, AccountType.GLIMPSE, minecraftUuid, null);
    }
    /**
     * Query if the given Glimpse user is a sponsor of the server token's Glimpse user
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @param levelName the name of the requested Glimpse level
     * @return if the given Glimpse user is a sponsor of the server token's Glimpse user
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isSubscribedGlimpse(String serverToken, UUID minecraftUuid, String levelName) throws LookupException {
        return isSubscribed(serverToken, AccountType.GLIMPSE, minecraftUuid, levelName);
    }

    /**
     * Query if the given Twitch uid is following the server token's Twitch channel
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @return if the given Twitch uid is following the server token's Twitch channel
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isFollowingTwitch(String serverToken, UUID minecraftUuid) throws LookupException {
        return isFollowing(serverToken, AccountType.TWITCH, minecraftUuid);
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
    /**
     * Query if the given Twitch uid is subbed to the server token's Twitch channel
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @param tier the required tier level to qualify as being subscribed
     * @return if the given Twitch uid is subbed to the server token's Twitch channel
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isSubscribedTwitch(String serverToken, UUID minecraftUuid, SubTier tier) throws LookupException {
        return isSubscribed(serverToken, AccountType.TWITCH, minecraftUuid, tier.getValue());
    }

    /**
     * Query if the player's YouTube account is subscribed to the server token's YouTube channel
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @return if the player's YouTube account is subscribed to the server token's YouTube channel
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isSubscribedYouTube(String serverToken, UUID minecraftUuid) throws LookupException {
        return isFollowing(serverToken, AccountType.GOOGLE, minecraftUuid);
    }
    /**
     * Query if the player's YouTube account is a paid member of the server token's YouTube channel
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @return if the player's YouTube account is a paid member of the server token's YouTube channel
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isMemberYouTube(String serverToken, UUID minecraftUuid) throws LookupException {
        return isSubscribed(serverToken, AccountType.GOOGLE, minecraftUuid, null);
    }
    /**
     * Query if the player's YouTube account is a paid member of the server token's YouTube channel
     * @param serverToken the server authentication token to query data for
     * @param minecraftUuid the Minecraft player UUID to query
     * @param tier the required tier level to qualify as being a member
     * @return if the player's YouTube account is a paid member of the server token's YouTube channel
     * @throws LookupException if the API returns abnormal error code
     */
    public static boolean isMemberYouTube(String serverToken, UUID minecraftUuid, String tier) throws LookupException {
        return isSubscribed(serverToken, AccountType.GOOGLE, minecraftUuid, tier);
    }

}
