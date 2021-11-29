package com.danifoldi.actioncosmetic.data;

import org.bukkit.Particle;

public record Cosmetic(String action, String texture, String activeTexture, Particle particle, String name) {
}
