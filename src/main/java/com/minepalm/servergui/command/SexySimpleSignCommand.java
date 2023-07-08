package com.minepalm.servergui.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SexySimpleSignCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!player.isOp()) return false;

        return false;
//        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
//
//        SignGui gui = new SignGui(player) {
//            private int tick = 0;
//
//            {
//                this.setSignType(Material.ACACIA_WALL_SIGN);
//                this.setColor(DyeColor.WHITE);
//                this.setLine(1, Component.text("^"));
//                this.setLine(2, Component.text("Input your"));
//                this.setLine(3, Component.text("value here"));
//                this.setAutoUpdate(false);
//            }
//
//            @Override
//            public void onClose() {
//                getPlayer().sendMessage(Component.text("Input was: " + this.getLine(0)));
//            }
//
//            @Override
//            public void onTick() {
//                tick++;
//                if (tick % 30 == 0) {
//                    this.setLine(1, this.getLine(1).append(Component.text( "^")));
//                    this.setSignType(CraftMagicNumbers.getMaterial(BuiltInRegistries.BLOCK.getTag(BlockTags.WALL_SIGNS).get().getRandomElement(RandomSource.create()).get().value()));
//                    this.setColor(net.minecraft.world.item.DyeColor.byId(RandomSource.create().nextInt(15)));
//                    this.updateSign();
//                    this.tick = 0;
//                }
//            }
//        };
//        gui.open();
//
//        return true;
    }
}
