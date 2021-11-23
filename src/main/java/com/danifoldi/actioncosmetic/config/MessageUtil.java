package com.danifoldi.actioncosmetic.config;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageUtil {

    private static final @NotNull Pattern HEX = Pattern.compile("(&#[a-fA-F0-9]{6})");

    public static @NotNull String colorCodes(String text) {
        final @NotNull Matcher matcher = HEX.matcher(text);

        while (matcher.find()) {
            final @NotNull String color = matcher.group(1);
            final @NotNull String value = ChatColor.COLOR_CHAR + "x" + color.replace("&#", "").chars().mapToObj(i -> (char)i).map(String::valueOf).map(s -> ChatColor.COLOR_CHAR + s).collect(Collectors.joining());
            text = text.replace(color, value);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
