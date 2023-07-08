package com.minepalm.servergui.mixin.core;

import com.minepalm.servergui.api.GuiHelpers;
import com.minepalm.servergui.api.ServerGuiClickType;
import com.minepalm.servergui.api.gui.AnvilInputGui;
import com.minepalm.servergui.api.gui.SimpleGui;
import com.minepalm.servergui.impl.gui.virtual.VirtualScreenHandlerInterface;
import com.minepalm.servergui.impl.gui.virtual.book.BookScreenHandler;
import com.minepalm.servergui.impl.gui.virtual.inventory.VirtualScreenHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Unique
    private AbstractContainerMenu serverGui$previousScreen = null;

    @Shadow
    public ServerPlayer player;

    @Shadow
    public abstract void send(Packet<?> packet);

    @Inject(method = "handleContainerClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V", shift = At.Shift.AFTER), cancellable = true)
    private void serverGui$handleGuiClicks(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof VirtualScreenHandler handler) {
            try {
                var gui = handler.getGui();

                int slot = packet.getSlotNum();
                int button = packet.getButtonNum();
                ServerGuiClickType clickType = ServerGuiClickType.toClickType(packet.getClickType(), button, slot);
                boolean ignore = gui.onAnyClick(slot, clickType, packet.getClickType());

                if (ignore && !handler.getGui().getLockPlayerInventory() && (slot >= handler.getGui().getSize() || slot < 0 || handler.getGui().getSlotRedirect(slot) != null)) {
                    if (clickType == ServerGuiClickType.MOUSE_DOUBLE_CLICK || (clickType.isDragging && clickType.value == 2)) {
                        GuiHelpers.sendPlayerScreenHandler(this.player);
                    }

                    return;
                }

                boolean allow = gui.click(slot, clickType, packet.getClickType());
                if (handler.getGui().isOpen()) {
                    if (!allow) {
                        if (slot >= 0 && slot < handler.getGui().getSize()) {
                            this.send(new ClientboundContainerSetSlotPacket(handler.containerId, handler.incrementStateId(), slot, handler.getSlot(slot).getItem()));
                        }
                        GuiHelpers.sendSlotUpdate(this.player, -1, -1, this.player.containerMenu.getCarried(), handler.incrementStateId());

                        if (clickType.numKey) {
                            int x = clickType.value + handler.slots.size() - 10;
                            GuiHelpers.sendSlotUpdate(player, handler.containerId, x, handler.getSlot(x).getItem(), handler.incrementStateId());
                        } else if (clickType == ServerGuiClickType.MOUSE_DOUBLE_CLICK || clickType == ServerGuiClickType.MOUSE_LEFT_SHIFT || clickType == ServerGuiClickType.MOUSE_RIGHT_SHIFT || (clickType.isDragging && clickType.value == 2)) {
                            GuiHelpers.sendPlayerScreenHandler(this.player);
                        }
                    }
                }
            } catch (Throwable e) {
                handler.getGui().handleException(e);
                ci.cancel();
            }
            ci.cancel();
        } else if (this.player.containerMenu instanceof BookScreenHandler) {
            ci.cancel();

        }
    }

    @Inject(method = "handleContainerClick", at = @At("TAIL"))
    private void serverGui$resyncGui(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof VirtualScreenHandler handler) {
            try {
                int slot = packet.getSlotNum();
                int button = packet.getButtonNum();
                ServerGuiClickType type = ServerGuiClickType.toClickType(packet.getClickType(), button, slot);

                if (type == ServerGuiClickType.MOUSE_DOUBLE_CLICK || (type.isDragging && type.value == 2) || type.shift) {
                    GuiHelpers.sendPlayerScreenHandler(this.player);
                }

            } catch (Throwable e) {
                handler.getGui().handleException(e);
            }
        }
    }

    @Inject(method = "handleContainerClose(Lnet/minecraft/network/protocol/game/ServerboundContainerClosePacket;Lorg/bukkit/event/inventory/InventoryCloseEvent$Reason;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER), cancellable = true)
    private void serverGui$storeScreenHandler(ServerboundContainerClosePacket packet,InventoryCloseEvent.Reason reason, CallbackInfo info) {
        player.sendSystemMessage(Component.literal("close screen"));
        if (this.player.containerMenu instanceof VirtualScreenHandler handler) {
            if (handler.getGui().canPlayerClose()) {
                this.serverGui$previousScreen = this.player.containerMenu;
                this.player.sendSystemMessage(Component.literal("previous Screen sets ").append( serverGui$previousScreen.getTitle()));
            } else {
                var screenHandler = this.player.containerMenu;
                if (screenHandler.getType() != null) {
                    try {
                        this.send(new ClientboundOpenScreenPacket(screenHandler.containerId, screenHandler.getType(), GuiHelpers.convertAsMCComponent(handler.getGui().getTitle())));
                    } catch (Throwable e) {
                        handler.getGui().handleException(e);
                    }
                }
                info.cancel();
            }
        }
    }

    @Inject(method = "handleContainerClose(Lnet/minecraft/network/protocol/game/ServerboundContainerClosePacket;Lorg/bukkit/event/inventory/InventoryCloseEvent$Reason;)V", at = @At("TAIL"))
    private void serverGui$executeClosing(ServerboundContainerClosePacket packet, InventoryCloseEvent.Reason reason, CallbackInfo info) {
        this.player.sendSystemMessage(Component.literal("Closing GUI..."));
        this.player.sendSystemMessage(Component.literal(reason.name()));
        try {
            if (this.serverGui$previousScreen != null) {
                if (this.serverGui$previousScreen instanceof VirtualScreenHandlerInterface screenHandler) {
                    screenHandler.getGui().close(true);
                }
            }
        } catch (Throwable e) {
            if (this.serverGui$previousScreen instanceof VirtualScreenHandlerInterface screenHandler) {
                screenHandler.getGui().handleException(e);
            } else {
                e.printStackTrace();
            }
        }
        this.serverGui$previousScreen = null;
    }

    @Inject(method = "handleRenameItem", at = @At("TAIL"))
    private void serverGui$catchRenamingWithCustomGui(ServerboundRenameItemPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof VirtualScreenHandler handler) {
            try {
                if (handler.getGui() instanceof AnvilInputGui) {
                    ((AnvilInputGui) handler.getGui()).input(packet.getName());
                }
            } catch (Throwable e) {
                handler.getGui().handleException(e);
            }
        }
    }

    @Inject(method = "handlePlaceRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V", shift = At.Shift.BEFORE), cancellable = true)
    private void serverGui$catchRecipeRequests(ServerboundPlaceRecipePacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof VirtualScreenHandler handler && handler.getGui() instanceof SimpleGui gui) {
            try {
                gui.onCraftRequest(packet.getRecipe(), packet.isShiftDown());
            } catch (Throwable e) {
                handler.getGui().handleException(e);
            }
        }
    }
}
