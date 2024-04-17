package de.glaubekeinemdev.discordutilities.menus;

import de.glaubekeinemdev.discordutilities.menus.helper.MenuHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * Button Menu - Create a text with one (or more) custom Emote(s) as a button.
 * After the emote is clicked the menu deletes itself and a custom handler is called to handle the result to your fit.
 * Take a look at the: "action" function for handling input.
 * The "finalAction" Function is called after the action function and called before the menu gets deleted
 * Use the ButtonMenu.Builder class to build a Button Menu
 * Choices + Text need to be set, otherwise the builder returns null
 *
 * NOTE: You need to implement the "action" function - otherwise the builder returns null
 *
 * */
public class ButtonMenu extends Menu {

    private final Color color;
    private final String text;
    private final String description;
    private final List<String> choices;

    private final Consumer<MessageReaction> action;
    private final Consumer<Message> finalAction;

    private Message message;
    private boolean cancelled;

    public ButtonMenu(long timeOut, List<User> usableUsers, Color color, String text, String description, List<String> choices, Consumer<MessageReaction> action, Consumer<Message> finalAction) {
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
    public void sendMessage(TextChannel textChannel) {
        createMessage(textChannel.sendMessage(getMessage()));
    }

    @Override
    public void sendMessage(Message message) {
        editMessage(message.editMessage(MessageEditBuilder.fromCreateData(getMessage()).build()));
    }

    @Override
    public void handleReactionAdd(Member member, MessageReaction messageReaction, Guild guild) {
        if(!messageReaction.getMessageId().equals(this.message.getId()))
            return;

        if(!isAllowed(member)) {
            messageReaction.removeReaction(member.getUser()).queue();
            return;
        }

        final String reaction = messageReaction.getEmoji().getName();

        if(!this.choices.contains(reaction)) {
            messageReaction.removeReaction(member.getUser()).queue();
            return;
        }

        action.accept(messageReaction);
        if(this.finalAction != null)
            this.finalAction.accept(this.message);
        setCancelled(true);
    }

    @Override
    public void createMessage(MessageCreateAction messageAction) {
        messageAction.queue(sentMessage -> {
            message = sentMessage;

            for (String choice : choices) {
                RichCustomEmoji emote;

                try {
                    emote = sentMessage.getJDA().getEmojiById(choice);
                } catch (Exception e) {
                    emote = null;
                }

                final RestAction<Void> restAction = (emote == null ? sentMessage.addReaction(
                        Emoji.fromUnicode(choice)) : sentMessage.addReaction(emote));

                restAction.queue();
            }
        });
    }

    @Override
    public void editMessage(MessageEditAction messageEditAction) {
        messageEditAction.queue(sentMessage -> {
            message = sentMessage;

            for (String choice : choices) {
                RichCustomEmoji emote;

                try {
                    emote = sentMessage.getJDA().getEmojiById(choice);
                } catch (Exception e) {
                    emote = null;
                }

                final RestAction<Void> restAction = (emote == null ? sentMessage.addReaction(
                        Emoji.fromUnicode(choice)) : sentMessage.addReaction(emote));

                restAction.queue();
            }
        });
    }

    @Override
    public MessageCreateData getMessage() {
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();

        if (this.text != null)
            messageBuilder.addContent(this.text);

        messageBuilder.addEmbeds((new EmbedBuilder()).setColor(this.color).setDescription((
                this.description != null ? this.description : "")).build());

        return messageBuilder.build();
    }

    @Override
    public void handleMenuTimeOuted() {
        if (this.cancelled)
            return;

        this.message.clearReactions().queue();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static class Builder {

        private long timeOut = -1;
        private final ArrayList<User> usableUser = new ArrayList<>();

        private List<String> choices = new ArrayList<>();
        private String text;
        private String description;
        private Color color = Color.WHITE;

        private Consumer<MessageReaction> action;
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

        public ButtonMenu.Builder setAction(final Consumer<MessageReaction> action) {
            this.action = action;
            return this;
        }

        public ButtonMenu.Builder setFinalAction(final Consumer<Message> finalAction) {
            this.finalAction = finalAction;
            return this;
        }

        public ButtonMenu build() {
            if (text == null)
                return null;

            if (choices.isEmpty())
                return null;

            if(action == null)
                return null;

            return new ButtonMenu(timeOut, usableUser, color, text, description, choices, action, finalAction);
        }
    }
}
