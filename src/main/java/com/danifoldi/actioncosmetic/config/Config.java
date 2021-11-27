package com.danifoldi.actioncosmetic.config;

import com.danifoldi.actioncosmetic.data.Cosmetic;
import com.danifoldi.dml.DmlParseException;
import com.danifoldi.dml.DmlParser;
import com.danifoldi.dml.type.DmlKey;
import com.danifoldi.dml.type.DmlObject;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Singleton
public class Config {

    private final @NotNull Logger logger;
    @Inject
    public Config(final @NotNull Logger logger) {
        this.logger = logger;
    }

    private String guiTitle;
    private Map<String, Cosmetic> cosmetics;

    private Path configFile;

    public void load(Path file) throws IOException, DmlParseException {
        configFile = file;
        DmlObject config = DmlParser.parse(file).asObject();
        guiTitle = config.get("guiTitle").asString().value();
        DmlObject configuredCosmetics = config.get("cosmetics").asObject();

        cosmetics = new ConcurrentHashMap<>();
        cosmetics.put("", new Cosmetic(
                config.get("noneTexture").asString().value(),
                config.get("noneActiveTexture").asString().value(),
                null,
                config.get("noneTitle").asString().value()));

        for (DmlKey cosmeticName: configuredCosmetics.keys()) {
            cosmetics.put(cosmeticName.value(), new Cosmetic(
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

    public String getGuiTitle() {
        return guiTitle;
    }

    public Map<String, Cosmetic> getCosmetics() {
        return Map.copyOf(cosmetics);
    }
}
