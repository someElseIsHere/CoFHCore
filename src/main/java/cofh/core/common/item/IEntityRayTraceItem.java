package cofh.core.common.item;

import cofh.core.common.network.packet.server.ItemRayTraceEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Item interface that allows you to send arbitrary entity raytraces from client to server.
 * Useful for items where client-side raytraces are desired for improved responsiveness.
 */
public interface IEntityRayTraceItem {

    default void sendEntityRayTrace(Player player, InteractionHand hand, Vec3 origin, Entity target, Vec3 hit) {

        sendEntityRayTrace(player, hand, origin, target, hit, 0);
    }

    default void sendEntityRayTrace(Player player, InteractionHand hand, Vec3 origin, Entity target, Vec3 hit, float power) {

        ItemRayTraceEntityPacket.sendToServer(player, hand, origin, target, hit, power);
    }

    default void handleEntityRayTrace(ServerLevel level, ServerPlayer player, InteractionHand hand, ItemStack stack, Vec3 origin, Entity target, Vec3 hit) {

    }

    default void handleEntityRayTrace(ServerLevel level, ServerPlayer player, InteractionHand hand, ItemStack stack, Vec3 origin, Entity target, Vec3 hit, float power) {

        handleEntityRayTrace(level, player, hand, stack, origin, target, hit);
    }

}
