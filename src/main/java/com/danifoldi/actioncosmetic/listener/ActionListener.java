package com.danifoldi.actioncosmetic.listener;

import com.danifoldi.actioncosmetic.ActionCosmeticPlugin;
import com.danifoldi.actioncosmetic.config.Config;
import com.danifoldi.actioncosmetic.data.Cosmetic;
import com.danifoldi.actioncosmetic.data.PlayerSetting;
import com.danifoldi.actioncosmetic.data.SettingCache;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class ActionListener implements Listener {

    private final @NotNull SettingCache cache;
    private final @NotNull BukkitScheduler scheduler;
    private final @NotNull ActionCosmeticPlugin plugin;
    private final @NotNull Config config;

    private final @NotNull Cache<UUID, Object> rateLimit = CacheBuilder.newBuilder().expireAfterWrite(400, TimeUnit.MILLISECONDS).build();

    private final int COUNT = 4;
    private final Predicate<Metadatable> VANISHED = p -> {
        for (final MetadataValue meta : p.getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }

        return false;
    };

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

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (player.isSneaking()) {

            return;
        }
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {

            return;
        }
        if (VANISHED.test(player)) {

            return;
        }
        if (rateLimit.getIfPresent(uuid) != null && !player.hasPermission("actioncosmetic.ratelimit.bypass")) {

            return;
        }

        PlayerSetting setting = cache.get(uuid);
        if (setting == null || setting.getSneakSelection().equals("")) {

            return;
        }
        if (!player.hasPermission("actioncosmetic.sneak." + setting.getSneakSelection())) {

            cache.update(uuid, setting.withSneakSelection(""));
            return;
        }
        Cosmetic cosmetic = config.getCosmetics().stream().filter(c -> c.action().equals(setting.getSneakSelection())).findFirst().orElse(null);
        if (cosmetic == null) {

            return;
        }
        Location location = player.getLocation().add(0, 2.0, 0);

        scheduler.runTaskLater(plugin, () -> {
            for (int i = 0; i < COUNT; i++) {
                final Location l = location.clone();
                l.setX(l.getX() + 0.5 * Math.cos((double) i / COUNT * Math.PI * 2));
                l.getWorld().spawnParticle(cosmetic.particle(), l, 2, .2, .2, .2, 5.0e-4);
            }
        }, 1);
        rateLimit.put(uuid, uuid);
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (VANISHED.test(player)) {

            return;
        }
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {

            return;
        }
        if (rateLimit.getIfPresent(uuid) != null && !player.hasPermission("actioncosmetic.ratelimit.bypass")) {

            return;
        }

        PlayerSetting setting = cache.get(uuid);
        if (setting == null || setting.getJumpSelection().equals("")) {

            return;
        }
        if (!player.hasPermission("actioncosmetic.jump." + setting.getJumpSelection())) {

            cache.update(player.getUniqueId(), setting.withJumpSelection(""));
            return;
        }
        Cosmetic cosmetic = config.getCosmetics().stream().filter(c -> c.action().equals(setting.getJumpSelection())).findFirst().orElse(null);
        if (cosmetic == null) {

            return;
        }

        Location location = player.getLocation().add(0, 2.0, 0);

        scheduler.runTaskLater(plugin, () -> {
            for (int i = 0; i < COUNT; i++) {
                final Location l = location.clone();
                l.setX(l.getX() + 0.5 * Math.cos((double) i / COUNT * Math.PI * 2));
                l.getWorld().spawnParticle(cosmetic.particle(), l, 2, .2, .2, .2, 5.0e-4);
            }
        }, 1);
        rateLimit.put(uuid, uuid);
    }
}
