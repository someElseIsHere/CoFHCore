package cofh.core.common.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.function.Supplier;

public class SubCommandRepair {

    public static Supplier<Integer> permissionLevel = () -> 2;

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("repair")
                .requires(source -> source.hasPermission(permissionLevel.get()))
                // Self
                .executes(context -> repairEquipment(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException())))
                // Targets Specified
                .then(Commands.argument(CoFHCommand.CMD_TARGETS, EntityArgument.players())
                        .executes(context -> repairEquipment(context.getSource(), EntityArgument.getPlayers(context, CoFHCommand.CMD_TARGETS))));
    }

    private static int repairEquipment(CommandSourceStack source, Collection<? extends ServerPlayer> targets) {

        int repairedEquipment = 0;

        for (ServerPlayer entity : targets) {
            for (ItemStack stack : entity.getAllSlots()) {
                if (stack.isDamageableItem() && stack.isDamaged()) {
                    stack.setDamageValue(0);
                    ++repairedEquipment;
                }
            }
        }
        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.cofh.repair.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.cofh.repair.success.multiple", targets.size()), true);
        }
        return repairedEquipment;
    }

}
