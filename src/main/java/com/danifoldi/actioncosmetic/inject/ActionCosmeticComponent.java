package com.danifoldi.actioncosmetic.inject;

import com.danifoldi.actioncosmetic.ActionCosmeticLoader;
import com.danifoldi.actioncosmetic.ActionCosmeticPlugin;
import dagger.BindsInstance;
import dagger.Component;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.logging.Logger;

@Singleton
@Component(modules = {
        ActionCosmeticBindingModule.class,
        ActionCosmeticProvider.class
})
public interface ActionCosmeticComponent {

    @NotNull ActionCosmeticLoader loader();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder plugin(final @NotNull ActionCosmeticPlugin plugin);

        @BindsInstance
        Builder datafolder(final @NotNull Path datafolder);

        @BindsInstance
        Builder logger(final @NotNull Logger logger);

        @BindsInstance
        Builder scheduler(final @NotNull BukkitScheduler scheduler);

        @BindsInstance
        Builder pluginManager(final @NotNull PluginManager pluginManager);

        ActionCosmeticComponent build();
    }
}
