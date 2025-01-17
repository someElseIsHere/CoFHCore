package cofh.core.common.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.util.helpers.ItemHelper;
import cofh.lib.common.network.packet.IPacketServer;
import cofh.lib.common.network.packet.PacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import static cofh.core.common.network.packet.PacketIDs.PACKET_ITEM_MODE_CHANGE;

public class ItemModeChangePacket extends PacketBase implements IPacketServer {

    protected boolean decr;

    public ItemModeChangePacket() {

        super(PACKET_ITEM_MODE_CHANGE, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        if (!ItemHelper.isPlayerHoldingMultiModeItem(player)) {
            return;
        }
        if (decr && ItemHelper.decrHeldMultiModeItemState(player) || !decr && ItemHelper.incrHeldMultiModeItemState(player)) {
            ItemHelper.onHeldMultiModeItemChange(player);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBoolean(decr);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        decr = buf.readBoolean();
    }

    public static void incrMode() {

        sendToServer(false);
    }

    public static void decrMode() {

        sendToServer(true);
    }

    private static void sendToServer(boolean decr) {

        ItemModeChangePacket packet = new ItemModeChangePacket();
        packet.decr = decr;
        packet.sendToServer();
    }

}
