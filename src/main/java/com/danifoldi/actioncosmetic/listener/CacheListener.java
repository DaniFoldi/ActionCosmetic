package com.danifoldi.actioncosmetic.listener;

import com.danifoldi.actioncosmetic.data.SettingCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@SuppressWarnings("ClassCanBeRecord")
public class CacheListener implements Listener {

    private final @NotNull SettingCache cache;

    @Inject
    public CacheListener(final @NotNull SettingCache cache) {
        this.cache = cache;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        cache.get(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cache.evict(event.getPlayer().getUniqueId());
    }
}
