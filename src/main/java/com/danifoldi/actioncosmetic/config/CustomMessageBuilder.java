package com.danifoldi.actioncosmetic.config;

import com.danifoldi.messagelib.core.SimpleMessageBuilder;
import com.danifoldi.messagelib.templateprocessor.TemplateProcessor;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CustomMessageBuilder extends SimpleMessageBuilder<String> {
    private final CustomMessageProvider messageProvider;

    @Inject
    public CustomMessageBuilder(final @NotNull CustomMessageProvider messageProvider) {
        super(messageProvider, TemplateProcessor.bracket());
        this.messageProvider = messageProvider;
    }
}
