package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.platform.PreNettyConstants;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.event.PipelineReorderEvent;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.crypto.Cipher;

@Mixin(ClientConnection.class)
public class MixinClientConnection implements IClientConnection {

    @Shadow private Channel channel;

    @Shadow private boolean encrypted;
    @Unique
    private Cipher viafabricplus_decryptionCipher;

    @Unique
    private Cipher viafabricplus_encryptionCipher;

    @Inject(method = "setCompressionThreshold", at = @At("RETURN"))
    private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
        channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
    }

    @Inject(method = "setupEncryption", at = @At("HEAD"), cancellable = true)
    private void storeEncryptionCiphers(Cipher decryptionCipher, Cipher encryptionCipher, CallbackInfo ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            ci.cancel();
            this.viafabricplus_decryptionCipher = decryptionCipher;
            this.viafabricplus_encryptionCipher = encryptionCipher;
        }
    }

    @Override
    public void viafabricplus_setupPreNettyEncryption() {
        this.encrypted = true;
        this.channel.pipeline().addBefore(PreNettyConstants.HANDLER_DECODER_NAME, "decrypt", new PacketDecryptor(this.viafabricplus_decryptionCipher));
        this.channel.pipeline().addBefore(PreNettyConstants.HANDLER_ENCODER_NAME, "encrypt", new PacketEncryptor(this.viafabricplus_encryptionCipher));
    }
}
