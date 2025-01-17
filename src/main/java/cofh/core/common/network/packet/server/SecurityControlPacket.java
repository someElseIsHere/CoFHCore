package cofh.core.common.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.util.control.ISecurableTile;
import cofh.lib.api.control.ISecurable.AccessMode;
import cofh.lib.common.network.packet.IPacketServer;
import cofh.lib.common.network.packet.PacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static cofh.core.common.network.packet.PacketIDs.PACKET_SECURITY_CONTROL;

public class SecurityControlPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected byte mode;

    public SecurityControlPacket() {

        super(PACKET_SECURITY_CONTROL, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        Level world = player.level;
        if (!world.isLoaded(pos)) {
            return;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof ISecurableTile securableTile) {
            securableTile.setAccess(AccessMode.VALUES[mode]);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeByte(mode);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        pos = buf.readBlockPos();
        mode = buf.readByte();
    }

    public static void sendToServer(ISecurableTile tile) {

        SecurityControlPacket packet = new SecurityControlPacket();
        packet.pos = tile.pos();
        packet.mode = (byte) tile.securityControl().getAccess().ordinal();
        packet.sendToServer();
    }

}
