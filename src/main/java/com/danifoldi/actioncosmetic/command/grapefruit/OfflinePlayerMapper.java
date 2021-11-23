package com.danifoldi.actioncosmetic.command.grapefruit;

import com.google.common.reflect.TypeToken;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;

@SuppressWarnings("deprecation")
public class OfflinePlayerMapper extends AbstractPlayerMapper<OfflinePlayer> {
    static final OfflinePlayerMapper INSTANCE = new OfflinePlayerMapper();

    private OfflinePlayerMapper() {
        super(TypeToken.of(OfflinePlayer.class));
    }

    @Override
    public @NotNull Optional<OfflinePlayer> getPlayer(final @NotNull String input) {
        final Matcher matcher = UUID_PATTERN.matcher(input);
        final OfflinePlayer result;

        if (matcher.matches()) {
            final UUID uuid = UUID.fromString(input);
            final OfflinePlayer online = SERVER.getPlayer(uuid);
            result = online != null ? online : SERVER.getOfflinePlayer(uuid);
        } else {
            final OfflinePlayer online = SERVER.getPlayer(input);
            result = online != null ? online : SERVER.getOfflinePlayer(input);
        }

        if (result.isOnline() || result.hasPlayedBefore()) {
            return Optional.of(result);
        }

        return Optional.empty();
    }
}
