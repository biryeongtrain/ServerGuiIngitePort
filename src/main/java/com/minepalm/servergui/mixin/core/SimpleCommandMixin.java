package com.minepalm.servergui.mixin.core;

import com.minepalm.servergui.command.ActionbarTest;
import com.minepalm.servergui.command.SexyAnvilCommand;
import com.minepalm.servergui.command.SimpleGUITestCommand;
import com.minepalm.servergui.command.TestCommand;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCommandMap.class)
public abstract class SimpleCommandMixin {
    @Shadow
    public abstract boolean register(String fallbackPrefix, Command command);

    @Inject(method = "setDefaultCommands()V", at = @At("TAIL"), remap = false)
    public void registerOwnCommands(CallbackInfo callback) {
        this.register("actionbar_test", new ActionbarTest("actionbar_test"));
        this.register("anvil_test", new SexyAnvilCommand("anvil_test"));
        this.register("simple_test", new SimpleGUITestCommand("simple_test"));
        this.register("book_test", new TestCommand("book_test"));
    }
}
