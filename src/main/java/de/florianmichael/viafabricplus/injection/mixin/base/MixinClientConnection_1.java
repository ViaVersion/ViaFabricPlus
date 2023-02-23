package de.florianmichael.viafabricplus.injection.mixin.base;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.platform.PreNettyConstants;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.netty.CustomViaDecodeHandler;
import de.florianmichael.vialoadingbase.netty.CustomViaEncodeHandler;
import de.florianmichael.vialoadingbase.netty.NettyConstants;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.network.ClientConnection;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.netty.PreNettyDecoder;
import net.raphimc.vialegacy.netty.PreNettyEncoder;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.baseprotocols.PreNettyBaseProtocol;
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
            final UserConnection user = new UserConnectionImpl(channel, true);
            channel.attr(ViaFabricPlus.LOCAL_VIA_CONNECTION).set(user);
            channel.attr(ViaFabricPlus.LOCAL_MINECRAFT_CONNECTION).set(field_11663);

            new ProtocolPipelineImpl(user);

            channel.pipeline().addBefore("encoder", NettyConstants.HANDLER_ENCODER_NAME, new CustomViaEncodeHandler(user));
            channel.pipeline().addBefore("decoder", NettyConstants.HANDLER_DECODER_NAME, new CustomViaDecodeHandler(user));

            if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
                user.getProtocolInfo().getPipeline().add(PreNettyBaseProtocol.INSTANCE);

                channel.pipeline().addBefore("prepender", PreNettyConstants.HANDLER_ENCODER_NAME, new PreNettyEncoder(user));
                channel.pipeline().addBefore("splitter", PreNettyConstants.HANDLER_DECODER_NAME, new PreNettyDecoder(user));
            }
        }
    }
}
