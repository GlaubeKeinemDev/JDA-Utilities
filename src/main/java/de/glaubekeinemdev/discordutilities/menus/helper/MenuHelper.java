package de.glaubekeinemdev.discordutilities.menus.helper;

import de.glaubekeinemdev.discordutilities.menus.Menu;
import net.dv8tion.jda.api.JDA;

import java.util.concurrent.*;

public class MenuHelper {

    private final CopyOnWriteArrayList<Menu> menuCache = new CopyOnWriteArrayList<>();

    private static MenuHelper instance;

    private ScheduledExecutorService scheduledExecutorService;

    public MenuHelper() {
        instance = this;
    }

    public void setup(final JDA jda) {
        jda.addEventListener(new MenuListener(jda));
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {

            menuCache.forEach(eachMenu -> {
                if(System.currentTimeMillis() > eachMenu.getTimeOut()) {
                    eachMenu.handleMenuTimeOuted();
                    menuCache.remove(eachMenu);
                }
            });

        }, 0, 2, TimeUnit.SECONDS);
    }

    public void shutdown() {
        scheduledExecutorService.shutdownNow();
    }

    public static MenuHelper getInstance() {
        return instance;
    }

    public CopyOnWriteArrayList<Menu> getMenuCache() {
        return menuCache;
    }
}
