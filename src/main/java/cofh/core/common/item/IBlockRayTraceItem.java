package cofh.core.common.item;

import cofh.core.common.network.packet.server.ItemRayTraceBlockPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Item interface that allows you to send arbitrary block raytraces from client to server.
 * Useful for items where client-side raytraces are desired for improved responsiveness.
 */
public interface IBlockRayTraceItem {

    default void sendBlockRayTrace(Player player, InteractionHand hand, Vec3 origin, BlockHitResult result) {

        ItemRayTraceBlockPacket.sendToServer(player, hand, origin, result);
    }

    void handleBlockRayTrace(ServerLevel level, ServerPlayer player, InteractionHand hand, ItemStack stack, Vec3 origin, BlockHitResult result);

}
