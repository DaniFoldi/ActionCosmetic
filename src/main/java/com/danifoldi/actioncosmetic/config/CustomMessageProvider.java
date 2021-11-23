package com.danifoldi.actioncosmetic.config;

import com.danifoldi.dml.DmlParseException;
import com.danifoldi.dml.DmlParser;
import com.danifoldi.dml.type.DmlObject;
import com.danifoldi.messagelib.messageprovider.MessageProvider;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class CustomMessageProvider implements MessageProvider<String> {
    private final Map<String, String> cachedMessages = new ConcurrentHashMap<>();
    private final Path datafolder;
    private final Logger logger;
    private DmlObject config;

    @Inject
    public CustomMessageProvider(final @NotNull Path datafolder,
                                 final @NotNull Logger logger) {
        this.datafolder = datafolder;
        this.logger = logger;
    }

    public void load() throws DmlParseException, IOException {
        this.cachedMessages.clear();
        final Path file = FileUtil.copyIfNotExists("messages.dml", this.datafolder);
        this.config = DmlParser.parse(file).asObject();
    }

    @Override
    public @NotNull String getMessageBase(final @NotNull String id) {
        return this.cachedMessages.computeIfAbsent(id, val -> {
            try {
                String[] parts = id.split("\\.");
                DmlObject next = config;
                for (String part: Arrays.stream(parts).limit(parts.length - 1).toList()) {
                    next = next.get(part).asObject();
                }
                return MessageUtil.colorCodes(next.get(parts[parts.length - 1]).asString().value());
            } catch (NullPointerException e) {
                this.logger.log(Level.WARNING, "Could not find a message with id {0}", id);
                return id;
            }
        });
    }
}