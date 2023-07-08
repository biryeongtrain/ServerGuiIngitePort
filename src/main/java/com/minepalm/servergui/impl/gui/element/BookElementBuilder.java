package com.minepalm.servergui.impl.gui.element;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

public class BookElementBuilder extends GuiElementBuilder {

    /**
     * Constructs a new BookElementBuilder with the default settings.
     */
    public BookElementBuilder() {
        super(Material.WRITTEN_BOOK);
    }

    /**
     * Constructs a new BookElementBuilder with the supplied number
     * of items.
     *
     * @param count the number of items in the element
     */
    public BookElementBuilder(int count) {
        super(Material.WRITTEN_BOOK, count);
    }

    /**
     * Adds a new page to the book. <br>
     * Note that only signed books support formatting
     *
     * @param lines an array of lines, they will also wrap automatically to fit to the screen
     * @return this book builder
     * @see BookElementBuilder#setPage(int, Component...)
     */
    public BookElementBuilder addPage(Component... lines) {
        var text = Component.text("");
        for (Component line : lines) {
            text = text.append(line).append(Component.text("\n"));
        }
        this.getBookMeta().addPages(text);
        return this;
    }

    /**
     * Sets a page of the book. <br>
     * Note that only signed books support formatting
     *
     * @param index the page index, from 0
     * @param lines an array of lines, they will also wrap automatically to fit to the screen
     * @return this book builder
     * @throws IndexOutOfBoundsException if the page has not been created
     * @see BookElementBuilder#addPage(Component...)
     */
    public BookElementBuilder setPage(int index, Component... lines) {
        var text = Component.text("");
        for (Component line : lines) {
            text = text.append(line).append(Component.text("\n"));
        }
        this.getBookMeta().page(index, text);
        return this;
    }

    /**
     * Sets the author of the book, also marks
     * the book as signed.
     *
     * @param author the authors name
     * @return this book builder
     */
    public BookElementBuilder setAuthor(String author) {
        this.getBookMeta().setAuthor(author);
        this.signed();
        return this;
    }

    /**
     * Sets the title of the book, also marks
     * the book as signed.
     *
     * @param title the book title
     * @return this book builder
     */
    public BookElementBuilder setTitle(String title) {
        this.getBookMeta().setTitle(title);
        this.signed();
        return this;
    }

    /**
     * Sets the book to be signed, not necessary
     * if already using setTitle or setAuthor.
     *
     * @return this book builder
     * @see BookElementBuilder#unSigned()
     */
    public BookElementBuilder signed() {
        this.item = Material.WRITTEN_BOOK;
        return this;
    }

    /**
     * Sets the book to not be signed, this will
     * also remove the title and author on
     * stack creation.
     *
     * @return this book builder
     * @see BookElementBuilder#signed()
     */
    public BookElementBuilder unSigned() {
        this.setItem(Material.WRITABLE_BOOK);
        return this;
    }

    protected List<Component> getOrCreatePages() {
        return this.getBookMeta().pages() != null ? this.getBookMeta().pages() : new ArrayList<>();
    }

    @Override
    public GuiElementBuilder setItem(Material item) {
        if (!(item == Material.WRITABLE_BOOK || item == Material.WRITTEN_BOOK)) {
            throw new IllegalArgumentException("Item must be a type of book");
        }

        return super.setItem(item);
    }

    /**
     * Only written books may have formatting, thus if the book is not marked as signed,
     * we must strip the formatting. To sign a book use the {@link BookElementBuilder#setTitle(String)}
     * or {@link BookElementBuilder#setAuthor(String)} methods.
     *
     * @return the book as a stack
     */
    @Override
    public ItemStack asStack() {
        var itemStack = new ItemStack(item, 1);
        itemStack.setItemMeta(tag);
        return itemStack;
    }

    public Component getPageComponent(int page) {
        return this.getBookMeta().page(page);
    }
    /**
     * Constructs BookElementBuilder based on the supplied book.
     * Useful for making changes to existing books.
     * <br>
     * The method will check for the existence of a 'title'
     * and 'author' tag, if either is found it will assume
     * the book has been signed. This can be undone
     * with the {@link BookElementBuilder#unSigned()}.
     *
     *
     * @param book the target book stack
     * @return the builder
     * @throws IllegalArgumentException if the stack is not a book
     */
    public static BookElementBuilder from(ItemStack book) {
        if (!(book.getType() == Material.WRITABLE_BOOK || book.getType() == Material.WRITTEN_BOOK)) {
            throw new IllegalArgumentException("Item must be a type of book");
        }

        BookElementBuilder builder = new BookElementBuilder(book.getAmount());

        builder.setItem(book.getType());
        tag = book.getItemMeta();

        return builder;
    }

    /**
     * Returns the contents of the specified page.
     *
     * @param book  the book to get the page from
     * @param index the page index, from 0
     * @return the contents of the page or empty if page does not exist
     * @throws IllegalArgumentException if the item is not a book
     */
    public static Component getPageContents(ItemStack book, int index) {
        if (!(book.getType() == Material.WRITABLE_BOOK || book.getType() == Material.WRITTEN_BOOK)) {
            throw new IllegalArgumentException("Item must be a type of book");
        }
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        if (bookMeta.getPageCount() >= index) {
            return bookMeta.page(index);
        }

        return Component.text("");
    }

    /**
     * Returns the contents of the specified page.
     *
     * @param book  the book element builder to get the page from
     * @param index the page index, from 0
     * @return the contents of the page or empty if page does not exist
     */
    public static Component getPageContents(BookElementBuilder book, int index) {
        List<Component> pages = book.getOrCreatePages();
        if(index < pages.size()) {
            return pages.get(index);
        }
        return Component.text("");
    }

    public BookMeta getBookMeta() {
        return (BookMeta) this.getOrCreateNbt();
    }
}
