package com.minepalm.servergui.impl.actionbar;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface ActionbarProvider {
    Component build(Player player);
}
