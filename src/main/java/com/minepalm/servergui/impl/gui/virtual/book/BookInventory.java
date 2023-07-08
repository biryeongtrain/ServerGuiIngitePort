package com.minepalm.servergui.impl.gui.virtual.book;

import com.minepalm.servergui.api.gui.BookGui;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookInventory implements Container {
    private final BookGui gui;
    public List<HumanEntity> transaction = new ArrayList<>();
    private int maxStack = 1;
    public BookInventory(BookGui gui) {
        this.gui = gui;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int slot) {
        return CraftItemStack.asNMSCopy(gui.getBook());
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {

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
        //gui.onOpen();
    }
    @Override
    public void onClose(CraftHumanEntity who) {
       // gui.onClose();
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

    public BookGui gui() {
        return gui;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BookInventory) obj;
        return Objects.equals(this.gui, that.gui);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gui);
    }

    @Override
    public String toString() {
        return "BookInventory[" +
                "gui=" + gui + ']';
    }

}
