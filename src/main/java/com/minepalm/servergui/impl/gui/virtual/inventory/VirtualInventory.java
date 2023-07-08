package com.minepalm.servergui.impl.gui.virtual.inventory;

import com.minepalm.servergui.impl.gui.element.GuiElementInterface;
import com.minepalm.servergui.impl.gui.slot.SlotGuiInterface;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record VirtualInventory(SlotGuiInterface gui) implements Container {

    public static List<HumanEntity> transaction = new ArrayList<>();
    @Override
    public int getContainerSize() {
        return this.gui.getSize();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        Slot slot = this.gui.getSlotRedirect(index);
        if (slot != null) {
            return slot.getItem();
        } else {
            GuiElementInterface element = this.gui.getSlot(index);
            if (element == null) {
                return ItemStack.EMPTY;
            }
            return CraftItemStack.asNMSCopy(element.getItemStackForDisplay(this.gui));
        }
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        Slot slot = this.gui.getSlotRedirect(index);
        if (slot != null) {
            return slot.container.removeItem(index, count);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        Slot slot = this.gui.getSlotRedirect(index);
        if (slot != null) {
            return slot.container.removeItemNoUpdate(index);
        }
        return ItemStack.EMPTY;
    }


    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        Slot slot = this.gui.getSlotRedirect(index);
        if (slot != null) {
            slot.container.setItem(index, stack);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public List<ItemStack> getContents() {
        return null;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
    }

    @Override
    public void onClose(CraftHumanEntity who) {
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public InventoryHolder getOwner() {
        return null;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void clearContent() {

    }
}
