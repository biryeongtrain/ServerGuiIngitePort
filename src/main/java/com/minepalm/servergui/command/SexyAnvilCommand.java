package com.minepalm.servergui.command;

import com.minepalm.servergui.api.ServerGuiClickType;
import com.minepalm.servergui.api.gui.AnvilInputGui;
import com.minepalm.servergui.impl.gui.element.GuiElement;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SexyAnvilCommand extends BukkitCommand {

    public SexyAnvilCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!player.isOp()) return false;
        ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();
        AnvilInputGui gui = new AnvilInputGui(player, true) {
            @Override
            public void onClose() {
                player.sendMessage(Component.text(this.getInput()));
            }
        };

        gui.setTitle(Component.text("Nice"));
        gui.setSlot(1, new GuiElement(new ItemStack(Material.DIAMOND_AXE), (index, clickType, actionType) -> {
            ItemStack item = gui.getSlot(index).getItemStack();
            if (clickType == ServerGuiClickType.MOUSE_LEFT) {
                item.setAmount(item.getAmount() == 1 ? item.getAmount() : item.getAmount() - 1);
            } else if (clickType == ServerGuiClickType.MOUSE_RIGHT) {
                item.setAmount(item.getAmount() + 1);
            }
            ((GuiElement) gui.getSlot(index)).setItemStack(item);
        }));

        gui.setSlot(2, new GuiElement(new ItemStack(Material.SLIME_BALL), (index, clickType, actionType) -> {
            player.sendMessage(Component.text(gui.getInput()));
        }));

        gui.setSlot(30, new ItemStack(Material.TNT));

        gui.open();

        return true;
    }
}
