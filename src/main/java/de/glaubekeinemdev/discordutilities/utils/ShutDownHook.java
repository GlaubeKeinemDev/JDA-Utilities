package de.glaubekeinemdev.discordutilities.utils;

import de.glaubekeinemdev.discordutilities.DiscordBot;

public class ShutDownHook extends Thread {

    private DiscordBot discordBot;

    public ShutDownHook(final DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void run() {
        discordBot.getJda().shutdownNow();
        discordBot.getDataBaseManager().saveAllDataBases();
        //MenuHelper.getInstance().shutdown();
    }

    @Override
    public synchronized void start() {
        discordBot.getJda().shutdownNow();
        discordBot.getDataBaseManager().saveAllDataBases();
        //MenuHelper.getInstance().shutdown();
    }
}
