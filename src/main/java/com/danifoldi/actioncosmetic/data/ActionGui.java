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

    public void open(Player player) {

        PlayerSetting setting = cache.get(player.getUniqueId());
        ChestGui gui = new ChestGui(6, MessageUtil.colorCodes(config.getGuiTitle()));
        StaticPane sneakPane = new StaticPane(0, 0, 9, 3, Pane.Priority.NORMAL);
        StaticPane jumpPane = new StaticPane(0, 3, 9, 3, Pane.Priority.NORMAL);
        List<Pair<ItemStack, String>> sneakHeads = new ArrayList<>();
        List<Pair<ItemStack, String>> jumpHeads = new ArrayList<>();

        int s = 0;
        int j = 0;
        for (String action: config.getCosmetics().keySet()) {

            Cosmetic cosmetic = config.getCosmetics().get(action);
            if (action.equals("") || player.hasPermission("actioncosmetic.sneak." + action)) {

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = head.getItemMeta();
                meta.displayName(Component.text(MessageUtil.colorCodes(cosmetic.name())));
                head.setItemMeta(meta);
                applyTexture(head, action, setting.getSneakSelection().equals(action));
                GuiItem item = new GuiItem(head, event -> {

                    sneakHeads.forEach(p -> applyTexture(p.getFirst(), p.getSecond(), p.getSecond().equals(action)));
                    cache.update(player.getUniqueId(), cache.get(player.getUniqueId()).withSneakSelection(action));
                    gui.update();
                });
                sneakPane.addItem(item, s % 9, s / 9);
                sneakHeads.add(Pair.of(head, action));
                s++;
            }
            if (action.equals("") || player.hasPermission("actioncosmetic.jump." + action)) {

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = head.getItemMeta();
                meta.displayName(Component.text(MessageUtil.colorCodes(cosmetic.name())));
                head.setItemMeta(meta);
                applyTexture(head, action, setting.getJumpSelection().equals(action));
                GuiItem item = new GuiItem(head, event -> {

                    jumpHeads.forEach(p -> applyTexture(p.getFirst(), p.getSecond(), p.getSecond().equals(action)));
                    cache.update(player.getUniqueId(), cache.get(player.getUniqueId()).withJumpSelection(action));
                    gui.update();
                });
                jumpPane.addItem(item, j % 9, j / 9);
                jumpHeads.add(Pair.of(head, action));
                j++;
            }
        }

        gui.addPane(sneakPane);
        gui.addPane(jumpPane);
        scheduler.runTask(plugin, () -> gui.show(player));
    }

    private void applyTexture(ItemStack item, String action, boolean selected) {

        SkullMeta meta = (SkullMeta)item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile("act" + action);
        profile.setProperty(new ProfileProperty("textures", selected ? config.getCosmetics().get(action).activeTexture() : config.getCosmetics().get(action).texture()));
        meta.setPlayerProfile(profile);
        meta.displayName(Component.text(MessageUtil.colorCodes(config.getCosmetics().get(action).name())));
        item.setItemMeta(meta);
    }
}
