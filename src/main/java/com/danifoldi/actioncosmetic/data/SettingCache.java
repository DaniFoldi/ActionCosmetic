package com.danifoldi.actioncosmetic.data;

import com.danifoldi.dataverse.data.NamespacedDataVerse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class SettingCache {

    private final @NotNull Lazy<NamespacedDataVerse<PlayerSetting>> settingDataVerse;
    private final @NotNull ExecutorService asyncExecutor;
    private final @NotNull Map<UUID, PlayerSetting> settingCache = new ConcurrentHashMap<>();
    private final @NotNull Cache<UUID, Object> requestCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
    private final static @NotNull Object DUMMY = new Object();

    @Inject
    public SettingCache(final @NotNull Lazy<NamespacedDataVerse<PlayerSetting>> settingDataVerse,
                        final @NotNull ExecutorService asyncExecutor) {

        this.settingDataVerse = settingDataVerse;
        this.asyncExecutor = asyncExecutor;
    }

    public PlayerSetting get(final @NotNull UUID uuid) {

        PlayerSetting setting = settingCache.get(uuid);
        if (setting == null && requestCache.getIfPresent(uuid) == null) {

            requestCache.put(uuid, DUMMY);
            settingDataVerse.get().get(uuid).thenAcceptAsync(s -> {

                if (s != null) {

                    settingCache.put(uuid, s);
                } else {

                    settingCache.put(uuid, new PlayerSetting());
                }
                requestCache.invalidate(uuid);
            }, asyncExecutor);
            return new PlayerSetting();
        }
        return setting;
    }

    public void update(final @NotNull UUID uuid, final @NotNull PlayerSetting setting) {

        settingCache.put(uuid, setting);
        settingDataVerse.get().createOrUpdate(uuid, setting);
    }

    public void evict(final @NotNull UUID uuid) {

        PlayerSetting setting = settingCache.get(uuid);
        if (setting != null) {

            settingDataVerse.get().createOrUpdate(uuid, setting);
        }
        settingCache.remove(uuid);
    }
}
