package cofh.core.common.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.common.network.packet.PacketIDs;
import cofh.core.util.control.ITransferControllableTile;
import cofh.lib.common.network.packet.IPacketServer;
import cofh.lib.common.network.packet.PacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TransferControlPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected boolean transferIn;
    protected boolean transferOut;

    public TransferControlPacket() {

        super(PacketIDs.PACKET_TRANSFER_CONTROL, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        Level world = player.level;
        if (!world.isLoaded(pos)) {
            return;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof ITransferControllableTile transferControllableTile) {
            transferControllableTile.setControl(transferIn, transferOut);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeBoolean(transferIn);
        buf.writeBoolean(transferOut);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        pos = buf.readBlockPos();
        transferIn = buf.readBoolean();
        transferOut = buf.readBoolean();
    }

    public static void sendToServer(ITransferControllableTile tile) {

        TransferControlPacket packet = new TransferControlPacket();
        packet.pos = tile.pos();
        packet.transferIn = tile.transferControl().getTransferIn();
        packet.transferOut = tile.transferControl().getTransferOut();
        packet.sendToServer();
    }

}
