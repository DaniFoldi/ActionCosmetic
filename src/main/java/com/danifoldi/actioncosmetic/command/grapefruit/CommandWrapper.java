package com.danifoldi.actioncosmetic.command.grapefruit;

import com.danifoldi.actioncosmetic.command.CommandManager;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

public final class CommandWrapper implements CommandExecutor, Listener {
    private final List<String> aliases;
    private final CommandManager manager;

    CommandWrapper(final @NotNull List<String> aliases,
                   final @NotNull CommandManager manager) {
        this.aliases = requireNonNull(aliases, "aliases cannot be null");
        this.manager = requireNonNull(manager, "manager cannot be null");
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender,
                             final @NotNull Command command,
                             final @NotNull String label,
                             final @NotNull String[] args) {
        final StringJoiner joiner = new StringJoiner(" ");
        joiner.add(command.getName());
        Arrays.stream(args).forEach(joiner::add);

        this.manager.getDispatcher().dispatchCommand(sender, joiner.toString());
        return true;
    }

    @EventHandler
    public void on(final @NotNull AsyncTabCompleteEvent event) {
        final String buffer = event.getBuffer();
        if ((!event.isCommand() && !buffer.startsWith("/")) || buffer.indexOf(' ') == -1) {
            return;
        }

        final List<String> args = Lists.newArrayList(buffer.split(" ", -1));
        final String root = stripSlash(args.get(0));
        if (!containsIgnoreCase(this.aliases, root)) {
            return;
        }

        if (args.size() > 1) {
            args.set(0, root); //remove '/' prefix
        }

        final List<String> completions = this.manager.getDispatcher().listSuggestions(
                event.getSender(), String.join(" ", args));
        event.setCompletions(completions);
        event.setHandled(true);
    }

    private String stripSlash(final @NotNull String arg) {
        requireNonNull(arg, "arg cannot be null");
        return arg.startsWith("/") ? arg.substring(1) : arg;
    }

    private boolean containsIgnoreCase(final @NotNull Collection<String> elements,
                                             final @NotNull String element) {
        return elements.stream().anyMatch(element::equalsIgnoreCase);
    }
}
