package com.danifoldi.actioncosmetic.command.grapefruit;

import grapefruit.command.parameter.mapper.ParameterMapper;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface PlayerMapper<P extends OfflinePlayer> extends ParameterMapper<CommandSender, P> {

    @NotNull Optional<P> getPlayer(final @NotNull String input);

    static @NotNull PlayerMapper<Player> online() {
        return OnlinePlayerMapper.INSTANCE;
    }

    static @NotNull PlayerMapper<OfflinePlayer> offline() {
        return OfflinePlayerMapper.INSTANCE;
    }
}
