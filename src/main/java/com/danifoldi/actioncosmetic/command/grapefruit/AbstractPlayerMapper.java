package com.danifoldi.actioncosmetic.command.grapefruit;

import com.google.common.reflect.TypeToken;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.CommandInput;
import grapefruit.command.message.Message;
import grapefruit.command.message.Template;
import grapefruit.command.parameter.mapper.AbstractParameterMapper;
import grapefruit.command.parameter.mapper.ParameterMappingException;
import grapefruit.command.util.AnnotationList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public abstract class AbstractPlayerMapper<P extends OfflinePlayer>
        extends AbstractParameterMapper<CommandSender, P>
        implements PlayerMapper<P> {
    protected static final Pattern UUID_PATTERN =
            Pattern.compile("([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})");
    protected static final Server SERVER = Bukkit.getServer();

    protected AbstractPlayerMapper(final @NotNull TypeToken<P> type) {
        super(requireNonNull(type, "type cannot be null"));
    }

    @Override
    public final @NotNull P map(final @NotNull CommandContext<CommandSender> context,
                                    final @NotNull Queue<CommandInput> args,
                                    final @NotNull AnnotationList modifiers) throws ParameterMappingException {
        final String input = args.element().rawArg();
        return getPlayer(input).orElseThrow(() -> new ParameterMappingException(Message.of(
                EPMessageKeys.PLAYER_NOT_FOUND,
                Template.of("{player}", input)
        )));
    }

    @Override
    public final @NotNull List<String> listSuggestions(final @NotNull CommandContext<CommandSender> context,
                                                       final @NotNull String currentArg,
                                                       final @NotNull AnnotationList modifiers) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> {
                    for (final MetadataValue meta : player.getMetadata("vanished")) {
                        if (meta.asBoolean()) {
                            return true;
                        }
                    }

                    return false;
                })
                .map(Player::getName)
                .toList();
    }
}
