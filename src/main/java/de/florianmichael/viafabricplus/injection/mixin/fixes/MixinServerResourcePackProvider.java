package de.florianmichael.viafabricplus.injection.mixin.fixes;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.definition.PackFormatsDefinition;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.GameVersion;
import net.minecraft.client.resource.ServerResourcePackProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Mixin(ServerResourcePackProvider.class)
public class MixinServerResourcePackProvider {

    @Unique
    private File protocolhack_trackedFile;

    @Redirect(method = "getDownloadHeaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/SharedConstants;getGameVersion()Lnet/minecraft/GameVersion;"))
    private static GameVersion editHeaders() {
        return PackFormatsDefinition.current();
    }

    @Inject(method = "getDownloadHeaders", at = @At("TAIL"), cancellable = true)
    private static void removeHeaders(CallbackInfoReturnable<Map<String, String>> cir) {
        final LinkedHashMap<String, String> modifiableMap = new LinkedHashMap<>(cir.getReturnValue());
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThan(ProtocolVersion.v1_14)) {
            modifiableMap.remove("X-Minecraft-Version-ID");
        }
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThan(ProtocolVersion.v1_13)) {
            modifiableMap.remove("X-Minecraft-Pack-Format");
            modifiableMap.remove("User-Agent");
        }

        cir.setReturnValue(modifiableMap);
    }

    @Inject(method = "verifyFile", at = @At("HEAD"))
    public void keepFile(String expectedSha1, File file, CallbackInfoReturnable<Boolean> cir) {
        protocolhack_trackedFile = file;
    }

    @Redirect(method = "verifyFile", at = @At(value = "INVOKE", target = "Lcom/google/common/hash/HashCode;toString()Ljava/lang/String;", remap = false))
    public String revertHashAlgorithm(HashCode instance) {
        try {
            if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
                //noinspection deprecation
                return Hashing.sha1().hashBytes(Files.toByteArray(protocolhack_trackedFile)).toString();
            } else if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThan(ProtocolVersion.v1_18)) {
                return DigestUtils.sha1Hex(new FileInputStream(protocolhack_trackedFile));
            }
        } catch (IOException ignored) {
        }
        return instance.toString();
    }

    @Redirect(method = "verifyFile", at = @At(value = "INVOKE", target = "Ljava/lang/String;toLowerCase(Ljava/util/Locale;)Ljava/lang/String;"))
    public String disableIgnoreCase(String instance, Locale locale) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return instance;
        }

        return instance.toLowerCase(locale);
    }
}
