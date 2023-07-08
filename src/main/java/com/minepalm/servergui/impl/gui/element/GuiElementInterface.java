package com.minepalm.servergui.impl.gui.element;

import com.minepalm.servergui.api.ServerGuiClickType;
import com.minepalm.servergui.api.gui.GuiInterface;
import com.minepalm.servergui.impl.gui.slot.SlotGuiInterface;
import net.minecraft.world.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


@SuppressWarnings({"unused"})
public interface GuiElementInterface {
    ClickCallback EMPTY_CALLBACK = (x,y,z,a) -> {};
    ItemClickCallback EMPTY_CALLBACK_OLD = (x, y, z) -> {};

    @FunctionalInterface
    interface ClickCallback {

        /**
         * Executed when a GuiElement is clicked.
         *  @param index  the slot index
         * @param type   the simplified type of click
         * @param action the Minecraft action type
         * @param gui    the gui being source of the click
         */
        void click(int index, ServerGuiClickType type, net.minecraft.world.inventory.ClickType action, SlotGuiInterface gui);
    }

    @FunctionalInterface
    interface ItemClickCallback extends ClickCallback {

        /**
         * Executed when a GuiElement is clicked.
         *
         * @param index  the slot index
         * @param type   the simplified type of click
         * @param action the Minecraft action type
         */
        void click(int index, ServerGuiClickType type, ClickType action);

        default void click(int index, ServerGuiClickType type, ClickType action, SlotGuiInterface gui) {
            this.click(index, type, action);
        }
    }
    default ClickCallback getGuiCallback() {
        return this.getCallback();
    }


    /**
     * Returns the elements currently displayed stack
     *
     * @return the current stack
     */
    ItemStack getItemStack();

    default ItemStack getItemStackForDisplay(GuiInterface gui) {
        return this.getItemStack().clone();
    }

    /**
     * This method is called when this GuiElement is removed
     * from a SlotGuiInstance
     *
     * @param gui A gui to which this GuiElement is removed
     */
    default void onRemoved(SlotGuiInterface gui) {

    }

    /**
     * This method is called when this GuiElement is added
     * to a SlotGuiInstance
     *
     * @param gui A gui to which this GuiElement is added
     */
    default void onAdded(SlotGuiInterface gui) {

    }

    @Deprecated
    default ItemClickCallback getCallback() {
        return EMPTY_CALLBACK_OLD;
    }
}
