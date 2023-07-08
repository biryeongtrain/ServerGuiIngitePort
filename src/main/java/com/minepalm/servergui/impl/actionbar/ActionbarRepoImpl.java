package com.minepalm.servergui.impl.actionbar;

import com.google.common.collect.ImmutableSet;
import com.minepalm.servergui.api.ActionbarRepo;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ActionbarRepoImpl implements ActionbarRepo {
    private ReentrantLock lock = new ReentrantLock();
    private final Map<String, ActionbarFactory> factoryMap = new ConcurrentHashMap<>();

    @Override
    public ImmutableSet<Player> getHoldingPlayers(String id) {
        return this.factoryMap.get(id).getRegisteredPlayers();
    }

    @Override
    public ImmutableSet<ActionbarFactory> getFactories(Player p) {
        Set<ActionbarFactory> set = new HashSet<>();
        for (ActionbarFactory factory : factoryMap.values()) {
            if (factory.isPlayerRegistered(p)) {
                set.add(factory);
            }
        }
        return ImmutableSet.copyOf(set);
    }

    @Override
    public ImmutableSet<ActionbarFactory> getFactories() {
        return ImmutableSet.copyOf(factoryMap.values());
    }

    @Override
    public void add(Player player, String factoryId) {
        this.factoryMap.get(factoryId).registerPlayer(player);
    }

    @Override
    public void remove(Player player, String factoryId) {
        this.factoryMap.get(factoryId).unregisterPlayer(player);
    }

    @Override
    public void registerFactory(String id, ActionbarFactory factory) {
        lock.lock();
        try {
            factoryMap.put(id, factory);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unregisterFactory(String id) {
        lock.lock();
        try {
            factoryMap.remove(id);
        } finally {
            lock.unlock();
        }
    }
}
