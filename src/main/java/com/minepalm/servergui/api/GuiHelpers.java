package com.minepalm.servergui.api;

import com.minepalm.servergui.api.gui.GuiInterface;
import com.minepalm.servergui.impl.gui.virtual.VirtualScreenHandlerInterface;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

public final class GuiHelpers {
    public static final UnaryOperator<Style> STYLE_CLEARER = style -> style.decoration(TextDecoration.ITALIC, style.decoration(TextDecoration.ITALIC)).color(style.color() != null ? style.color() : TextColor.color(NamedTextColor.WHITE));

    @Nullable
    public static GuiInterface getCurrentGui(Player player) {
        return ((CraftPlayer)player).getHandle().containerMenu instanceof VirtualScreenHandlerInterface v ? v.getGui() : null;
    }


    public static void sendSlotUpdate(Player player, int syncId, int slot, ItemStack stack, int revision) {
        ((CraftPlayer)player).getHandle().connection.send(new ClientboundContainerSetSlotPacket(syncId, revision, slot, CraftItemStack.asNMSCopy(stack)));
    }

    public static void sendSlotUpdate(ServerPlayer player, int syncId, int slot, net.minecraft.world.item.ItemStack stack, int revision) {
        player.connection.send(new ClientboundContainerSetSlotPacket(syncId, revision, slot, stack));
    }

    public static void sendSlotUpdate(Player player, int syncId, int slot, ItemStack stack) {
        sendSlotUpdate(player, syncId, slot, stack, 0);
    }

    public static void sendPlayerScreenHandler(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();
        sendPlayerScreenHandler(serverPlayer);
    }

    public static void sendPlayerScreenHandler(ServerPlayer player) {
        player.connection.send(new ClientboundContainerSetContentPacket(player.containerMenu.containerId, player.containerMenu.incrementStateId(), player.containerMenu.getItems(), player.containerMenu.getCarried()));
    }

    public static void sendPlayerInventory(Player player) {
        ServerPlayer serverPlayer = convertNMSPlayer(player);
            serverPlayer.connection.send(new ClientboundContainerSetContentPacket(serverPlayer.inventoryMenu.containerId, serverPlayer.inventoryMenu.incrementStateId(), serverPlayer.inventoryMenu.getItems(), serverPlayer.inventoryMenu.getCarried()));
    }

    public static int posToIndex(int x, int y, int height, int width) {
        return x + y * width;
    }

    public static int getHeight(MenuType<?> type) {
        if (MenuType.GENERIC_9x6.equals(type)) {
            return 6;
        } else if (MenuType.GENERIC_9x5.equals(type) || MenuType.CRAFTING.equals(type)) {
            return 5;
        } else if (MenuType.GENERIC_9x4.equals(type)) {
            return 4;
        } else if (MenuType.GENERIC_9x2.equals(type) || MenuType.ENCHANTMENT.equals(type) || MenuType.STONECUTTER.equals(type)) {
            return 2;
        } else if (MenuType.GENERIC_9x1.equals(type) || MenuType.BEACON.equals(type) || MenuType.HOPPER.equals(type) || MenuType.BREWING_STAND.equals(type)) {
            return 1;
        }

        return 3;
    }

    public static int getWidth(MenuType<?> type) {
        if (MenuType.CRAFTING.equals(type)) {
            return 2;
        } else if (MenuType.GENERIC_3x3.equals(type)) {
            return 3;
        } else if (MenuType.HOPPER.equals(type) || MenuType.BREWING_STAND.equals(type)) {
            return 5;
        } else if (MenuType.ENCHANTMENT.equals(type) || MenuType.STONECUTTER.equals(type) || MenuType.BEACON.equals(type) || MenuType.BLAST_FURNACE.equals(type) || MenuType.FURNACE.equals(type) || MenuType.SMOKER.equals(type) || MenuType.ANVIL.equals(type) || MenuType.SMITHING.equals(type) || MenuType.GRINDSTONE.equals(type) || MenuType.MERCHANT.equals(type) || MenuType.CARTOGRAPHY_TABLE.equals(type) || MenuType.LOOM.equals(type)) {
            return 1;
        }

        return 9;
    }

    private GuiHelpers() {
    }

    private static ServerPlayer convertNMSPlayer(Player player) {
        return ((CraftPlayer)player).getHandle();
    }

    public static Component convertAsMCComponent(net.kyori.adventure.text.Component component) {
        String gsonText = GsonComponentSerializer.gson().serializer().toJson(component);
        return CraftChatMessage.fromJSON(gsonText);
    }

    public static net.kyori.adventure.text.Component convertAsKyoriComponent(MutableComponent mcComponent) {
        String gsonText = CraftChatMessage.toJSON(mcComponent);

        return GsonComponentSerializer.gson().deserialize(gsonText);
    }

    public static ServerPlayer getServerPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
