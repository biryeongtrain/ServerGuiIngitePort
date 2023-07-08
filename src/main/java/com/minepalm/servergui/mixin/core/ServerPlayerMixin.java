package com.minepalm.servergui.mixin.core;

import com.minepalm.servergui.impl.PlayerExtensions;
import com.minepalm.servergui.impl.gui.virtual.VirtualScreenHandlerInterface;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements PlayerExtensions {

    @Shadow
    public abstract void doCloseContainer();

    @Unique
    private boolean servergui$ignoreNext = false;

    public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "openMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;nextContainerCounter()I", shift = At.Shift.BEFORE))
    private void servergui$dontForceCloseFor(MenuProvider factory, CallbackInfoReturnable<OptionalInt> cir) {
        if (this.containerMenu != this.inventoryMenu) {
            this.servergui$ignoreNext = true;
            this.closeContainer();
        }
    }

    @Inject(method = "closeContainer", at = @At("HEAD"), cancellable = true)
    private void servergui$ingoreClosing(CallbackInfo ci) {
        if (this.servergui$ignoreNext) {
            this.servergui$ignoreNext = false;
            this.doCloseContainer();
            ci.cancel();
        }
    }

    @Inject(method = "die", at = @At("TAIL"))
    private void servergui$die(CallbackInfo ci) {
        if (this.containerMenu instanceof VirtualScreenHandlerInterface handler) {
            handler.getGui().onClose();
        }
    }

    @Override
    public void sgui$ignoreNextClose() {
        this.servergui$ignoreNext = true;
    }
}
