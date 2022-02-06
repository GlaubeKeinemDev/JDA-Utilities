package de.glaubekeinemdev.discordutilities.menus;

import de.glaubekeinemdev.discordutilities.menus.helper.MenuHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

        if(messageReaction.getReactionEmote().getName().equals("❌") && useCancelButton) {
            message.clearReactions().queue();
            setCancelled(true);
            return;
        }

        if(useNumbers && !Arrays.asList(numbers).contains(messageReaction.getReactionEmote().getName())) {
            messageReaction.removeReaction(member.getUser()).queue();
            return;
        }

        if(!useNumbers && !Arrays.asList(letters).contains(messageReaction.getReactionEmote().getName())) {
            messageReaction.removeReaction(member.getUser()).queue();
            return;
        }

        final int number = getNumber(messageReaction.getReactionEmote().getName());

        if(number > choices.size()) {
            messageReaction.removeReaction(member.getUser()).queue();
            return;
        }

        final OrderedMenu orderedMenu = this;

        messageReaction.removeReaction(member.getUser()).queue(unused -> action.accept(orderedMenu, getNumber(messageReaction.getReactionEmote().getName())));
    }

    @Override
    public void handleMenuTimeOuted() {
        if(isCancelled())
            return;

        message.clearReactions().queue();
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
                sentMessage.addReaction(getEmoji(i)).queue();
            }

            if(useCancelButton)
                sentMessage.addReaction("❌").queue();
        });
    }

    @Override
    public Message getMessage() {
        MessageBuilder messageBuilder = new MessageBuilder();
        if(this.text != null)
            messageBuilder.append(this.text);

        final StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < choices.size(); i++) {
            stringBuilder.append("\n").append(getEmoji(i)).append(" ").append(choices.get(i));
        }

        messageBuilder.setEmbeds((new EmbedBuilder()).setColor(this.color).setDescription((
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

        private long timeOut;
        private Color color;
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
            if(timeOut < System.currentTimeMillis())
                return null;

            if(usableUser.isEmpty())
                return null;

            if(color == null)
                return null;

            if(choices.isEmpty())
                return null;

            return new OrderedMenu(timeOut, usableUser, text, color, description, action, useNumbers, choices, useCancelButton);
        }

    }
}
