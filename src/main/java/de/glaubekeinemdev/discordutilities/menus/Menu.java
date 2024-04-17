package de.glaubekeinemdev.discordutilities.menus;

import de.glaubekeinemdev.discordutilities.menus.helper.MenuHelper;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

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

    public abstract void createMessage(MessageCreateAction messageAction);

    public abstract void editMessage(MessageEditAction messageEditAction);

    public abstract MessageCreateData getMessage();

    public long getTimeOut() {
        return timeOut;
    }

    public List<User> getAllowedUsers() {
        return allowedUsers;
    }

    public void setCancelled(boolean cancelled) {
        MenuHelper.getInstance().getMenuCache().remove(this);
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isAllowed(Member member) {
        if (member.getUser().isBot())
            return false;

        if(this.allowedUsers.isEmpty())
            return true;

        return allowedUsers.contains(member.getUser());
    }

}
