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

public class PagedMenu extends Menu {

    private final String stop = "⏹";

    private final List<String> items;
    private final int itemsPerPage;
    private final String text;
    private final Color color;

    private int maxPages;
    private int currentPage = 1;

    private Message message;

    public PagedMenu(long timeOut, List<User> usableUsers, List<String> items, int itemsPerPage, String text, Color color) {
        super(timeOut, usableUsers);
        this.items = items;
        this.itemsPerPage = itemsPerPage;
        this.text = text;
        this.color = color;

        this.maxPages = (int) Math.ceil((double) items.size() / (double) itemsPerPage);

        if(maxPages != 1)
            MenuHelper.getInstance().getMenuCache().add(this);
    }

    @Override
    public void handleReactionAdd(Member member, MessageReaction messageReaction, Guild guild) {
        if(!isAllowed(member))
            return;

        final MessageReaction.ReactionEmote reactionEmote = messageReaction.getReactionEmote();

        // Forward
        if(reactionEmote.getName().equals("\u25B6")) {
            messageReaction.removeReaction(member.getUser()).queue();

            if(currentPage == maxPages)
                return;

            this.currentPage = this.currentPage + 1;
            message.editMessage(getPage(currentPage)).queue();
            return;
        }

        // Backward
        if(reactionEmote.getName().equals("\u25C0")) {
            messageReaction.removeReaction(member.getUser()).queue();

            if(currentPage == 1)
                return;

            this.currentPage = this.currentPage - 1;
            message.editMessage(getPage(currentPage)).queue();
            return;
        }

        if(reactionEmote.getName().equals(stop)) {
            message.clearReactions().queue();
            MenuHelper.getInstance().getMenuCache().remove(this);
            return;
        }

        messageReaction.removeReaction(member.getUser()).queue();
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

            if(maxPages != 1) {
                sentMessage.addReaction("\u25C0").queue(unused
                        -> sentMessage.addReaction(stop).queue(unused1 -> sentMessage.addReaction("\u25B6").queue()));
            }
        });
    }

    @Override
    public Message getMessage() {
        return getPage(1);
    }

    private Message getPage(final int page) {
        final MessageBuilder messageBuilder = new MessageBuilder(this.text);

        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(color);

        if(maxPages != 1)
            embedBuilder.setFooter("Seite " + page + "/" + maxPages);

        final StringBuilder stringBuilder = new StringBuilder();

        final int start = (currentPage - 1) * this.itemsPerPage;
        int end = (Math.min(this.items.size(), (currentPage * this.itemsPerPage)));

        for(int i = start; i < end; i++) {
            stringBuilder.append("`" + (i + 1) + ".` " + items.get(i) + "\n");
        }

        embedBuilder.addField("", stringBuilder.toString(), false);

        messageBuilder.setEmbed(embedBuilder.build());
        return messageBuilder.build();
    }

    public static class Builder {

        private long timeOut;
        private final ArrayList<User> usableUser = new ArrayList<>();

        private List<String> items = new ArrayList<>();
        private int itemsPerPage;
        private String text;
        private Color color;


        public Builder setTimeOut(long timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public Builder addUser(final User user) {
            this.usableUser.add(user);
            return this;
        }

        public Builder addItem(final String item) {
            this.items.add(item);
            return this;
        }

        public Builder addItems(final String... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        public Builder setItemsPerPage(final int size) {
            this.itemsPerPage = size;
            return this;
        }

        public Builder setText(final String text) {
            this.text = text;
            return this;
        }

        public Builder setColor(final Color color) {
            this.color = color;
            return this;
        }

        public PagedMenu build() {
            if(timeOut < System.currentTimeMillis())
                return null;

            if(usableUser.isEmpty())
                return null;

            if(color == null)
                return null;

            if(items.isEmpty())
                return null;

            return new PagedMenu(timeOut, usableUser, items, itemsPerPage, text, color);
        }

    }
}
