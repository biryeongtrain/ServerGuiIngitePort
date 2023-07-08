package com.minepalm.servergui.command;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.minepalm.servergui.api.GuiHelpers;
import com.minepalm.servergui.api.GuiTypes;
import com.minepalm.servergui.api.ServerGuiClickType;
import com.minepalm.servergui.api.gui.SimpleGui;
import com.minepalm.servergui.impl.gui.element.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.inventory.Slot;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SimpleGUITestCommand extends BukkitCommand {

    public SimpleGUITestCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!player.isOp()) return false;

        SimpleGui gui = new SimpleGui(GuiTypes.GENERIC_3x3, player, false) {
            @Override
            public boolean onClick(int index, ServerGuiClickType type, net.minecraft.world.inventory.ClickType action, GuiElementInterface element) {
                this.player.sendMessage(Component.text(type.toString()));

                return super.onClick(index, type, action, element);
            }

            @Override
            public void onTick() {
                this.setSlot(0, new GuiElementBuilder(Material.ARROW).setCount((int) (player.getWorld().getTime() % 127)));
                super.onTick();
            }
        };
        gui.setTitle(Component.text("Nice"));
        gui.setSlot(0, new GuiElementBuilder(Material.ARROW).setCount(100));
        gui.setSlot(1, new AnimatedGuiElement(new ItemStack[]{
                new ItemStack(Material.NETHERITE_PICKAXE),
                new ItemStack(Material.DIAMOND_PICKAXE),
                new ItemStack(Material.GOLDEN_PICKAXE),
                new ItemStack(Material.IRON_PICKAXE),
                new ItemStack(Material.STONE_PICKAXE),
                new ItemStack(Material.WOODEN_PICKAXE)
        }, 10, false, (x, y, z) -> {
        }));

        gui.setSlot(2, new AnimatedGuiElementBuilder()
                .setItem(Material.NETHERITE_AXE).setDamage(150).saveItemStack()
                .setItem(Material.DIAMOND_AXE).setDamage(150).unbreakable().saveItemStack()
                .setItem(Material.GOLDEN_AXE).glow().saveItemStack()
                .setItem(Material.IRON_AXE).enchant(Enchantment.WATER_WORKER, 1).hideFlags().saveItemStack()
                .setItem(Material.STONE_AXE).saveItemStack()
                .setItem(Material.WOODEN_AXE).saveItemStack()
                .setInterval(10).setRandom(true)
        );

        for (int x = 3; x < gui.getSize(); x++) {
            ItemStack itemStack = new ItemStack(Material.STONE);
            itemStack.setAmount(x);
            gui.setSlot(x, new GuiElement(itemStack, (index, clickType, actionType) -> {
            }));
        }

        gui.setSlot(5, new GuiElementBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(
                        "ewogICJ0aW1lc3RhbXAiIDogMTYxOTk3MDIyMjQzOCwKICAicHJvZmlsZUlkIiA6ICI2OTBkMDM2OGM2NTE0OGM5ODZjMzEwN2FjMmRjNjFlYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ5emZyXzciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI0OGVhYTQxNGNjZjA1NmJhOTY5ZTdkODAxZmI2YTkyNzhkMGZlYWUxOGUyMTczNTZjYzhhOTQ2NTY0MzU1ZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9")
                .setName(Component.text("Battery"))
                .glow()
        );

        gui.setSlot(6, new GuiElementBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(new CraftPlayerProfile(UUID.fromString("f5a216d9-d660-4996-8d0f-d49053677676"), "patbox"))
                .setName(Component.text("Patbox's Head"))
                .glow()
        );

        gui.setSlot(7, new GuiElementBuilder()
                .setItem(Material.BARRIER)
                .glow()
                .setName(Component.text("Bye")
                        .style(Style.style(TextDecoration.ITALIC, TextDecoration.BOLD)))
                .addLoreLine(Component.text("Some lore"))
                .addLoreLine(Component.text("More lore").color(NamedTextColor.RED))
                .setCount(3)
                .setCallback((index, clickType, actionType) -> gui.close())
        );

        gui.setSlot(8, new GuiElementBuilder()
                .setItem(Material.TNT)
                .glow()
                .setName(Component.text("Test :)")
                        .style(Style.style(TextDecoration.ITALIC, TextDecoration.BOLD)))
                .addLoreLine(Component.text("Some lore"))
                .addLoreLine(Component.text("More lore").color(NamedTextColor.RED))
                .setCount(1)
                .setCallback((index, clickType, actionType) -> {
                    gui.setPlayerClose(true);
                    player.sendMessage(Component.text("derg "));
                    ItemStack item = gui.getSlot(index).getItemStack();
                    if (clickType == ServerGuiClickType.MOUSE_LEFT) {
                        item.setAmount(item.getAmount() == 1 ? item.getAmount() : item.getAmount() - 1);
                    } else if (clickType == ServerGuiClickType.MOUSE_RIGHT) {
                        item.setAmount(item.getAmount() + 1);
                    }
                    ((GuiElement) gui.getSlot(index)).setItemStack(item);
                    if (item.getAmount() <= player.getEnderChest().getSize()) {
                        gui.setSlotRedirect(4, new Slot(GuiHelpers.getServerPlayer(player).getEnderChestInventory(), item.getAmount() - 1, 0, 0));
                    }
                })
        );
        gui.setSlotRedirect(4, new Slot(GuiHelpers.getServerPlayer(player).getEnderChestInventory(), 0,0,0));

        gui.open();
        return true;
    }
}
