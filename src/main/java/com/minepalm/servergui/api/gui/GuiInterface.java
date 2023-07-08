package com.minepalm.servergui.api.gui;

import net.kyori.adventure.text.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

public interface GuiInterface {

    Component getTitle();
    void setTitle(Component title);
    default void sendProperty(ScreenProperty property, int value) {
        if (!property.validFor(this.getType())) {
            throw new IllegalArgumentException(String.format("The property '%s' is not valid for the handler '%s'", property.name(), BuiltInRegistries.MENU.getId(this.getType())));
        }
        if (this.isOpen()) {
            this.getServerPlayer().connection.send(new ClientboundContainerSetDataPacket(this.getSyncId(), property.id(), value));
        }
    }

    MenuType<?> getType();

    /**
     * Returns the player this gui was constructed for.
     *
     * @return the player
     */
    Player getPlayer();

    ServerPlayer getServerPlayer();

    /**
     * Returns the sync id used for communicating information about this screen between the server and client.
     *
     * @return the sync id or <code>-1</code> if the screen has not been opened
     */
    int getSyncId();

    /**
     * Returns <code>true</code> the screen is currently open on te players screen
     *
     * @return <code>true</code> the screen is open
     */
    boolean isOpen();

    /**
     * Opens the screen for the player.
     *
     * @return <code>true</code> if the screen successfully opened
     * @see GuiInterface#onOpen()
     */
    boolean open();

    boolean forceOpen();

    boolean getAutoUpdate();

    void setAutoUpdate(boolean value);

    /**
     * Used internally for closing the gui.
     *
     * @param alreadyClosed Is set to true, if gui's ScreenHandler is already closed
     * @see GuiInterface#onClose()
     */
    @ApiStatus.Internal
    void close(boolean alreadyClosed);

    /**
     * Closes the current gui
     *
     * @see GuiInterface#onClose()
     */
    default void close() {
        this.close(false);
    }

    /**
     * Executes when the screen is opened
     */
    default void beforeOpen() {
    }

    /**
     * Executes when the screen is opened
     */
    default void afterOpen() {
    }

    /**
     * Executes when the screen is opened
     */
    default void onOpen() {
    }

    /**
     * Executes when the screen is closed
     */
    default void onClose() {
    }

    /**
     * Executes each tick while the screen is open
     */
    default void onTick() {
    }

    default boolean canPlayerClose() {
        return true;
    }
    void setPlayerClose(boolean canClose);

    default void handleException(Throwable throwable) {
        throwable.printStackTrace();
    }


}
