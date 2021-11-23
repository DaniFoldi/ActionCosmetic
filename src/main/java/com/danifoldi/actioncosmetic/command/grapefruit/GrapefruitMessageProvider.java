package com.danifoldi.actioncosmetic.command.grapefruit;

import com.danifoldi.actioncosmetic.config.CustomMessageBuilder;
import grapefruit.command.message.MessageKey;
import grapefruit.command.message.MessageProvider;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GrapefruitMessageProvider implements MessageProvider {
    private final Map<String, String> keyCache = new ConcurrentHashMap<>();
    private final CustomMessageBuilder messageBuilder;

    @Inject
    public GrapefruitMessageProvider(final @NotNull CustomMessageBuilder messageBuilder) {
        this.messageBuilder = messageBuilder;
    }

    @Override
    public @NotNull String provide(final @NotNull MessageKey key) {
        final String epKey = this.keyCache.computeIfAbsent(key.key(), GrapefruitMessageProvider::toCamelCase);
        return this.messageBuilder.getBase(epKey).execute();
    }

    private static String toCamelCase(final @NotNull String notSoCamelCaseKey) {
        final String[] keyParts = notSoCamelCaseKey.split("-");
        if (keyParts.length == 0) {
            return "";
        }

        final StringBuilder keyBuilder = new StringBuilder();
        // The first part doesn't get modified
        keyBuilder.append(keyParts[0]);

        if (keyParts.length <= 1) {
            return keyBuilder.toString();
        }

        Arrays.stream(keyParts).skip(1L)
                .forEach(keyPart -> {
                    if (!keyPart.isEmpty()) {
                        final String camelCaseKeyPart = Character.toTitleCase(keyPart.charAt(0))
                                + (keyPart.length() > 1 ? keyPart.substring(1) : "");
                        keyBuilder.append(camelCaseKeyPart);
                    }
                });

        return keyBuilder.toString();
    }
}
