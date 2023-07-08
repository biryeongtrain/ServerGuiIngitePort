package com.minepalm.servergui.impl.gui.virtual.book;

import com.minepalm.servergui.api.gui.BookGui;
import com.minepalm.servergui.impl.gui.virtual.VirtualScreenHandlerInterface;
import com.minepalm.servergui.impl.gui.virtual.inventory.VirtualSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftInventoryLectern;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;

public class BookScreenHandler extends AbstractContainerMenu implements VirtualScreenHandlerInterface {
    private final BookGui gui;
    private CraftInventoryView bukkitEntity = null;
    private org.bukkit.entity.Player player;
    private final ArrayList<HumanEntity> transaction = new ArrayList<>();
    public  BookScreenHandler(int syncId, BookGui gui, org.bukkit.entity.Player player) {
        super(MenuType.LECTERN, syncId);
        this.gui = gui;
        BookInventory bookInventory = new BookInventory(gui);
        CraftInventoryLectern inventory = new CraftInventoryLectern(bookInventory);
        this.bukkitEntity = new CraftInventoryView(player, inventory, this);
        this.player = player;

        this.addSlot(new BookSlot(bookInventory, 0, 0, 0));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case 1 -> {
                this.gui.onPreviousPageButton();
                return true;
            }
            case 2 -> {
                this.gui.onNextPageButton();
                return true;
            }
            case 3 -> {
                this.gui.onTakeBookButton();
                return true;
            }
        }
        if (id >= 100) {
            this.gui.setPage(id - 100);
            return true;
        }
        return false;
    }

    @Override
    public BookGui getGui() {
        return gui;
    }

    @Override
    public InventoryView getBukkitView() {
        return this.bukkitEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }


    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void setItem(int slot, int revision, ItemStack stack) {
        if (slot == 0) {
            this.getSlot(slot).setByPlayer(stack);
        } else {
            this.getSlot(slot).setByPlayer(ItemStack.EMPTY);
        }
    }

    @Override
    public void broadcastChanges() {
        try {
            this.gui.onTick();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.broadcastChanges();
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return !(slot instanceof VirtualSlot) && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        return false;
    }
}
