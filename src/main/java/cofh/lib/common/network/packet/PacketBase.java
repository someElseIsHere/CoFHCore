package cofh.lib.common.network.packet;

import cofh.lib.common.network.PacketHandler;

public abstract class PacketBase implements IPacket {

    protected final int id;
    protected final PacketHandler handler;

    protected PacketBase(int id, PacketHandler handler) {

        this.id = id;
        this.handler = handler;
    }

    @Override
    public byte getId() {

        return (byte) id;
    }

    @Override
    public PacketHandler getHandler() {

        return handler;
    }

}
