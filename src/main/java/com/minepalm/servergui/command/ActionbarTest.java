package com.minepalm.servergui.command;

import com.minepalm.servergui.api.ActionbarRepo;
import com.minepalm.servergui.impl.utils.Container;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionbarTest extends BukkitCommand {

    public ActionbarTest(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return false;

        if (!p.isOp()) return false;

        var repo = Container.get(ActionbarRepo.class);
        repo.add(p, "test_actionbar");

        return true;
    }
}
