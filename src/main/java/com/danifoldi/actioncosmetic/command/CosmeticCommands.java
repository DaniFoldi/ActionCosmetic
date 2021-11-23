package com.danifoldi.actioncosmetic.command;

import com.danifoldi.actioncosmetic.config.Config;
import com.danifoldi.actioncosmetic.data.ActionGui;
import grapefruit.command.CommandContainer;
import grapefruit.command.CommandDefinition;
import grapefruit.command.dispatcher.Redirect;
import grapefruit.command.parameter.modifier.Source;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.swing.*;

public class CosmeticCommands implements CommandContainer {

    private final @NotNull Config config;
    private final @NotNull ActionGui gui;
    @Inject
    public CosmeticCommands(final @NotNull Config config,
                            final @NotNull ActionGui gui) {

        this.config = config;
        this.gui = gui;
    }

    @Redirect(from = "cosmetic|actioncosmetic")
    @CommandDefinition(route = "cosmetic|actioncosmetic set", permission = "actioncosmetic.command.set", runAsync = true)
    public void onSetCommand(final @Source Player player) {

        gui.open(player);
    }

    @CommandDefinition(route = "cosmetic|actioncosmetic reload", permission = "actioncosmetic.command.reload", runAsync = true)
    public void onReloadCommand(final @Source CommandSender sender) {

        config.reload();
        sender.sendMessage("Config reloaded");
    }
}
