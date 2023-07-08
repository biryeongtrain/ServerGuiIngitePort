package com.minepalm.servergui.api.gui;

import com.minepalm.servergui.api.GuiHelpers;
import com.minepalm.servergui.impl.gui.FakeScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.data.type.Sign;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Sign Gui Implementation
 * <p>
 * The Vanilla sign GUI does not use a {@link net.minecraft.world.inventory.AbstractContainerMenu} and thus
 * it gets its data directly from a block in the world. Due to this, before opening the
 * screen the server must send a fake 'ghost' sign block to the player which contains the data
 * we want the sign to show. We send the block at the players location at max world height
 * so it hopefully goes unnoticed. The fake block is removed when the GUI is closed.
 * This also means in order to refresh the data on the sign, we must close and re-open the GUI,
 * as only handled screens have property support.
 * On the server side however, this sign GUI uses a custom {@link FakeScreenHandler} so the server
 * can manage and trigger methods like onTIck, onClose, ect.
 * <p>
 * SignGui has lots of deprecated methods which have no function, mainly due to the lack of
 * item slots and a client ScreenHandler.
 */
public class SignGui implements GuiInterface {

    protected final SignBlockEntity signEntity;
    protected BlockState type = Blocks.OAK_SIGN.defaultBlockState();
    protected boolean autoUpdate = true;

    protected List<Integer> sendLineUpdate = new ArrayList<>(4);
    protected final ServerPlayer player;
    protected boolean open = false;
    protected boolean reOpen = false;
    protected FakeScreenHandler screenHandler;
    private boolean isCloseExecuted = false;

    /**
     * Constructs a new SignGui for the provided player
     *
     * @param player the player to serve this gui to
     */
    public SignGui(Player player)  {

        this.player = ((CraftPlayer)player).getHandle();
        this.signEntity = new SignBlockEntity(new BlockPos(this.player.blockPosition().getX(), this.player.level.getHeight() - 1, this.player.blockPosition().getZ()), Blocks.OAK_SIGN.defaultBlockState());
    }

    /**
     * Sets a line of {@link Component} on the sign.
     *
     * @param line the line index, from 0
     * @param text the Text for the line, note that all formatting is stripped when the player closes the sign
     */
    public void setLine(int line, net.kyori.adventure.text.Component text) {
        var modifiedComponent = GuiHelpers.convertAsMCComponent(text);
        this.signEntity.setMessage(line, modifiedComponent);
        this.sendLineUpdate.add(line);

        if (this.open & this.autoUpdate) {
            this.updateSign();
        }
    }

    /**
     * Returns the {@link Component} from a line on the sign.
     *
     * @param line the line number
     * @return the text on the line
     */
    public net.kyori.adventure.text.Component getLine(int line) {
        return GuiHelpers.convertAsKyoriComponent((MutableComponent) this.signEntity.getMessage(line, false));
    }

    /**
     * Sets default color for the sign text.
     *
     * @param color the default sign color
     */
    public void setColor(DyeColor color) {
        this.signEntity.setColor(net.minecraft.world.item.DyeColor.valueOf(color.name()));

        if (this.open && this.autoUpdate) {
            this.updateSign();
        }
    }

    public void setColor(net.minecraft.world.item.DyeColor color) {
        this.signEntity.setColor(color);

        if (this.open && this.autoUpdate) {
            this.updateSign();
        }
    }
    /**
     * Sets the block model used for the sign background.
     *
     * @param type a block in the {@link net.minecraft.tags.BlockTags#SIGNS} tag
     */
    public void setSignType(Material type) {

        if (type.createBlockData() instanceof Sign) {
            throw new IllegalArgumentException("The type must be a sign");
        }

        this.type = CraftMagicNumbers.getBlock(type).defaultBlockState();

        if (this.open && this.autoUpdate) {
            this.updateSign();
        }
    }

    /**
     * Sends sign updates to the player. <br>
     * This requires closing and reopening the gui, causing a flicker.
     */
    public void updateSign() {
        if (this.player.containerMenu == this.screenHandler) {
            this.reOpen = true;
            this.player.connection.send(new ClientboundContainerClosePacket(this.screenHandler.containerId));
        } else {
            this.open();
        }
    }

    @Override
    public Player getPlayer() {
        return this.player.getBukkitEntity();
    }

    @Override
    public ServerPlayer getServerPlayer() {
        return this.player;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public boolean open() {
        this.reOpen = true;

        if (this.player.containerMenu != this.player.inventoryMenu && this.player.containerMenu != this.screenHandler) {
            this.player.closeContainer();
        }
        if (screenHandler == null) {
            this.screenHandler = new FakeScreenHandler(this);
        }
        this.player.containerMenu = this.screenHandler;

        this.player.connection.send(new ClientboundBlockUpdatePacket(this.signEntity.getBlockPos(), this.type));
        this.player.connection.send(this.signEntity.getUpdatePacket());
        this.player.connection.send(new ClientboundOpenSignEditorPacket(this.signEntity.getBlockPos()));

        this.reOpen = false;
        this.open = true;

        return true;
    }

    @Override
    public boolean forceOpen() {
        return false;
    }

    @Override
    public void close(boolean alreadyClosed) {
        if (this.isCloseExecuted) return;
        if (this.open && !this.reOpen) {
            this.open = false;
            this.reOpen = false;
            this.isCloseExecuted = true;
            this.player.connection.send(new ClientboundBlockUpdatePacket(player.level, signEntity.getBlockPos()));

            if (alreadyClosed && this.player.containerMenu == this.screenHandler) {
                this.player.doCloseContainer();
            } else {
                this.player.closeContainer();
            }

            this.onClose();
        } else {
            this.reOpen = false;
            this.open();
        }
    }

    @Override
    public void setPlayerClose(boolean canClose) {
        this.isCloseExecuted = canClose;
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
     * Used internally to receive input from the client
     */
    @ApiStatus.Internal
    public void setLineInternal(int line, net.kyori.adventure.text.Component text) {
        if (this.reOpen && this.sendLineUpdate.contains(line)) {
            this.sendLineUpdate.remove((Integer) line);
        } else {
            this.signEntity.setMessage(line, GuiHelpers.convertAsMCComponent(text));
        }
    }

    @Deprecated
    @Override
    public void setTitle(net.kyori.adventure.text.Component title) {
    }

    @Deprecated
    @Override
    public net.kyori.adventure.text.Component getTitle() {
        return null;
    }

    @Deprecated
    @Override
    public MenuType<?> getType() {
        return null;
    }

    @Deprecated
    @Override
    public int getSyncId() {
        return -1;
    }
}

