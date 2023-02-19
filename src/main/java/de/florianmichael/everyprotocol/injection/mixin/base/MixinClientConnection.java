package de.florianmichael.everyprotocol.injection.mixin.base;

import de.florianmichael.everyprotocol.platform.PreNettyConstants;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.event.PipelineReorderEvent;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.raphimc.vialegacy.api.LegacyProtocolVersions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.crypto.Cipher;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Shadow private Channel channel;

    @Shadow private boolean encrypted;
    @Unique
    private Cipher vialegacy_decryptionCipher;

    @Unique
    private Cipher vialegacy_encryptionCipher;

    @Inject(method = "setCompressionThreshold", at = @At("RETURN"))
    private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
        channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
    }

    @Inject(method = "setupEncryption", at = @At("HEAD"), cancellable = true)
    private void storeEncryptionCiphers(Cipher decryptionCipher, Cipher encryptionCipher, CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersions.r1_6_4)) {
            ci.cancel();
            this.vialegacy_decryptionCipher = decryptionCipher;
            this.vialegacy_encryptionCipher = encryptionCipher;
        }
    }

    @Unique
    public void vialegacy_setupPreNettyEncryption() {
        this.encrypted = true;
        this.channel.pipeline().addBefore(PreNettyConstants.DECODER, "decrypt", new PacketDecryptor(this.vialegacy_decryptionCipher));
        this.channel.pipeline().addBefore(PreNettyConstants.ENCODER, "encrypt", new PacketEncryptor(this.vialegacy_encryptionCipher));
    }
}
