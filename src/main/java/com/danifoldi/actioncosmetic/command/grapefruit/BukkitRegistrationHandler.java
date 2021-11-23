package com.danifoldi.actioncosmetic.command.grapefruit;

import com.danifoldi.actioncosmetic.ActionCosmeticPlugin;
import com.danifoldi.actioncosmetic.command.CommandManager;
import com.google.common.collect.ImmutableList;
import grapefruit.command.dispatcher.registration.CommandRegistrationContext;
import grapefruit.command.dispatcher.registration.CommandRegistrationHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unchecked")
public class BukkitRegistrationHandler implements CommandRegistrationHandler<CommandSender> {
    private static final CommandMap COMMAND_MAP = Bukkit.getCommandMap();
    private static final Constructor<PluginCommand> PLUGIN_COMMAND_CONSTRUCTOR;
    private static final Map<String, Command> KNOWN_COMMANDS;
    private final CommandManager manager;
    private final ActionCosmeticPlugin plugin;

    static {
        try {
            final Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PLUGIN_COMMAND_CONSTRUCTOR = constructor;

            final Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommands.setAccessible(true);
            KNOWN_COMMANDS = (Map<String, Command>) knownCommands.get(COMMAND_MAP);
        } catch (final ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public BukkitRegistrationHandler(final @NotNull CommandManager manager,
                                     final @NotNull ActionCosmeticPlugin plugin) {
        this.manager = requireNonNull(manager, "manager cannot be null");
        this.plugin = requireNonNull(plugin, "plugin cannot be null");
    }

    @Override
    public void accept(final @NotNull CommandRegistrationContext<CommandSender> context) {
        final String[] rootAliases = context.route().get(0).split("\\|");
        final String primray = rootAliases[0];
        final List<String> aliases = rootAliases.length > 1
                ? Arrays.asList(Arrays.copyOfRange(rootAliases, 1, rootAliases.length))
                : List.of();
        register(primray, aliases);
    }

    private void register(final @NotNull String primary, final @NotNull List<String> aliases) {
        final List<String> allAliases = ImmutableList.<String>builder()
                .add(primary)
                .addAll(aliases)
                .build();
        final CommandWrapper wrapper = new CommandWrapper(allAliases, this.manager);
        try {
            unregisterExisting(primary);
            final PluginCommand pCommand = constructCommand(primary, wrapper, this.plugin);
            pCommand.setAliases(aliases);
            allAliases.forEach(alias -> KNOWN_COMMANDS.put(alias, pCommand));
            registerListener(wrapper);

        } catch (final ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    private @NotNull PluginCommand constructCommand(final @NotNull String label,
                                                    final @NotNull CommandExecutor executor,
                                                    final @NotNull JavaPlugin plugin) throws ReflectiveOperationException {
        final PluginCommand pCommand = PLUGIN_COMMAND_CONSTRUCTOR.newInstance(label, plugin);
        pCommand.setExecutor(executor);
        return pCommand;
    }

    private void unregisterExisting(final @NotNull String label) {
        final Command command = KNOWN_COMMANDS.remove(label);
        if (command != null) {
            command.getAliases().forEach(KNOWN_COMMANDS::remove);
        }
    }

    private void registerListener(final @NotNull CommandWrapper wrapper) {
        Bukkit.getPluginManager().registerEvents(wrapper, this.plugin);
    }
}
