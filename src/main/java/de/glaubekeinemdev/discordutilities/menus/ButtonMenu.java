package de.glaubekeinemdev.discordutilities.menus;

import de.glaubekeinemdev.discordutilities.menus.helper.MenuHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ButtonMenu extends Menu {

    private final Color color;
    private final String text;
    private final String description;
    private final List<String> choices;

    private final Consumer<MessageReaction.ReactionEmote> action;
    private final Consumer<Message> finalAction;

    private Message message;
    private boolean cancelled;

    public ButtonMenu(long timeOut, List<User> usableUsers, Color color, String text, String description, List<String> choices, Consumer<MessageReaction.ReactionEmote> action, Consumer<Message> finalAction) {
        super(timeOut, usableUsers);
        this.color = color;
        this.text = text;
        this.description = description;
        this.choices = choices;
        this.action = action;
        this.finalAction = finalAction;

        MenuHelper.getInstance().getMenuCache().add(this);
    }

    @Override
    public void handleReactionAdd(Member member, MessageReaction messageReaction, Guild guild) {
        if(!messageReaction.getMessageId().equals(this.message.getId()))
            return;

        if(!isAllowed(member)) {
            messageReaction.removeReaction(member.getUser()).queue();
            return;
        }

        final String reaction = messageReaction.getReactionEmote().isEmote() ?
                messageReaction.getReactionEmote().getId() : messageReaction.getReactionEmote().getName();

        if(!this.choices.contains(reaction)) {
            messageReaction.removeReaction(member.getUser()).queue();
            return;
        }

        action.accept(messageReaction.getReactionEmote());
        finalAction.accept(this.message);
        setCancelled(true);
    }

    @Override
    public void handleMenuTimeOuted() {
        if(this.cancelled)
            return;

        this.message.clearReactions().queue();
    }

    @Override
    public void sendMessage(TextChannel textChannel) {
        initialize(textChannel.sendMessage(getMessage()));
    }

    @Override
    public void sendMessage(Message message) {
        initialize(message.editMessage(getMessage()));
    }

    @Override
    public void initialize(MessageAction messageAction) {
        messageAction.queue(sentMessage -> {
            message = sentMessage;

            for(int i = 0; i < choices.size(); i++) {
                Emote emote;

                try {
                    emote = sentMessage.getJDA().getEmoteById(choices.get(i));
                } catch(Exception e) {
                    emote = null;
                }

                final RestAction<Void> restAction = (emote == null ? sentMessage.addReaction(choices.get(i)) : sentMessage.addReaction(emote));

                restAction.queue();
            }
        });
    }

    @Override
    public Message getMessage() {
        final MessageBuilder messageBuilder = new MessageBuilder();
        if (this.text != null) {
            messageBuilder.append(this.text);
        }

        if (this.description != null) {
            messageBuilder.setEmbeds((new EmbedBuilder()).setColor((this.color != null ? this.color : Color.GREEN))
                    .setDescription(this.description).build());
        }

        return messageBuilder.build();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static class Builder {

        private long timeOut;
        private final ArrayList<User> usableUser = new ArrayList<>();

        private List<String> choices = new ArrayList<>();
        private String text;
        private String description;
        private Color color;

        private Consumer<MessageReaction.ReactionEmote> action;
        private Consumer<Message> finalAction;

        public ButtonMenu.Builder setTimeOut(long timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public ButtonMenu.Builder addUser(final User user) {
            this.usableUser.add(user);
            return this;
        }

        public ButtonMenu.Builder addChoice(final String item) {
            this.choices.add(item);
            return this;
        }

        public ButtonMenu.Builder addChoices(final String... items) {
            this.choices.addAll(Arrays.asList(items));
            return this;
        }

        public ButtonMenu.Builder setText(final String text) {
            this.text = text;
            return this;
        }

        public ButtonMenu.Builder setDescription(final String description) {
            this.description = description;
            return this;
        }

        public ButtonMenu.Builder setColor(final Color color) {
            this.color = color;
            return this;
        }

        public ButtonMenu.Builder setAction(final Consumer<MessageReaction.ReactionEmote> action) {
            this.action = action;
            return this;
        }

        public ButtonMenu.Builder setFinalAction(final Consumer<Message> finalAction) {
            this.finalAction = finalAction;
            return this;
        }

        public ButtonMenu build() {
            if(timeOut < System.currentTimeMillis())
                return null;

            if(usableUser.isEmpty())
                return null;

            if(text == null)
                return null;

            if(choices.isEmpty())
                return null;

            return new ButtonMenu(timeOut, usableUser, color, text, description, choices, action, finalAction);
        }

    }
}
