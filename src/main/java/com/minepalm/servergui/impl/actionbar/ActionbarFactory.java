package com.minepalm.servergui.impl.actionbar;

import com.google.common.collect.ImmutableSet;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record ActionbarFactory(Function<Player, Component> playerFunc) implements ActionbarProvider {
    private static final List<Player> playerList = new ArrayList<>();

    @Override
    public Component build(Player player) {
        return playerFunc.apply(player);
    }

    public boolean isPlayerRegistered(Player player) {
        return playerList.contains(player);
    }

    public void registerPlayer(Player player) {
        playerList.add(player);
    }

    public void unregisterPlayer(Player player) {
        playerList.remove(player);
    }

    public ImmutableSet<Player> getRegisteredPlayers() {
        return ImmutableSet.copyOf(playerList);
    }
}
