package dev.mirow.findallitems.listener;

import dev.mirow.findallitems.FindAllItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final FindAllItems instance;

    public JoinListener(FindAllItems instance) {
        this.instance = instance;
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (instance.getRandomItem().isPlayerAllowed(player)) {
            String message = instance.getConfig().get("locales.join-message").toString().replace('&', '§').replace("%player%", player.getName());
            event.setJoinMessage(FindAllItems.PREFIX + message);
            instance.getBossBarUtils().addPlayer(player);
        }
    }
}
