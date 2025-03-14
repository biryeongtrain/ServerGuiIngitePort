package com.minepalm.servergui.api.gui;

import com.minepalm.servergui.api.GuiHelpers;
import com.minepalm.servergui.impl.gui.element.GuiElementInterface;
import com.minepalm.servergui.impl.gui.slot.SlotGuiInterface;
import net.minecraft.world.inventory.Slot;
import org.bukkit.entity.Player;

public abstract class BaseSlotGui implements SlotGuiInterface {
    protected final Player player;
    protected final GuiElementInterface[] elements;
    protected final Slot[] slotRedirects;
    /**
     * @Deprecated Use isOpen() instead
     */
    @Deprecated(forRemoval = true)
    protected boolean open = false;
    protected boolean autoUpdate = true;
    protected boolean reOpen = false;
    protected final int size;


    public BaseSlotGui(Player player, int size) {
        this.player = player;
        this.elements = new GuiElementInterface[size];
        this.slotRedirects = new Slot[size];
        this.size = size;
    }

    @Override
    public void setSlot(int index, GuiElementInterface element) {
        if (this.elements[index] != null) {
            this.elements[index].onRemoved(this);
        }
        this.elements[index] = element;
        this.slotRedirects[index] = null;
        element.onAdded(this);
    }

    @Override
    public void setSlotRedirect(int index, Slot inventory) {
        if (this.elements[index] != null) {
            this.elements[index].onRemoved(this);
            this.elements[index] = null;
        }
        this.slotRedirects[index] = inventory;
    }

    @Override
    public int getFirstEmptySlot() {
        for (int i = 0; i < this.elements.length; i++) {
            if (this.elements[i] == null && this.slotRedirects[i] == null) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void clearSlot(int index) {
        if (this.elements[index] != null) {
            this.elements[index].onRemoved(this);
            this.elements[index] = null;
        }
        this.slotRedirects[index] = null;
    }

    @Override
    public GuiElementInterface getSlot(int index) {
        if (index >= 0 && index < this.size) {
            return this.elements[index];
        }
        return null;
    }

    @Override
    public Slot getSlotRedirect(int index) {
        if (index >= 0 && index < this.size) {
            return this.slotRedirects[index];
        }
        return null;
    }

    @Override
    public boolean isOpen() {
        return GuiHelpers.getCurrentGui(this.player) == this;
    }

    @Override
    public boolean getAutoUpdate() {
        return this.autoUpdate;
    }

    @Override
    public void setAutoUpdate(boolean value) {
        this.autoUpdate = value;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }
}
