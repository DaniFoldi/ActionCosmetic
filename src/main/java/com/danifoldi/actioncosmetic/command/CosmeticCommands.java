package com.danifoldi.actioncosmetic.command;

import com.danifoldi.actioncosmetic.config.Config;
import com.danifoldi.actioncosmetic.data.ActionGui;
import grapefruit.command.CommandContainer;
import grapefruit.command.CommandDefinition;
import grapefruit.command.dispatcher.Redirect;
import grapefruit.command.parameter.modifier.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

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
    @CommandDefinition(route = "cosmetic|actioncosmetic help", permission = "actioncosmetic.command.help", runAsync = true)
    public void onHelpCommand(final @Source CommandSender sender) {

        sender.sendMessage(Component.text("/cosmetic jump: " + config.getJumpTitle()));
        sender.sendMessage(Component.text("/cosmetic sneak: " + config.getSneakTitle()));
    }

    @CommandDefinition(route = "cosmetic|actioncosmetic sneak", permission = "actioncosmetic.command.sneak", runAsync = true)
    public void onSneakCommand(final @Source Player player) {

        gui.openSneak(player);
    }

    @CommandDefinition(route = "cosmetic|actioncosmetic jump", permission = "actioncosmetic.command.jump", runAsync = true)
    public void onJumpCommand(final @Source Player player) {

        gui.openJump(player);
    }

    @CommandDefinition(route = "cosmetic|actioncosmetic reload", permission = "actioncosmetic.command.reload", runAsync = true)
    public void onReloadCommand(final @Source CommandSender sender) {

        config.reload();
        sender.sendMessage(Component.text("Config reloaded"));
    }
}
