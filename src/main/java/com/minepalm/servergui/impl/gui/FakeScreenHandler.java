package com.minepalm.servergui.impl.gui;

import com.minepalm.servergui.api.gui.GuiInterface;
import com.minepalm.servergui.impl.gui.virtual.VirtualScreenHandlerInterface;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftInventoryView;
import org.bukkit.inventory.InventoryView;

/**
 * Some guis don't use screen handlers (Sign or book input)
 * This is mostly utility class to simplify implementation
 */
public class FakeScreenHandler extends AbstractContainerMenu implements VirtualScreenHandlerInterface {

    private final GuiInterface gui;
    private CraftInventoryView craftInventoryView;

    public FakeScreenHandler(GuiInterface gui) {
        super(null, -1);
        this.gui = gui;
        this.craftInventoryView = new CraftInventoryView(gui.getPlayer(), gui.getPlayer().getInventory(), this);
    }

    @Override
    public GuiInterface getGui() {
        return this.gui;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public InventoryView getBukkitView() {
        return this.craftInventoryView;
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
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
