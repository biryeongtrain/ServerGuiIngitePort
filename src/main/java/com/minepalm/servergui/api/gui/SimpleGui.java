package com.minepalm.servergui.api.gui;

import com.minepalm.servergui.api.GuiHelpers;
import com.minepalm.servergui.impl.gui.element.GuiElement;
import com.minepalm.servergui.impl.gui.element.GuiElementInterface;
import com.minepalm.servergui.impl.gui.virtual.SguiScreenHandlerFactory;
import com.minepalm.servergui.impl.gui.virtual.inventory.VirtualScreenHandler;
import com.minepalm.servergui.impl.gui.virtual.inventory.VirtualSlot;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.OptionalInt;

public class SimpleGui extends BaseSlotGui {
    protected final int width;
    protected final int height;

    protected final MenuType<?> type;
    private final boolean includePlayer;
    private final int sizeCont;
    protected boolean lockPlayerInventory = false;
    protected VirtualScreenHandler screenHandler = null;
    protected int syncId = -1;
    protected boolean hasRedirects = false;
    private Component title = null;
    private ServerPlayer serverPlayer;
    private boolean isCloseCommandExecuted = false;

    private boolean canPlayerCloseGui = true;
    /**
     * Constructs a new simple container gui for the supplied player.
     *
     * @param type                  the screen handler that the client should display
     * @param player                the player to server this gui to
     * @param manipulatePlayerSlots if <code>true</code> the players inventory
     *                              will be treated as slots of this gui
     */
    public SimpleGui(MenuType<?> type, Player player, boolean manipulatePlayerSlots) {
        super(player, GuiHelpers.getHeight(type) * GuiHelpers.getWidth(type) + (manipulatePlayerSlots ? 36 : 0));
        this.height = GuiHelpers.getHeight(type);
        this.width = GuiHelpers.getWidth(type);
        this.type = type;
        this.sizeCont = this.width * this.height;
        this.includePlayer = manipulatePlayerSlots;
        this.serverPlayer = ((CraftPlayer) player).getHandle();
    }

    /**
     * Returns the number of vertical slots in this gui.
     *
     * @return the height of this gui
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Returns the number of horizontal slots in this gui.
     *
     * @return the width of this gui
     */
    public int getWidth() {
        return this.width;
    }

    @Override
    public void setSlot(int index, GuiElementInterface element) {
        super.setSlot(index, element);
        if (this.isOpen() && this.autoUpdate) {
            this.screenHandler.setSlot(index, new VirtualSlot(this.screenHandler.inventory, index, 0, 0));
        }
    }

    @Override
    public void setSlotRedirect(int index, Slot slot) {
        super.setSlotRedirect(index, slot);
        if (this.isOpen() && this.autoUpdate) {
            this.screenHandler.setSlot(index, slot);
        }
    }

    @Override
    public void clearSlot(int index) {
        super.clearSlot(index);
        this.hasRedirects = true;
        if (this.isOpen() && this.autoUpdate) {
            this.screenHandler.setSlot(index, new VirtualSlot(this.screenHandler.inventory, index, 0, 0));
        }
    }

