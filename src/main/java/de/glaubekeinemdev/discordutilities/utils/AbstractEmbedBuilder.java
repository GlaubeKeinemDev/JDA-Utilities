package de.glaubekeinemdev.discordutilities.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public abstract class AbstractEmbedBuilder {

    public EmbedBuilder embedBuilder;

    public AbstractEmbedBuilder() {
        embedBuilder = new EmbedBuilder();
    }

    public AbstractEmbedBuilder(final String title) {
        embedBuilder = new EmbedBuilder();

        this.embedBuilder.setTitle(title);
    }

    public abstract AbstractEmbedBuilder setTitle(final String title);
    public abstract AbstractEmbedBuilder setFooter(final String footer);
    public abstract AbstractEmbedBuilder addField(final String title, final String body);
    public abstract AbstractEmbedBuilder setDefaultFooter(final Member member);
    public abstract AbstractEmbedBuilder setThumbnail(final String url);
    public abstract AbstractEmbedBuilder setDescription(final String description);
    public abstract AbstractEmbedBuilder setImageUrl(final String imageUrl);
    public abstract AbstractEmbedBuilder setColor(final Color color);

    public MessageEmbed build() {
        return embedBuilder.build();
    }

}
