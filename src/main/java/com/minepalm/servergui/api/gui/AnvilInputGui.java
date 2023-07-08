package com.minepalm.servergui.api.gui;

import com.minepalm.servergui.api.GuiHelpers;
import com.minepalm.servergui.impl.gui.element.GuiElementInterface;
import net.kyori.adventure.text.Component;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public class AnvilInputGui extends SimpleGui {
    private String inputText;
    private String defaultText;

    /**
     * Constructs a new input gui for the provided player.
     *
     * @param player                the player to serve this gui to
     * @param manipulatePlayerSlots if <code>true</code> the players inventory
     *                              will be treated as slots of this gui
     */
    public AnvilInputGui(Player player, boolean manipulatePlayerSlots) {
        super(MenuType.ANVIL, player, manipulatePlayerSlots);
        this.setDefaultInputValue("");
    }

    /**
     * Sets the default name value for the input (the input stacks name).
     *
     * @param input the default input
     */
    public void setDefaultInputValue(String input) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        itemStack.getItemMeta().displayName(Component.text(input));
        this.inputText = input;
        this.defaultText = input;
        this.setSlot(0, itemStack, ((index, type1, action, gui) -> {
            this.reOpen = true;
            this.inputText = this.defaultText;
            this.sendGui();
        }));
    }

    /**
     * Returns the current inputted string
     *
     * @return the current string
     */
    public String getInput() {
        return this.inputText;
    }

    /**
     * Executes when the input is changed.
     *
     * @param input the new input
     */
    public void onInput(String input) {
    }

    /**
     * Used internally to receive input from the client
     */
    @ApiStatus.Internal
    public void input(String input) {
        this.inputText = input;
        this.onInput(input);
        GuiElementInterface element = this.getSlot(2);
        ItemStack stack = net.minecraft.world.item.ItemStack.EMPTY.asBukkitCopy();
        if (element != null) {
            stack = element.getItemStack();
        }
        GuiHelpers.sendSlotUpdate(player, this.syncId, 2, stack);
    }
}
