package de.glaubekeinemdev.discordutilities.menus.helper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MenuListener extends ListenerAdapter {

    public MenuListener(final JDA jda) {
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if(!event.isFromGuild())
            return;

        MenuHelper.getInstance().getMenuCache().forEach(eachMenu -> {
            if(event.getUser() != null && !event.getUser().isBot())
                eachMenu.handleReactionAdd(event.getMember(), event.getReaction(), event.getGuild());
        });
    }

}
