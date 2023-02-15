package de.glaubekeinemdev.discordutilities.utils;

import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiscordUtility {

    public static Collection<GatewayIntent> getGateWayIntents() {
        List<GatewayIntent> intents = new ArrayList<>();
        intents.add(GatewayIntent.GUILD_MEMBERS);
        intents.add(GatewayIntent.MESSAGE_CONTENT);
        intents.add(GatewayIntent.GUILD_MESSAGES);
        intents.add(GatewayIntent.GUILD_MESSAGE_TYPING);
        intents.add(GatewayIntent.DIRECT_MESSAGES);
        intents.add(GatewayIntent.DIRECT_MESSAGE_TYPING);
        intents.add(GatewayIntent.GUILD_PRESENCES);
        intents.add(GatewayIntent.GUILD_VOICE_STATES);
        intents.add(GatewayIntent.GUILD_EMOJIS_AND_STICKERS);
        intents.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        return intents;
    }

    public static Collection<CacheFlag> getCacheFlags() {
        List<CacheFlag> cacheFlags = new ArrayList<>();
        cacheFlags.add(CacheFlag.EMOJI);
        cacheFlags.add(CacheFlag.STICKER);
        cacheFlags.add(CacheFlag.VOICE_STATE);
        cacheFlags.add(CacheFlag.ACTIVITY);
        cacheFlags.add(CacheFlag.CLIENT_STATUS);
        cacheFlags.add(CacheFlag.MEMBER_OVERRIDES);
        return cacheFlags;
    }

}
