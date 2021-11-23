package com.danifoldi.actioncosmetic;

import com.danifoldi.actioncosmetic.inject.ActionCosmeticComponent;
import com.danifoldi.actioncosmetic.inject.DaggerActionCosmeticComponent;
import com.danifoldi.dataverse.data.Namespaced;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionCosmeticPlugin extends JavaPlugin implements Namespaced {

    private @Nullable ActionCosmeticLoader loader;

    @Override
    public void onEnable() {

        final @NotNull ActionCosmeticComponent component = DaggerActionCosmeticComponent
                .builder()
                .plugin(this)
                .logger(getLogger())
                .datafolder(getDataFolder().toPath())
                .scheduler(Bukkit.getScheduler())
                .pluginManager(Bukkit.getPluginManager())
                .build();
        this.loader = component.loader();
        this.loader.load();
    }

    @Override
    public void onDisable() {

        if (this.loader == null) {
            return;
        }

        this.loader.unload();
    }

    @Override
    public @NotNull String getNamespace() {
        return getDescription().getName();
    }
}
