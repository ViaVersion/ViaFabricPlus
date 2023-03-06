package de.florianmichael.viafabricplus.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.raphimc.vialegacy.netty.PreNettyEncoder;

public class VFPPreNettyEncoder extends PreNettyEncoder {

    public VFPPreNettyEncoder(UserConnection user) {
        super(user);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        if (Via.getManager().isDebug()) {
            final ByteBuf myBuf = in.copy();
            Type.VAR_INT.readPrimitive(myBuf); // length
            Via.getPlatform().getLogger().info("Encoding pre netty packet: " + (Type.VAR_INT.readPrimitive(myBuf) & 255));
        }
        super.encode(ctx, in, out);
    }
}
