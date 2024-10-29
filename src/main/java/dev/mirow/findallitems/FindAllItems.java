package dev.mirow.findallitems;

import dev.mirow.findallitems.challenge.RandomItem;
import dev.mirow.findallitems.commands.SkipItem;
import dev.mirow.findallitems.listener.JoinListener;
import dev.mirow.findallitems.listener.QuitListener;
import dev.mirow.findallitems.utils.BossbarUtils;
import lombok.Getter;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.java.JavaPlugin;

public final class FindAllItems extends JavaPlugin {

    @Getter
    public FindAllItems instance;

    @Getter
    public BossbarUtils bossBarUtils;

    @Getter
    public RandomItem randomItem;

    public static String PREFIX = "LOADING";
    @Override
    public void onEnable() {
        long now = System.currentTimeMillis();
        getLogger().info("Starting FindAllItems...");
        instance = this;
        instance.saveResource("config.yml", false);
        instance.getConfig().options().copyDefaults(true);
        instance.saveConfig();

        PREFIX = instance.getConfig().get("locales.prefix").toString().replace('&', '§');

        randomItem = new RandomItem(this);

        randomItem.checkForItem();

        bossBarUtils = new BossbarUtils(this);

        bossBarUtils.createBossBar();

        new JoinListener(this);
        new QuitListener(this);

        new SkipItem(this);

        getLogger().info("FindAllItems started in " + (System.currentTimeMillis() - now) + "ms");
    }

    @Override
    public void onDisable() {
        instance.saveConfig();
    }
}
