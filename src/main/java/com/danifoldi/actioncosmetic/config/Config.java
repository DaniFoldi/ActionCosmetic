package com.danifoldi.actioncosmetic.config;

import com.danifoldi.actioncosmetic.data.Cosmetic;
import com.danifoldi.dml.DmlParser;
import com.danifoldi.dml.exception.DmlParseException;
import com.danifoldi.dml.type.DmlKey;
import com.danifoldi.dml.type.DmlObject;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@Singleton
public class Config {

    private final @NotNull Logger logger;
    @Inject
    public Config(final @NotNull Logger logger) {
        this.logger = logger;
    }

    private String sneakTitle;
    private String jumpTitle;
    private String sneakCommand;
    private String jumpCommand;
    private List<Cosmetic> cosmetics;

    private Path configFile;

    public void load(Path file) throws IOException, DmlParseException {
        configFile = file;
        DmlObject config = DmlParser.parse(file).asObject();
        sneakTitle = config.get("sneakTitle").asString().value();
        jumpTitle = config.get("jumpTitle").asString().value();
        sneakCommand = config.get("sneakCommand").asString().value();
        jumpCommand = config.get("jumpCommand").asString().value();
        DmlObject configuredCosmetics = config.get("cosmetics").asObject();

        cosmetics = Collections.synchronizedList(new ArrayList<>());
        cosmetics.add(new Cosmetic(
                "",
                config.get("noneTexture").asString().value(),
                config.get("noneActiveTexture").asString().value(),
                null,
                config.get("noneTitle").asString().value()));

        for (DmlKey cosmeticName: configuredCosmetics.keys()) {
            cosmetics.add(new Cosmetic(
                    cosmeticName.value(),
                    configuredCosmetics.get(cosmeticName).asObject().get("texture").asString().value(),
                    configuredCosmetics.get(cosmeticName).asObject().get("activeTexture").asString().value(),
                    Particle.valueOf(configuredCosmetics.get(cosmeticName).asObject().get("particle").asString().value().toUpperCase(Locale.ROOT)),
                    configuredCosmetics.get(cosmeticName).asObject().get("name").asString().value()));
        }
    }

    public void reload() {
        try {
            load(configFile);
        } catch (IOException | DmlParseException e) {
            logger.severe("Failed to load config file");
            e.printStackTrace();
        }
    }

    public String getSneakTitle() {
        return sneakTitle;
    }

    public String getJumpTitle() {
        return jumpTitle;
    }

    public String getSneakCommand() {
        return sneakCommand;
    }

    public String getJumpCommand() {
        return jumpCommand;
    }

    public List<Cosmetic> getCosmetics() {
        return List.copyOf(cosmetics);
    }
}
