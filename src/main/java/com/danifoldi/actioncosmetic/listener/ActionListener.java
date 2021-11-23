package com.danifoldi.actioncosmetic.listener;

import com.danifoldi.actioncosmetic.ActionCosmeticPlugin;
import com.danifoldi.actioncosmetic.config.Config;
import com.danifoldi.actioncosmetic.data.PlayerSetting;
import com.danifoldi.actioncosmetic.data.SettingCache;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Locale;

public class ActionListener implements Listener {

    private final @NotNull SettingCache cache;
    private final @NotNull BukkitScheduler scheduler;
    private final @NotNull ActionCosmeticPlugin plugin;
    private final @NotNull Config config;

    private final int COUNT = 4;

    @Inject
    public ActionListener(final @NotNull SettingCache cache,
                          final @NotNull BukkitScheduler scheduler,
                          final @NotNull ActionCosmeticPlugin plugin,
                          final @NotNull Config config) {

        this.cache = cache;
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {

        if (!event.getPlayer().isSneaking()) {

            return;
        }

        PlayerSetting setting = cache.get(event.getPlayer().getUniqueId());
        if (setting == null) {

            return;
        }
        if (!event.getPlayer().hasPermission("actioncosmetic.sneak." + setting.getSneakSelection())) {

            return;
        }

        Location location = event.getPlayer().getLocation().clone().add(0, 2.0, 0);

        scheduler.runTaskLater(plugin, () -> {
            for (int i = 0; i < COUNT; i++) {
                final Location l = location.clone();
                l.setX(l.getX() + 0.5 * Math.cos((double) i / COUNT * Math.PI * 2));
                l.getWorld().spawnParticle(config.getCosmetics().get(setting.getSneakSelection()).particle(), l, 2, .2, .2, .2, 5.0e-4);
            }
        }, 1);
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {

        if (!event.getPlayer().isJumping()) {

            return;
        }

        PlayerSetting setting = cache.get(event.getPlayer().getUniqueId());

        if (setting == null) {

            return;
        }
        if (!event.getPlayer().hasPermission("actioncosmetic.jump." + setting.getJumpSelection())) {

            return;
        }

        Location location = event.getPlayer().getLocation().clone().add(0, 2.0, 0);

        scheduler.runTaskLater(plugin, () -> {
            for (int i = 0; i < COUNT; i++) {
                final Location l = location.clone();
                l.setX(l.getX() + 0.5 * Math.cos((double) i / COUNT * Math.PI * 2));
                l.getWorld().spawnParticle(config.getCosmetics().get(setting.getJumpSelection()).particle(), l, 2, .2, .2, .2, 5.0e-4);
            }
        }, 1);
    }
}
