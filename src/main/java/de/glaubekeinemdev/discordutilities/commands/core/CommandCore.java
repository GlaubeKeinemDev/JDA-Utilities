package de.glaubekeinemdev.discordutilities.commands.core;

import de.glaubekeinemdev.discordutilities.DiscordBot;
import de.glaubekeinemdev.discordutilities.utils.AbstractEmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandCore extends ListenerAdapter {

    private final DiscordBot discordBot;

    private String commandPrefix;
    private CopyOnWriteArrayList<Command> commands = new CopyOnWriteArrayList<>();

    public CommandCore(String commandInvoke, DiscordBot discordBot) {
        this.commandPrefix = commandInvoke;
        this.discordBot = discordBot;
    }

    public CopyOnWriteArrayList<Command> getCommands() {
        return commands;
    }

    public void registerCommand(final Command command) {
        commands.add(command);
    }

    public boolean handleInput(final String line, final Member sender, final Guild guild, final TextChannel channel, final Message sentMessage) {
        if(!line.isEmpty() && Character.toString(line.charAt(0)).equals(commandPrefix)) {
            String[] message = line.split(" ");

            String command = message[0];

            final String commandName = command.replace(commandPrefix, "");

            String[] args = new String[]{};

            if(!line.replace(command, "").isEmpty()) {
                args = line.replace(command, "").substring(1).split(" ");
            }

            for(Command allCommands : commands) {
                if(command.startsWith(commandPrefix) && (allCommands.commandName().equalsIgnoreCase(commandName)
                        || allCommands.alias().contains(commandName.toLowerCase()))) {

                    if(!allCommands.hasPermission(sender)) {
                        if(allCommands.getAvailableChannels() == null) {
                            sendNoPermission(channel, sender, commandName);
                            return true;
                        }

                        if(allCommands.getAvailableChannels() != null && allCommands.getAvailableChannels().contains(channel.getId())) {
                            sendNoPermission(channel, sender, commandName);
                            return true;
                        }

                        sendNoPermission(channel, sender, commandName);
                        return false;
                    }

                    if(allCommands.getAvailableChannels() == null) {
                        System.out.println("Command " + commandPrefix + allCommands.commandName() + " executed by " + sender.getUser().getAsTag() + " in channel " + channel.getName());
                        allCommands.execute(args, commandName, sender, channel, sentMessage);
                        return true;
                    }

                    if(allCommands.getAvailableChannels() != null && allCommands.getAvailableChannels().contains(channel.getId())) {
                        System.out.println("Command " + commandPrefix + allCommands.commandName() + " executed by " + sender.getUser().getAsTag() + " in channel " + channel);
                        allCommands.execute(args, commandName, sender, channel, sentMessage);
                        return true;
                    }

                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void sendNoPermission(final TextChannel textChannel, final Member member, final String command) {
        final AbstractEmbedBuilder embedBuilder = discordBot.getEmbedBuilder().setTitle("Fehlende Berechtigung")
                .setDefaultFooter(member).setColor(Color.RED);

        textChannel.sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot())
            return;

        handleInput(event.getMessage().getContentRaw().trim(), event.getMember(), event.getGuild(), event.getChannel(), event.getMessage());
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
