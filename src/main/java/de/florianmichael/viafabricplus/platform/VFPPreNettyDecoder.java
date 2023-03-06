package de.florianmichael.viafabricplus.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.raphimc.vialegacy.netty.PreNettyDecoder;

import java.util.List;

public class VFPPreNettyDecoder extends PreNettyDecoder {

    public VFPPreNettyDecoder(UserConnection user) {
        super(user);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (Via.getManager().isDebug()) {
            if (!in.isReadable() || in.readableBytes() <= 0) {
                return;
            }
            Via.getPlatform().getLogger().info("Decoding pre netty packet: " + in.copy().readUnsignedByte());
        }
        super.decode(ctx, in, out);
    }
}
