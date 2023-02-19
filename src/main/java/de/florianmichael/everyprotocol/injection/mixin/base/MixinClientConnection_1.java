package de.florianmichael.everyprotocol.injection.mixin.base;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.vialoadingbase.netty.CustomViaDecodeHandler;
import de.florianmichael.vialoadingbase.netty.CustomViaEncodeHandler;
import de.florianmichael.vialoadingbase.netty.NettyConstants;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnection_1 {

    @Final
    @Shadow
    ClientConnection field_11663;

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void hackNettyPipeline(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            // Creating the user connection
            final UserConnection user = new UserConnectionImpl(channel, true);

            // Creating the pipeline
            new ProtocolPipelineImpl(user);

            // ViaLoadingBase Packet handling
            channel.pipeline().addBefore("encoder", NettyConstants.HANDLER_ENCODER_NAME, new CustomViaEncodeHandler(user));
            channel.pipeline().addBefore("decoder", NettyConstants.HANDLER_DECODER_NAME, new CustomViaDecodeHandler(user));
        }
    }
}
