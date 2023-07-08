package com.minepalm.servergui.impl.gui.element;

import com.minepalm.servergui.api.gui.GuiInterface;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GuiElement implements GuiElementInterface {
    public static final GuiElement EMPTY = new GuiElement(new ItemStack(Material.AIR), EMPTY_CALLBACK);

    protected final ClickCallback callback;
    protected ItemStack item;

    /**
     * Constructs a GuiElement with the supplied options.
     *
     * @param item     the stack to use for display
     * @param callback the callback to execute when the element is selected
     * @see GuiElementBuilder
     */
    public GuiElement(ItemStack item, ClickCallback callback) {
        this.item = item;
        this.callback = callback;
    }

    /**
     * Constructs a GuiElement with the supplied options.
     *
     * @param item     the stack to use for display
     * @param callback the callback to execute when the element is selected
     * @see GuiElementBuilder
     */
    public GuiElement(ItemStack item, ItemClickCallback callback) {
        this.item = item;
        this.callback = callback;
    }

    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    /**
     * Sets the display ItemStack
     *
     * @param itemStack the display item
     */
    public void setItemStack(ItemStack itemStack) {
        this.item = itemStack;
    }

    @Override
    public ClickCallback getGuiCallback() {
        return this.callback;
    }

    @Override
    public ItemStack getItemStackForDisplay(GuiInterface gui) {
        return this.item.clone();
    }
}

