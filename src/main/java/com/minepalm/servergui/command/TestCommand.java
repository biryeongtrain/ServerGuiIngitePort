package com.minepalm.servergui.command;

import com.minepalm.servergui.api.gui.BookGui;
import com.minepalm.servergui.api.gui.ScreenProperty;
import com.minepalm.servergui.impl.gui.element.BookElementBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand extends BukkitCommand {


    public TestCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!player.isOp()) return false;

        BookElementBuilder bookBuilder = BookElementBuilder.from(player.getInventory().getItemInMainHand())
                .addPage(Component.text("Test line one!"), Component.text("Test line two!"))
                .addPage(
                        Component.text("Click to navigate to page: "),
                        Component.text("1").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.CHANGE_PAGE, "1")),
                        Component.text("2").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.CHANGE_PAGE, "2")),
                        Component.text("3").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.CHANGE_PAGE, "3")),
                        Component.text("Command").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/test"))
                )
                .addPage(Component.text("This is page three!"))
                .setTitle("test2")
                .setAuthor("test");

        BookGui gui = new BookGui(player, bookBuilder) {
            private boolean forceReopen = true;
            private boolean canCloseByPlayer = false;
            private int tick = 0;

            @Override
            public void onTick() {
                this.tick++;
                if (this.tick % 20 == 0) {
                    this.tick = 0;
                    bookBuilder.getBookMeta().page(this.getIndexPage(), bookBuilder.getPageComponent(this.getIndexPage()).append(Component.text(" " + this.tick)));
                    this.book = bookBuilder.asStack();
                    this.sendProperty(ScreenProperty.SELECTED, this.page);
                }
            }

            @Override
            public boolean onCommand(String command) {
                player.sendMessage(command);
                bookBuilder.addPage(Component.text(command));
                this.book = bookBuilder.asStack();

                this.forceReopen = true;
                return true;
            }

            @Override
            public void onClose() {
                if (this.forceReopen) {
                    this.forceReopen = false;
                    this.forceOpen();
                    return;
                }

                super.onClose();
            }

            @Override
            public boolean canPlayerClose() {
                return canCloseByPlayer;
            }

            @Override
            public void onTakeBookButton() {
                this.canCloseByPlayer = true;
            }
        };
        gui.open();

        return true;
    }
}
