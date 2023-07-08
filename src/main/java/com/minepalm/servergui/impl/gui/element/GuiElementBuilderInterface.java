package com.minepalm.servergui.impl.gui.element;

import com.minepalm.servergui.api.ServerGuiClickType;

import java.util.function.Consumer;

public interface GuiElementBuilderInterface<T extends GuiElementBuilderInterface<T>> {
    /**
     * Set the callback to execute when this element
     * is clicked inside a gui.
     *
     * @param callback the callback
     * @return this element builder
     */
    T setCallback(GuiElementInterface.ClickCallback callback);

    /**
     * Set the callback to execute when this element
     * is clicked inside a gui.
     *
     * @param callback the callback
     * @return this element builder
     */
    default T setCallback(GuiElementInterface.ItemClickCallback callback) {
        return this.setCallback((GuiElementInterface.ClickCallback) callback);
    }

    default T setCallback(Runnable callback) {
        return this.setCallback((a, b, c, d) -> callback.run());
    }

    default T setCallback(Consumer<ServerGuiClickType> callback) {
        return this.setCallback((a, b, c, d) -> callback.accept(b));
    }

    /**
     * Constructs the GuiElement with the values
     * from the builder.
     *
     * @return the built element
     */
    GuiElementInterface build();

}
