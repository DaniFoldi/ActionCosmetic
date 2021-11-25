package com.danifoldi.actioncosmetic.data;

import com.danifoldi.actioncosmetic.ActionCosmeticPlugin;
import com.danifoldi.actioncosmetic.config.Config;
import com.danifoldi.actioncosmetic.config.MessageUtil;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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

        ItemStack sneakBarrier = new ItemStack(Material.BARRIER);
        if (setting.getSneakSelection().equals("")) {

            sneakBarrier.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        }
        ItemMeta sneakMeta = sneakBarrier.getItemMeta();
        sneakMeta.displayName(Component.text(config.getNoneTitle()));
        sneakBarrier.setItemMeta(sneakMeta);
        sneakPane.addItem(new GuiItem(sneakBarrier, event -> selectSneak(gui, sneakPane, player.getUniqueId(), "", 0)), 0, 0);

        ItemStack jumpBarrier = new ItemStack(Material.BARRIER);
        if (setting.getJumpSelection().equals("")) {

            sneakBarrier.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        }
        ItemMeta jumpMeta = sneakBarrier.getItemMeta();
        jumpMeta.displayName(Component.text(config.getNoneTitle()));
        jumpBarrier.setItemMeta(jumpMeta);
        jumpPane.addItem(new GuiItem(jumpBarrier, event -> selectJump(gui, jumpPane, player.getUniqueId(), "", 0)), 0, 0);

        int s = 1;
        int j = 1;
        for (String action: config.getCosmetics().keySet()) {

            Cosmetic cosmetic = config.getCosmetics().get(action);
            if (player.hasPermission("actioncosmetic.sneak." + action)) {

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta)head.getItemMeta();
                PlayerProfile profile = Bukkit.createProfile(action);
                profile.setProperty(new ProfileProperty("textures", cosmetic.texture()));
                meta.setPlayerProfile(profile);
                meta.displayName(Component.text(MessageUtil.colorCodes(cosmetic.name())));
                head.setItemMeta(meta);
                if (setting.getSneakSelection().equals(action)) {

                    head.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
                }
                int finalS = s;
                GuiItem item = new GuiItem(head, event -> selectSneak(gui, sneakPane, player.getUniqueId(), action, finalS));
                sneakPane.addItem(item, s % 9, s / 9);
                s++;
            }
            if (player.hasPermission("actioncosmetic.jump." + action)) {

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta)head.getItemMeta();
                PlayerProfile profile = Bukkit.createProfile(action);
                profile.setProperty(new ProfileProperty("textures", cosmetic.texture()));
                meta.setPlayerProfile(profile);
                meta.displayName(Component.text(MessageUtil.colorCodes(cosmetic.name())));
                head.setItemMeta(meta);
                if (setting.getJumpSelection().equals(action)) {

                    head.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
                }
                int finalJ = j;
                GuiItem item = new GuiItem(head, event -> selectJump(gui, jumpPane, player.getUniqueId(), action, finalJ));
                jumpPane.addItem(item, j % 9, j / 9);
                j++;
            }
        }

        gui.addPane(sneakPane);
        gui.addPane(jumpPane);
        scheduler.runTask(plugin, () -> gui.show(player));
    }

    private void selectSneak(ChestGui gui, Pane pane, UUID uuid, String action, int i) {

        PlayerSetting setting = cache.get(uuid);
        AtomicInteger index = new AtomicInteger(0);
        pane.getItems().forEach(guiItem -> {
            if (index.getAndIncrement() == i) {

                guiItem.getItem().addUnsafeEnchantment(Enchantment.DURABILITY, 10);
            } else {

                guiItem.getItem().removeEnchantment(Enchantment.DURABILITY);
            }
        });
        cache.update(uuid, setting.withSneakSelection(action));
        gui.update();
    }

    private void selectJump(ChestGui gui,Pane pane, UUID uuid, String action, int i) {

        PlayerSetting setting = cache.get(uuid);
        AtomicInteger index = new AtomicInteger(0);
        pane.getItems().forEach(guiItem -> {
            if (index.getAndIncrement() == i) {

                guiItem.getItem().addUnsafeEnchantment(Enchantment.DURABILITY, 10);
            } else {

                guiItem.getItem().removeEnchantment(Enchantment.DURABILITY);
            }
        });
        cache.update(uuid, setting.withJumpSelection(action));
        gui.update();
    }
}
