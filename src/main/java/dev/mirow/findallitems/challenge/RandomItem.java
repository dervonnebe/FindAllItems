package dev.mirow.findallitems.challenge;

import dev.mirow.findallitems.FindAllItems;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class RandomItem {

    public ArrayList<String> remainingItems = new ArrayList<>();

    private final ArrayList<String> allowedPlayers;

    public Material material;

    public int currentItemInt;

    private final FindAllItems instance;

    public RandomItem(FindAllItems instance) {
        this.instance = instance;
        this.allowedPlayers = new ArrayList<>(instance.getConfig().getStringList("player.player"));

        if (instance.getConfig().get("materials") == null || instance.getConfig().getList("materials") == null || instance.getConfig().getList("materials").size() == 0) {
            for (Material material : Material.values()) {
                if (!material.isAir()) {
                    this.remainingItems.add(material.name());
                }
            }

            Collections.shuffle(remainingItems);

            instance.getConfig().set("materials", this.remainingItems);

        } else {
            instance.getConfig().getList("materials").forEach(material -> {
                this.remainingItems.add(String.valueOf(material));
            });
        }


        this.currentItemInt = instance.getConfig().getInt("currentitem");
        this.material = Material.matchMaterial(this.remainingItems.get(this.currentItemInt - 1));
    }

    public boolean isPlayerAllowed(Player player) {
        return instance.getConfig().getBoolean("player.enabled") && allowedPlayers.contains(player.getName());
    }

    public void getItem() {
        final String itemName = getItemName(material);
        this.currentItemInt++;

        instance.getConfig().set("currentitem", this.currentItemInt);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (isPlayerAllowed(onlinePlayer)) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
                String itemFound = instance.getConfig().get("locales.item-registered").toString().replace('&', '§')
                        .replace("%item_found%", itemName)
                        .replace("%remaining_items%", String.valueOf(this.remainingItems.size() - this.currentItemInt));
                onlinePlayer.sendMessage(FindAllItems.PREFIX + itemFound);
            }
        }

        generateRandomItem();

        instance.getBossBarUtils().updateBossbar();
    }

    public void generateRandomItem() {
        if (this.currentItemInt == this.remainingItems.size()) {
            for (Player onlinePlayer: Bukkit.getOnlinePlayers()) {
                if (!isPlayerAllowed(onlinePlayer)) {
                    continue;
                }
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                String message = instance.getConfig().get("locales.all-items-found").toString().replace('&', '§');
                onlinePlayer.sendMessage(FindAllItems.PREFIX + message);
                onlinePlayer.setGameMode(GameMode.SPECTATOR);
            }
            return;
        }

        Material newMaterial = Material.matchMaterial(this.remainingItems.get(this.currentItemInt - 1));
        this.material = newMaterial;
    }

    public String getItemName(Material material) {
        String capitalizedName = material.name().substring(0, 1).toUpperCase() + material.name().substring(1).toLowerCase();
        return capitalizedName.replace("_", " ");
    }

    public void checkForItem() {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)instance, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!isPlayerAllowed(onlinePlayer)) {
                    continue;
                }
                if (onlinePlayer.getInventory().contains(this.material))
                    getItem();
            }
            checkForItem();
        }, 20L);
    }
}
