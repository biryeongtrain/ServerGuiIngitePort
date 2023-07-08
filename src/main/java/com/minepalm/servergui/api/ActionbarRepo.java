package com.minepalm.servergui.api;

import com.google.common.collect.ImmutableSet;
import com.minepalm.servergui.impl.actionbar.ActionbarFactory;
import org.bukkit.entity.Player;

public interface ActionbarRepo {
    ImmutableSet<Player> getHoldingPlayers(String id);
    ImmutableSet<ActionbarFactory> getFactories(Player p);
    ImmutableSet<ActionbarFactory> getFactories();
    void add(Player player, String factoryId);
    void remove(Player player, String factoryId);
    void registerFactory(String id, ActionbarFactory factory);
    void unregisterFactory(String id);

}
