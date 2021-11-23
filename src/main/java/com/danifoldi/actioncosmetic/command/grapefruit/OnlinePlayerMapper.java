package com.danifoldi.actioncosmetic.command.grapefruit;

import com.google.common.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;

public class OnlinePlayerMapper extends AbstractPlayerMapper<Player> {
    static final OnlinePlayerMapper INSTANCE = new OnlinePlayerMapper();

    private OnlinePlayerMapper() {
        super(TypeToken.of(Player.class));
    }

    @Override
    public @NotNull Optional<Player> getPlayer(final @NotNull String input) {
        final Matcher matcher = UUID_PATTERN.matcher(input);
        if (matcher.matches()) {
            final UUID uuid = UUID.fromString(input);
            return Optional.ofNullable(SERVER.getPlayer(uuid));
        } else {
            return Optional.ofNullable(SERVER.getPlayer(input));
        }
    }
}
