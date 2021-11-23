package com.danifoldi.actioncosmetic.inject;

import com.danifoldi.actioncosmetic.ActionCosmeticPlugin;
import com.danifoldi.actioncosmetic.data.PlayerSetting;
import com.danifoldi.dataverse.DataVerse;
import com.danifoldi.dataverse.data.NamespacedDataVerse;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import javax.xml.crypto.Data;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Module
public class ActionCosmeticProvider {

    @Provides
    @Singleton
    public @NotNull NamespacedDataVerse<@NotNull PlayerSetting> provideSettingsDataverse(ActionCosmeticPlugin plugin) {

        System.out.println(DataVerse.getDataVerse());
        return DataVerse.getDataVerse().getNamespacedDataVerse(plugin, "playerSettings", PlayerSetting::new);
    }

    @Provides
    @Singleton
    public @NotNull ExecutorService provideExecutorService() {
        return Executors.newCachedThreadPool(new ThreadFactoryBuilder()
                .setNameFormat("ActionCosmetic Thread Pool - %1$d").build());
    }
}
