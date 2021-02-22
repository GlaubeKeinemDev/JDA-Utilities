package de.glaubekeinemdev.discordutilities.menus;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.List;

public abstract class Menu {

    private long timeOut;
    private List<User> allowedUsers;

    private boolean cancelled;

    public Menu(long timeOut, List<User> usableUsers) {
        this.timeOut = timeOut;
        this.allowedUsers = usableUsers;
    }

    public abstract void handleReactionAdd(final Member member, final MessageReaction messageReaction, final Guild guild);

    public abstract void handleMenuTimeOuted();

    public abstract void sendMessage(final TextChannel textChannel);

    public abstract void sendMessage(final Message message);

    public abstract void initialize(MessageAction messageAction);

    public abstract Message getMessage();

    public long getTimeOut() {
        return timeOut;
    }

    public List<User> getAllowedUsers() {
        return allowedUsers;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isAllowed(Member member) {
        if (member.getUser().isBot())
            return false;

        return allowedUsers.contains(member.getUser());
    }
}
