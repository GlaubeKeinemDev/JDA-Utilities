package de.glaubekeinemdev.discordutilities.menus;

import de.glaubekeinemdev.discordutilities.menus.helper.MenuHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 *
 * Ordered Menu - Create a text with numbers or letters as "selectors" for your choices
 * After one emote is clicked you can handle everything on your own. Look: "action" consumer
 * NOTE: Before the "action" function gets called the reaction gets removed automatically
 * This menu can be used multiple times, members can select different emotes or the same emotes multiple times
 * Use the OrderedMenu.Builder class to build a OrderedMenu Menu
 * Choices need to be set, otherwise the builder returns null
 *
 * NOTE: You need to implement the "action" function - otherwise the builder returns null
 *
 * */
public class OrderedMenu extends Menu {

    private final String[] numbers = new String[]{"1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣", "\ud83d\udd1f"};
    private final String[] letters = new String[]{"\ud83c\udde6", "\ud83c\udde7", "\ud83c\udde8", "\ud83c\udde9",
            "\ud83c\uddea", "\ud83c\uddeb", "\ud83c\uddec", "\ud83c\udded", "\ud83c\uddee", "\ud83c\uddef"};

    private final String text;
    private final Color color;
    private final String description;
    private final Boolean useNumbers;
    private final ArrayList<String> choices;
    private final boolean useCancelButton;

    private final BiConsumer<OrderedMenu, Integer> action;

    private Message message;

    public OrderedMenu(long timeOut, List<User> usableUsers, String text, Color color, String description,
                       BiConsumer<OrderedMenu, Integer> action, boolean useNumbers, ArrayList<String> choices, boolean useCancelButton) {
        super(timeOut, usableUsers);
        this.text = text;
        this.color = color;
        this.description = description;
        this.useNumbers = useNumbers;
        this.action = action;
        this.choices = choices;
        this.useCancelButton = useCancelButton;

        MenuHelper.getInstance().getMenuCache().add(this);
    }

    @Override
    public void handleReactionAdd(Member member, MessageReaction messageReaction, Guild guild) {
        if(!isAllowed(member))
            return;

        if(!message.getId().equals(messageReaction.getMessageId()))
            return;

        final UnicodeEmoji reactionEmote = messageReaction.getEmoji().asUnicode();

        if(reactionEmote.getName().equals("❌") && useCancelButton) {
            message.clearReactions().queue();
            setCancelled(true);
            return;
        }

        if(useNumbers && !Arrays.asList(numbers).contains(reactionEmote.getName())) {
            messageReaction.removeReaction(member.getUser()).queue();
            return;
        }

        if(!useNumbers && !Arrays.asList(letters).contains(reactionEmote.getName())) {
            messageReaction.removeReaction(member.getUser()).queue();
            return;
        }

        final int number = getNumber(reactionEmote.getName());

        if(number > choices.size()) {
            messageReaction.removeReaction(member.getUser()).queue();
            return;
        }

        final OrderedMenu orderedMenu = this;

        messageReaction.removeReaction(member.getUser()).queue(unused -> action.accept(orderedMenu, getNumber(reactionEmote.getName())));
    }

    @Override
    public void handleMenuTimeOuted() {
        if(isCancelled())
            return;

        message.clearReactions().queue();
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
    public void createMessage(MessageCreateAction messageAction) {
        messageAction.queue(sentMessage -> {
            message = sentMessage;

            for(int i = 0; i < choices.size(); i++) {
                sentMessage.addReaction(Emoji.fromUnicode(getEmoji(i))).queue();
            }

            if(useCancelButton)
                sentMessage.addReaction(Emoji.fromUnicode("❌")).queue();
        });
    }

    @Override
    public void editMessage(MessageEditAction messageEditAction) {
        messageEditAction.queue(sentMessage -> {
            message = sentMessage;

            for(int i = 0; i < choices.size(); i++) {
                sentMessage.addReaction(Emoji.fromUnicode(getEmoji(i))).queue();
            }

            if(useCancelButton)
                sentMessage.addReaction(Emoji.fromUnicode("❌")).queue();
        });
    }

    @Override
    public MessageCreateData getMessage() {
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();

        if(this.text != null)
            messageBuilder.addContent(this.text);

        final StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < choices.size(); i++) {
            stringBuilder.append("\n").append(getEmoji(i)).append(" ").append(choices.get(i));
        }

        messageBuilder.addEmbeds((new EmbedBuilder()).setColor(this.color).setDescription((
                this.description != null ? this.description + stringBuilder.toString() : stringBuilder.toString())
        ).build());

        return messageBuilder.build();
    }

    public Message getEffectiveMessage() {
        return this.message;
    }

    private String getEmoji(int index) {
        return this.useNumbers ? numbers[index] : letters[index];
    }

    private int getNumber(String emoji) {
        String[] array = this.useNumbers ? numbers : letters;

        for(int i = 0; i < array.length; ++i) {
            if(array[i].equals(emoji)) {
                return i + 1;
            }
        }

        return -1;
    }

    public static class Builder {

        private long timeOut = -1;
        private Color color = Color.WHITE;
        private String text;
        private String description;
        private final ArrayList<User> usableUser = new ArrayList<>();
        private final ArrayList<String> choices = new ArrayList<>();
        private BiConsumer<OrderedMenu, Integer> action;
        private boolean useNumbers = false;
        private boolean useCancelButton = false;

        public Builder setColor(final Color color) {
            this.color = color;
            return this;
        }

        public Builder setText(final String text) {
            this.text = text;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder addUser(final User user) {
            this.usableUser.add(user);
            return this;
        }

        public Builder addChoice(final String choice) {
            this.choices.add(choice);
            return this;
        }

        public Builder addChoices(final String... choices) {
            this.choices.addAll(Arrays.asList(choices));
            return this;
        }

        public Builder setAction(final BiConsumer<OrderedMenu, Integer> consumer) {
            this.action = consumer;
            return this;
        }

        public Builder useNumbers() {
            this.useNumbers = true;
            return this;
        }

        public Builder useCancelButton() {
            this.useCancelButton = true;
            return this;
        }

        public Builder setTimeOut(long timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public OrderedMenu build() {
            if(choices.isEmpty())
                return null;

            if(action == null)
                return null;

            return new OrderedMenu(timeOut, usableUser, text, color, description, action, useNumbers, choices, useCancelButton);
        }
    }

}
