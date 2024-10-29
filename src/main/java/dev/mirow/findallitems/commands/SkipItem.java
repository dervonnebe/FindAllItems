package dev.mirow.findallitems.commands;

import dev.mirow.findallitems.FindAllItems;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkipItem implements CommandExecutor {
    private final FindAllItems instance;

    public SkipItem(FindAllItems instance) {
        this.instance = instance;
        instance.getCommand("skipitem").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player player) {
            if (!instance.getRandomItem().isPlayerAllowed(player)) {
                player.sendMessage(FindAllItems.PREFIX + instance.getConfig().get("locales.not-allowed").toString().replace('&', '§'));
                return true;
            }
            if (player.hasPermission("findallitems.commands.skipitem")) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    String message = instance.getConfig().get("locales.skip-item").toString().replace('&', '§').replace("%player%", player.getName()).replace("%name%", instance.getRandomItem().getItemName(instance.getRandomItem().material));
                    onlinePlayer.sendMessage(FindAllItems.PREFIX + message);
                }
                instance.getRandomItem().getItem();
            } else {
                player.sendMessage(instance.getConfig().get("locales.no-permission").toString().replace('&', '§').replace("%permission%", "findallitems.commands.skipitem"));
            }
        }
        return false;
    }
}
