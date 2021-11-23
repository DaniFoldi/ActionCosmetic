package com.danifoldi.actioncosmetic.inject;

import com.danifoldi.actioncosmetic.command.CosmeticCommands;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import grapefruit.command.CommandContainer;

import javax.inject.Named;

@Module
public interface ActionCosmeticBindingModule {

    @Binds
    @IntoSet
    @Named("commands")
    CommandContainer bindCosmeticCommands(final CosmeticCommands commands);
}
