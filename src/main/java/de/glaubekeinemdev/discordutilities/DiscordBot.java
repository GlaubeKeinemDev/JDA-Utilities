package de.glaubekeinemdev.discordutilities;

import de.glaubekeinemdev.discordutilities.commands.HelpCommand;
import de.glaubekeinemdev.discordutilities.commands.core.Command;
import de.glaubekeinemdev.discordutilities.commands.core.CommandCore;
import de.glaubekeinemdev.discordutilities.database.DataBaseManager;
import de.glaubekeinemdev.discordutilities.discordlogger.DiscordBotLogger;
import de.glaubekeinemdev.discordutilities.utils.AbstractEmbedBuilder;
import de.glaubekeinemdev.discordutilities.utils.DiscordUtility;
import de.glaubekeinemdev.discordutilities.utils.ShutDownHook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiscordBot {

    private JDA jda;

    private CommandCore commandCore;
    private AbstractEmbedBuilder embedBuilder;
    private DataBaseManager dataBaseManager;
    private Collection<GatewayIntent> intents = new ArrayList<>();
    private Collection<CacheFlag> cacheFlags = new ArrayList<>();


    private DiscordBotLogger logger;

    private String token;
    private OnlineStatus onlineStatus;
    private Activity activity;

    public DiscordBot(final String token) {
        this(token, OnlineStatus.ONLINE, null);
    }

    public DiscordBot(String token, Activity activity) {
        this(token, OnlineStatus.ONLINE, activity);
    }

    public DiscordBot(String token, OnlineStatus onlineStatus) {
        this(token, onlineStatus, null);
    }

    public DiscordBot(String token, OnlineStatus onlineStatus, Activity activity) {
        this.token = token;
        this.onlineStatus = onlineStatus;
        this.activity = activity;
    }

    public void addCustomGateWayIntent(GatewayIntent gatewayIntent) {
        this.intents.add(gatewayIntent);
    }

    public void addCustomCacheFlag(CacheFlag cacheFlag) {
        this.cacheFlags.add(cacheFlag);
    }

    public Collection<CacheFlag> getCacheFlags() {
        Collection<CacheFlag> list = DiscordUtility.getCacheFlags();

        for (CacheFlag cacheFlag : this.cacheFlags) {
            if(!list.contains(cacheFlag))
                list.add(cacheFlag);
        }

        return list;
    }

    public Collection<GatewayIntent> getIntents() {
        Collection<GatewayIntent> list = DiscordUtility.getGateWayIntents();

        for (GatewayIntent intent : this.intents) {
            if(!list.contains(intent))
                list.add(intent);
        }

        return list;
    }

    public JDA start() throws Exception {
        this.jda = JDABuilder.create(token, getIntents())
                .enableCache(getCacheFlags())
                .setStatus(onlineStatus)
                .setActivity(activity)
                .setBulkDeleteSplittingEnabled(true)
                .setAutoReconnect(true)
                .build()
                .awaitReady();

        //new MenuHelper().setup(jda);

        Runtime.getRuntime().addShutdownHook(new ShutDownHook(this));

        return jda;
    }

    public void setupCommandCore(final String commandInvoke) {
        this.commandCore = new CommandCore(commandInvoke, this);

        commandCore.registerCommand(new HelpCommand(null, null, this));

        jda.addEventListener(commandCore);
    }

    public void setupDataBases() {
        this.dataBaseManager = new DataBaseManager();

        dataBaseManager.initialize();
    }

    public void setEmbedBuilder(AbstractEmbedBuilder embedBuilder) {
        this.embedBuilder = embedBuilder;
    }

    public void registerCommand(final Command command) {
        commandCore.registerCommand(command);
    }

    public void registerCommands(final Command... command) {
        for(Command eachCommand : command) {
            commandCore.registerCommand(eachCommand);
        }
    }

    public void unregisterCommand(final String commandName) {
        commandCore.getCommands().removeIf(command -> command.commandName().equals(commandName));
    }

    public void unregisterCommand(final Command command) {
        commandCore.getCommands().remove(command);
    }

    public void unregisterCommands(final ClassLoader classLoader) {
        commandCore.getCommands().forEach(eachCommand -> {
            if(eachCommand.getClass().getClassLoader().equals(classLoader))
                unregisterCommand(eachCommand);
        });
    }

    public void unregisterListener(final Object object) {
        jda.removeEventListener(object);
    }

    public void unregisterListeners(final ClassLoader classLoader) {
        jda.getEventManager().getRegisteredListeners().forEach(eachListener -> {
            if(eachListener.getClass().getClassLoader().equals(classLoader))
                jda.removeEventListener(eachListener);
        });
    }

    public void setupLogger() {
        this.logger = new DiscordBotLogger();
    }

    public JDA getJda() {
        return jda;
    }

    public CommandCore getCommandCore() {
        return commandCore;
    }

    public AbstractEmbedBuilder getEmbedBuilder() {
        embedBuilder.embedBuilder = new EmbedBuilder();
        return embedBuilder;
    }

    public DiscordBotLogger getLogger() {
        return logger;
    }

    public DataBaseManager getDataBaseManager() {
        return dataBaseManager;
    }
}
