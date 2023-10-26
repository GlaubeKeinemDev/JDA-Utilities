package de.glaubekeinemdev.discordutilities.commands.core;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

public abstract class Command {

    private String[] neededPermissionRoles;

    private List<String> availableChannels;

    private String arguments;

    public Command(String[] neededPermissionRoles, List<String> availableChannels) {
        this.neededPermissionRoles = neededPermissionRoles;
        this.availableChannels = availableChannels;
    }

    /**
     * List with aliases without command prefix (/, !, etc)
     */
    public abstract List<String> alias();

    /**
     * CommandName without Prefix (/, !, etc)
     */
    public abstract String commandName();

    /**
     * Command description could be null
     */
    public abstract String description();

    /**
     *  Called when the command is executed
     *
     * @param args the arguments, the command was executed with
     * @param command the actual executed command (without arguments) as string
     * @param commandSender the sender as discord member
     * @param channel the textchannel the command was written in
     * @param message the discord message object of the whole command with arguments and included prefix
     */
    public abstract void execute(final String[] args, final String command, final Member commandSender, final MessageChannelUnion channel, final Message message);

    /**
     * This method is executed when the command got written in the wrong channel
     *
     * @param command the written command without arguments and prefix as a string
     * @param commandSender the sender as discord member
     * @param channel the textchannel the command was written in
     * @param message the discord message object of the whole command with arguments and included prefix
     */
    public void wrongChannel(final String command, final Member commandSender, final MessageChannelUnion channel, final Message message) {
    }

    /**
     * This is a permission check with checks if the user is able to execute the command
     *
     * @param member the user to check the permission
     * @return returns true if the user has the needed permission
     */
    public boolean hasPermission(final Member member) {
        if(neededPermissionRoles == null)
            return true;

        for(String neededPermissionRole : neededPermissionRoles) {
            Role needRole = member.getGuild().getRoleById(neededPermissionRole);

            if(member.getRoles().contains(needRole)) {
                return true;
            }
        }

        return false;
    }

    public String[] getNeededPermissionRoles() {
        return neededPermissionRoles;
    }

    public List<String> getAvailableChannels() {
        return availableChannels;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }
}
