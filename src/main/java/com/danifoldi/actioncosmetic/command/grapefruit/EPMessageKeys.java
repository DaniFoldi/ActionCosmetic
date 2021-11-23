package com.danifoldi.actioncosmetic.command.grapefruit;

import grapefruit.command.message.MessageKey;

public final class EPMessageKeys {
    public static final MessageKey PLAYER_NOT_FOUND =  MessageKey.of("command.playerNotFound");

    private EPMessageKeys() {
        throw new UnsupportedOperationException();
    }
}
