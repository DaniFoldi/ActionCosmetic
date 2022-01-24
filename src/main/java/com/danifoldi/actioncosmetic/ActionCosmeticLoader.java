package com.danifoldi.actioncosmetic;

import com.danifoldi.actioncosmetic.command.CommandManager;
import com.danifoldi.actioncosmetic.config.Config;
import com.danifoldi.actioncosmetic.config.CustomMessageBuilder;
import com.danifoldi.actioncosmetic.config.FileUtil;
import com.danifoldi.actioncosmetic.listener.ActionListener;
import com.danifoldi.actioncosmetic.listener.CacheListener;
import com.danifoldi.dml.exception.DmlParseException;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class ActionCosmeticLoader {

    private final @NotNull CommandManager commandManager;
    private final @NotNull ActionCosmeticPlugin plugin;
    private final @NotNull Logger logger;
    private final @NotNull CustomMessageBuilder messageBuilder;
    private final @NotNull Path datafolder;
    private final @NotNull ActionListener actionListener;
    private final @NotNull CacheListener cacheListener;
    private final @NotNull PluginManager pluginManager;
    private final @NotNull Config config;
    @Inject
    public ActionCosmeticLoader(final @NotNull CommandManager commandManager,
                                final @NotNull ActionCosmeticPlugin plugin,
                                final @NotNull Logger logger,
                                final @NotNull CustomMessageBuilder messageBuilder,
                                final @NotNull Path datafolder,
                                final @NotNull ActionListener actionListener,
                                final @NotNull CacheListener cacheListener,
                                final @NotNull PluginManager pluginManager,
                                final @NotNull Config config) {
        this.commandManager = commandManager;
        this.plugin = plugin;
        this.logger = logger;
        this.messageBuilder = messageBuilder;
        this.datafolder = datafolder;
        this.actionListener = actionListener;
        this.cacheListener = cacheListener;
        this.pluginManager = pluginManager;
        this.config = config;
    }

    void load() {
        try {
            FileUtil.copyIfNotExists("config.dml", datafolder);
            commandManager.setup();
            pluginManager.registerEvents(actionListener, plugin);
            pluginManager.registerEvents(cacheListener, plugin);
            config.load(datafolder.resolve("config.dml"));
        } catch (IOException | DmlParseException e) {
            logger.severe("Failed to load ActionCosmetic");
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    void unload() {
        commandManager.destroy();
        HandlerList.unregisterAll(plugin);
    }
}
