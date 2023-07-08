package com.minepalm.servergui.api.gui;


import com.minepalm.servergui.impl.gui.element.BookElementBuilder;
import com.minepalm.servergui.impl.gui.virtual.SguiScreenHandlerFactory;
import com.minepalm.servergui.impl.gui.virtual.book.BookScreenHandler;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.OptionalInt;

public class BookGui implements GuiInterface {
    protected final Player player;
    protected ItemStack book;
    protected int page = 0;
    @Deprecated(forRemoval = true)
    protected boolean open = false;
    protected boolean reOpen = false;
    protected BookScreenHandler screenHandler = null;
    protected int syncId = -1;
    private boolean isCloseExecuted = false;
    private boolean canCloseByPlayer = true;

    public BookGui(Player player, ItemStack book) {
        this.player = player;

        if (!(book.getType() == Material.WRITABLE_BOOK || book.getType() == Material.WRITTEN_BOOK)) {
            throw new IllegalArgumentException("Item must be a type of book");
        }
        this.book = book;
    }

    public BookGui(Player player, BookElementBuilder book) {
        this.player = player;
        this.book = book.asStack();
    }

    /**
     * Sets the selected page number
     *
     * @param page the page index, from 0
     */
    public void setPage(int page) {
        this.page = Math.min(page, ((BookMeta)this.book.getItemMeta()).getPageCount() - 1);
        this.sendProperty(ScreenProperty.SELECTED, this.page);
    }

    /**
     * Returns the current selected page
     *
     * @return the page index, from 0
     */
    public int getPage() {
        return page;
    }

    public int getIndexPage() {
        return page + 1;
    }

    /**
     * Returns the book item used to store the data.
     *
     * @return the book stack
     */
    public ItemStack getBook() {
        return this.book;
    }

    /**
     * Activates when the 'Take Book' button is pressed
     */
    public void onTakeBookButton() {
    }

    public void onNextPageButton() {
        setPage(getPage() + 1);
    }

    public void onPreviousPageButton() {
        setPage(getPage() - 1);
    }

    @Override
    public MenuType<?> getType() {
        return MenuType.LECTERN;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public ServerPlayer getServerPlayer() {
        return ((CraftPlayer) player).getHandle();
    }

    @Override
    public int getSyncId() {
        return this.syncId;
    }

    @Override
    public boolean isOpen() {
        return this.screenHandler == this.getServerPlayer().containerMenu;
    }

    @Override
    public boolean open() {
        var state = false;
        if (!this.getServerPlayer().hasDisconnected() && !this.isOpen()) {
            this.beforeOpen();
            state = this.setupScreenHandler();
            this.afterOpen();
        }
        return state;
    }

    public boolean forceOpen() {
        var state = false;
        OptionalInt temp = this.getServerPlayer().openMenu(new SguiScreenHandlerFactory<>(this, (syncId, inv, player) -> new BookScreenHandler(syncId, this, ((Player) player.getBukkitEntity()))));
        if (temp.isPresent()) {
            this.syncId = temp.getAsInt();
            if (this.getServerPlayer().containerMenu instanceof BookScreenHandler) {
                this.screenHandler = (BookScreenHandler) this.getServerPlayer().containerMenu;
                this.sendProperty(ScreenProperty.SELECTED, this.page);
                return true;
            }
        }
        return state;
    }
    protected boolean setupScreenHandler() {
        //noinspection removal
        this.open = true;
        this.onOpen();
        this.reOpen = true;
        OptionalInt temp = this.getServerPlayer().openMenu(new SguiScreenHandlerFactory<>(this, (syncId, inv, player) -> new BookScreenHandler(syncId, this, ((Player) player.getBukkitEntity()))));
        this.reOpen = false;
        if (temp.isPresent()) {
            this.syncId = temp.getAsInt();
            if (this.getServerPlayer().containerMenu instanceof BookScreenHandler) {
                this.screenHandler = (BookScreenHandler) this.getServerPlayer().containerMenu;
                this.sendProperty(ScreenProperty.SELECTED, this.page);
                return true;
            }
        }
        return false;
    }

    public boolean onCommand(String command) {
        return false;
    }

    @Override
    public void close(boolean screenHandlerIsClosed) {
        if (this.isOpen() && !this.reOpen) {
            if (this.isCloseExecuted) {
                return;
            }
            this.isCloseExecuted = true;
            //noinspection removal
            this.open = this.isOpen();
            this.reOpen = false;

//            if (!screenHandlerIsClosed && this.getServerPlayer().containerMenu == this.screenHandler) {
//                this.getServerPlayer().closeContainer();
//            }

            this.onClose();
        } else {
            this.reOpen = false;
        }
    }

    @Override
    public void setPlayerClose(boolean canClose) {
        this.canCloseByPlayer = canClose;
    }

    @Override
    public boolean canPlayerClose() {
        return this.canCloseByPlayer;
    }

    @Deprecated
    @Override
    public Component getTitle() {
        return null;
    }
    @Deprecated
    @Override
    public void setTitle(Component title) {

    }

    @Deprecated
    @Override
    public boolean getAutoUpdate() {
        return false;
    }
    @Deprecated
    @Override
    public void setAutoUpdate(boolean value) {

    }
}
