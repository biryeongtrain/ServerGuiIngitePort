package com.minepalm.servergui.impl.gui.virtual;

import com.minepalm.servergui.api.GuiHelpers;
import com.minepalm.servergui.api.gui.GuiInterface;
import com.minepalm.servergui.impl.gui.slot.SlotGuiInterface;
import com.minepalm.servergui.impl.gui.virtual.inventory.VirtualScreenHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;

public record SguiScreenHandlerFactory<T extends GuiInterface>(T gui, MenuConstructor factory) implements MenuProvider {

    @Override
    public Component getDisplayName() {
        Component component = GuiHelpers.convertAsMCComponent(this.gui.getTitle());
        if (component == null) {
            component = Component.empty();
        }
        return component;
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return factory.createMenu(syncId, playerInventory, player);
    }

   public static <T extends SlotGuiInterface> SguiScreenHandlerFactory<T> ofDefault(T gui) {
       return new SguiScreenHandlerFactory<>(gui, ((syncId, inv, player) -> new VirtualScreenHandler(gui.getType(), syncId, gui, (org.bukkit.entity.Player) player.getBukkitEntity())));
   }
}
