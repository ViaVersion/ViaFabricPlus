package de.florianmichael.viafabricplus.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.util.PipelineUtil;
import de.florianmichael.vialoadingbase.netty.VLBViaDecodeHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.logging.Level;

public class VFPVLBViaDecodeHandler extends VLBViaDecodeHandler {

    public VFPVLBViaDecodeHandler(UserConnection info) {
        super(info);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
        try {
            super.decode(ctx, bytebuf, out);
        } catch (Throwable e) {
            if (PipelineUtil.containsCause(e, CancelCodecException.class)) throw e;
            Via.getPlatform().getLogger().log(Level.SEVERE, "ViaLoadingBase Packet Error occurred", e);
            e.printStackTrace();
        }
    }
}
