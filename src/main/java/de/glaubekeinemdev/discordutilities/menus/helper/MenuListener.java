package de.glaubekeinemdev.discordutilities.menus.helper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MenuListener extends ListenerAdapter {

    public MenuListener(final JDA jda) {
        jda.addEventListener(this);
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        MenuHelper.getInstance().getMenuCache().forEach(eachMenu -> {
            eachMenu.handleReactionAdd(event.getMember(), event.getReaction(), event.getGuild());
        });
    }

}
