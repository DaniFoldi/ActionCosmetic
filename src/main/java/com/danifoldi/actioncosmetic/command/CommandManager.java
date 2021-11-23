package com.danifoldi.actioncosmetic.command;

import com.danifoldi.actioncosmetic.ActionCosmeticPlugin;
import com.danifoldi.actioncosmetic.command.grapefruit.BukkitRegistrationHandler;
import com.danifoldi.actioncosmetic.command.grapefruit.GrapefruitMessageProvider;
import com.danifoldi.actioncosmetic.command.grapefruit.PlayerMapper;
import com.danifoldi.dataverse.lib.inject.Singleton;
import com.google.common.reflect.TypeToken;
import grapefruit.command.CommandContainer;
import grapefruit.command.dispatcher.CommandDispatcher;
import grapefruit.command.parameter.mapper.ParameterMapperRegistry;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Singleton
public final class CommandManager {
    private final Logger logger;
    private final ExecutorService asyncExecutor;
    private final Set<CommandContainer> commands;
    private final CommandDispatcher<CommandSender> dispatcher;

    @Inject
    public CommandManager(final @NotNull ActionCosmeticPlugin plugin,
                          final @NotNull @Named("commands") Set<CommandContainer> commands,
                          final @NotNull Logger logger,
                          final @NotNull ExecutorService asyncExecutor,
                          final @NotNull GrapefruitMessageProvider messageProvider) {
        this.commands = commands;
        this.logger = logger;
        this.asyncExecutor = asyncExecutor;
        this.dispatcher = CommandDispatcher.builder(TypeToken.of(CommandSender.class))
                .withAuthorizer(CommandSender::hasPermission)
                .withMessenger(CommandSender::sendMessage)
                .withMessageProvider(messageProvider)
                .withAsyncExecutor(this.asyncExecutor)
                .withRegistrationHandler(new BukkitRegistrationHandler(this, plugin))
                .build();
    }

    public void setup() {
        final ParameterMapperRegistry<CommandSender> mapperRegistry = this.dispatcher.mappers();
        mapperRegistry.registerMapper(PlayerMapper.offline());
        mapperRegistry.registerMapper(PlayerMapper.online());
        this.commands.forEach(this.dispatcher::registerCommands);
    }

    public void destroy() {
        this.asyncExecutor.shutdown();
        try {
            if (!this.asyncExecutor.awaitTermination(1000L, TimeUnit.SECONDS)) {
                this.asyncExecutor.shutdownNow();
            }
        } catch (final InterruptedException ex) {
            logger.warning("Shutting down async executor, potentially interrupting pending commands");
            this.asyncExecutor.shutdownNow();
        }
    }

    public @NotNull CommandDispatcher<CommandSender> getDispatcher() {
        return this.dispatcher;
    }
}
