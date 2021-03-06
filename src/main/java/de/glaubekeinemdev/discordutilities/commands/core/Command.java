package de.glaubekeinemdev.discordutilities.commands.core;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

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
     * Called when the command is executed
     *
     * @param args    the arguments, the command was executed with
     * @param command the executed commandName without Prefix
     */
    public abstract void execute(final String[] args, final String command, final Member commandSender, final TextChannel channel, final Message message);

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