    @Override
    public boolean isOpen() {
        return this.screenHandler != null && this.screenHandler == ((CraftPlayer)this.player).getHandle().containerMenu;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(Component title) {
        this.title = title;

        if (this.isOpen()) {
            ((CraftPlayer) this.player).getHandle().connection.send(new ClientboundOpenScreenPacket(this.syncId, this.type, GuiHelpers.convertAsMCComponent(title)));
            this.screenHandler.sendAllDataToRemote();
        }
    }

    @Override
    public boolean getAutoUpdate() {
        return this.autoUpdate;
    }

    @Override
    public void setAutoUpdate(boolean value) {
        this.autoUpdate = value;
    }

    /**
     * Returns if the gui includes the player inventory slots.
     *
     * @return <code>true</code> if the player inventory slots.
     */
    public boolean isIncludingPlayer() {
        return this.includePlayer;
    }

    /**
     * Returns the number of slots in the virtual inventory only.
     * Works the same as {@link SimpleGui#getSize()}, however excludes player gui slots if <code>includePlayer</code> is <code>true</code>.
     *
     * @return the size of the virtual inventory
     * @see SimpleGui#getSize()
     */
    public int getVirtualSize() {
        return this.sizeCont;
    }


    /**
     * Returns if this gui has slot redirects.
     *
     * @return <code>true</code> if this gui has slot redirects
     * @see SimpleGui#getSlotRedirect(int)
     */
    public boolean isRedirectingSlots() {
        return this.hasRedirects;
    }

    /**
     * Sends the gui to the player
     *
     * @return <code>true</code> if successful
     */
    protected boolean sendGui() {
        this.reOpen = true;
        OptionalInt temp = this.serverPlayer.openMenu(SguiScreenHandlerFactory.ofDefault(this));
        this.reOpen = false;
        if (temp.isPresent()) {
            this.syncId = temp.getAsInt();
            if (this.serverPlayer.containerMenu instanceof VirtualScreenHandler) {
                this.screenHandler = (VirtualScreenHandler) this.serverPlayer.containerMenu;
                return true;
            }
        }
        return false;
    }

    /**
     * Executes after player clicks any recipe from recipe book.
     *
     * @param recipe the selected recipe identifier
     * @param shift  is shift was held
     */
    public void onCraftRequest(ResourceLocation recipe, boolean shift) {
    }

    @Override
    public MenuType<?> getType() {
        return this.type;
    }

    @Override
    public ServerPlayer getServerPlayer() {
        return this.serverPlayer;
    }

    @Override
    public boolean open() {
        if (this.serverPlayer.hasDisconnected() || this.isOpen()) {
            return false;
        } else {
            this.beforeOpen();
            this.onOpen();
            this.sendGui();
            //noinspection removal
            this.open = this.isOpen();
            this.afterOpen();
            return this.isOpen();
        }
    }

    @Override
    public boolean forceOpen() {
        return sendGui();
    }

    @Override
    public void close(boolean screenHandlerIsClosed) {
        if ((this.isOpen() || screenHandlerIsClosed) && !this.reOpen) {
            if (isCloseCommandExecuted) return;
            this.isCloseCommandExecuted = true;
            this.reOpen = false;

            if (!screenHandlerIsClosed && this.serverPlayer.containerMenu == this.screenHandler) {
                this.serverPlayer.closeContainer();
                this.screenHandler = null;
            }

            this.serverPlayer.containerMenu.sendAllDataToRemote();

            this.onClose();
        } else {
            this.reOpen = false;
        }
    }

    @Override
    public void setPlayerClose(boolean canClose) {
        this.canPlayerCloseGui = canClose;
    }

    @Override
    public boolean canPlayerClose() {
        return this.canPlayerCloseGui;
    }

    @Override
    public boolean getLockPlayerInventory() {
        return this.lockPlayerInventory || this.includePlayer;
    }

    @Override
    public void setLockPlayerInventory(boolean value) {
        this.lockPlayerInventory = value;
    }

    @Override
    public int getSyncId() {
        return syncId;
    }

    /**
     * Allows to send some additional properties to guis
     * <p>
     * See values at https://wiki.vg/Protocol#Window_Property as reference
     *
     * @param property the property id
     * @param value    the value of the property to send
     * @deprecated As of 0.4.0, replaced by {@link GuiInterface#sendProperty} as its much more readable
     */
    @Deprecated
    public void sendProperty(int property, int value) {
        this.serverPlayer.connection.send(new ClientboundContainerSetDataPacket(this.syncId, property, value));
    }

    @Deprecated
    public void setSlot(int index, ItemStack itemStack, GuiElementInterface.ItemClickCallback callback) {
        this.setSlot(index, new GuiElement(itemStack, callback));
    }

    @Deprecated
    public void addSlot(ItemStack itemStack, GuiElementInterface.ItemClickCallback callback) {
        this.addSlot(new GuiElement(itemStack, callback));
    }
}
