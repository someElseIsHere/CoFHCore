package cofh.core.common.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.common.inventory.ContainerMenuCoFH;
import cofh.lib.common.network.packet.IPacketServer;
import cofh.lib.common.network.packet.PacketBase;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import static cofh.core.common.network.packet.PacketIDs.PACKET_CONTAINER_CONFIG;

public class ContainerConfigPacket extends PacketBase implements IPacketServer {

    protected FriendlyByteBuf buffer;

    public ContainerConfigPacket() {

        super(PACKET_CONTAINER_CONFIG, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        if (player.containerMenu instanceof ContainerMenuCoFH container) {
            container.handleConfigPacket(buffer);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBytes(buffer);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        buffer = buf;
    }

    public static void sendToServer(ContainerMenuCoFH container) {

        if (container == null) {
            return;
        }
        ContainerConfigPacket packet = new ContainerConfigPacket();
        packet.buffer = container.getConfigPacket(new FriendlyByteBuf(Unpooled.buffer()));
        packet.sendToServer();
    }

}
