package com.danifoldi.actioncosmetic.data;

import com.danifoldi.actioncosmetic.ActionCosmeticPlugin;
import com.danifoldi.actioncosmetic.config.Config;
import com.danifoldi.actioncosmetic.config.MessageUtil;
import com.danifoldi.dataverse.util.Pair;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ActionGui {

    private final @NotNull Config config;
    private final @NotNull ActionCosmeticPlugin plugin;
    private final @NotNull BukkitScheduler scheduler;
    private final @NotNull SettingCache cache;

    @Inject
    public ActionGui(final @NotNull Config config,
                     final @NotNull ActionCosmeticPlugin plugin,
                     final @NotNull BukkitScheduler scheduler,
                     final @NotNull SettingCache cache) {

        this.config = config;
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.cache = cache;
    }

    public void openSneak(Player player) {

        PlayerSetting setting = cache.get(player.getUniqueId());
        ChestGui gui = new ChestGui(3, MessageUtil.colorCodes(config.getSneakTitle()));
        StaticPane sneakPane = new StaticPane(0, 0, 9, 3, Pane.Priority.NORMAL);
        List<Pair<ItemStack, Cosmetic>> sneakHeads = new ArrayList<>();

        int s = 0;
        for (Cosmetic cosmetic: config.getCosmetics()) {

            if (cosmetic.action().equals("") || player.hasPermission("actioncosmetic.sneak." + cosmetic.action())) {

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = head.getItemMeta();
                meta.displayName(Component.text(MessageUtil.colorCodes(cosmetic.name())));
                head.setItemMeta(meta);
                applyTexture(head, cosmetic, setting.getSneakSelection().equals(cosmetic.action()));
                GuiItem item = new GuiItem(head, event -> {

                    sneakHeads.forEach(p -> applyTexture(p.getFirst(), p.getSecond(), p.getSecond().equals(cosmetic)));
                    cache.update(player.getUniqueId(), cache.get(player.getUniqueId()).withSneakSelection(cosmetic.action()));
                    gui.update();
                });
                sneakPane.addItem(item, s % 9, s / 9);
                sneakHeads.add(Pair.of(head, cosmetic));
                s++;
            }
        }

        gui.addPane(sneakPane);
        scheduler.runTask(plugin, () -> gui.show(player));
    }

    public void openJump(Player player) {

        PlayerSetting setting = cache.get(player.getUniqueId());
        ChestGui gui = new ChestGui(3, MessageUtil.colorCodes(config.getJumpTitle()));
        StaticPane jumpPane = new StaticPane(0, 0, 9, 3, Pane.Priority.NORMAL);
        List<Pair<ItemStack, Cosmetic>> jumpHeads = new ArrayList<>();

        int j = 0;
        for (Cosmetic cosmetic: config.getCosmetics()) {

            if (cosmetic.action().equals("") || player.hasPermission("actioncosmetic.jump." + cosmetic.action())) {

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = head.getItemMeta();
                meta.displayName(Component.text(MessageUtil.colorCodes(cosmetic.name())));
                head.setItemMeta(meta);
                applyTexture(head, cosmetic, setting.getJumpSelection().equals(cosmetic.action()));
                GuiItem item = new GuiItem(head, event -> {

                    jumpHeads.forEach(p -> applyTexture(p.getFirst(), p.getSecond(), p.getSecond().equals(cosmetic)));
                    cache.update(player.getUniqueId(), cache.get(player.getUniqueId()).withJumpSelection(cosmetic.action()));
                    gui.update();
                });
                jumpPane.addItem(item, j % 9, j / 9);
                jumpHeads.add(Pair.of(head, cosmetic));
                j++;
            }
        }

        gui.addPane(jumpPane);
        scheduler.runTask(plugin, () -> gui.show(player));
    }

    private void applyTexture(ItemStack item, Cosmetic cosmetic, boolean selected) {

        SkullMeta meta = (SkullMeta)item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile("act" + cosmetic.action());
        profile.setProperty(new ProfileProperty("textures", selected ? cosmetic.activeTexture() : cosmetic.texture()));
        meta.setPlayerProfile(profile);
        meta.displayName(Component.text(MessageUtil.colorCodes(cosmetic.name())));
        item.setItemMeta(meta);
    }
}
