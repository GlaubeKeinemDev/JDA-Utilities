package de.glaubekeinemdev.discordutilities.commands;

import de.glaubekeinemdev.discordutilities.DiscordBot;
import de.glaubekeinemdev.discordutilities.commands.core.Command;
import de.glaubekeinemdev.discordutilities.utils.AbstractEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends Command {

    private final DiscordBot discordBot;

    public HelpCommand(String[] neededPermissionRoles, List<String> availableChannels, DiscordBot discordBot) {
        super(neededPermissionRoles, availableChannels);
        this.discordBot = discordBot;
    }

    @Override
    public List<String> alias() {
        return new ArrayList<>();
    }

    @Override
    public String commandName() {
        return "help";
    }

    @Override
    public String description() {
        return "Zeigt verfügbare Commands an";
    }

    @Override
    public void execute(String[] args, String command, Member commandSender, TextChannel channel, Message message) {
        AbstractEmbedBuilder embedBuilder = discordBot.getEmbedBuilder().setTitle("Verfügbare Commands").setDefaultFooter(commandSender);

        discordBot.getCommandCore().getCommands().forEach(eachCommand -> {
            if(!eachCommand.commandName().equals("help")) {
                if(eachCommand.hasPermission(commandSender)) {
                    String aliases = "";

                    if(!eachCommand.alias().isEmpty()) {
                        final StringBuilder stringBuilder = new StringBuilder();

                        eachCommand.alias().forEach(eachAlias -> stringBuilder.append("• ").append(discordBot
                                .getCommandCore().getCommandPrefix()).append(eachAlias).append("\n"));

                        aliases = stringBuilder.toString();
                    }

                    embedBuilder.addField(discordBot.getCommandCore().getCommandPrefix() + eachCommand.commandName()
                                    + " " + (eachCommand.getArguments() != null ? eachCommand.getArguments() : ""),
                            aliases + "\n" + eachCommand.description());
                }
            }
        });

        channel.sendMessage(embedBuilder.build()).queue();
        message.delete().queue();
    }
}
