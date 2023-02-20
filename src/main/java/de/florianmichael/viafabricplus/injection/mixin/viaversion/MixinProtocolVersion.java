/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.injection.mixin.viaversion;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionRange;
import com.viaversion.viaversion.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = ProtocolVersion.class, remap = false)
public abstract class MixinProtocolVersion {

    @Unique
    private static Map<String, Pair<String, VersionRange>> protocolhack_remaps;

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void initMaps(CallbackInfo ci) {
        protocolhack_remaps = new HashMap<>();
        protocolhack_remaps.put("1.7-1.7.5", new Pair<>("1.7.2-1.7.5", new VersionRange("1.7", 2, 5)));
        protocolhack_remaps.put("1.9.3/4", new Pair<>("1.9.3-1.9.4", null));
        protocolhack_remaps.put("1.11.1/2", new Pair<>("1.11.1-1.11.2", null));
        protocolhack_remaps.put("1.16.4/5", new Pair<>("1.16.4-1.16.5", null));
        protocolhack_remaps.put("1.18/1.18.1", new Pair<>("1.18-1.18.1", null));
        protocolhack_remaps.put("1.19.1/2", new Pair<>("1.19.1-1.19.2", null));
        protocolhack_remaps.put("1.19.4", new Pair<>("23w06a", null));
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;register(ILjava/lang/String;)Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;"))
    private static ProtocolVersion unregisterAndRenameVersions(int version, String name) {
        final Pair<String, VersionRange> remapEntry = protocolhack_remaps.get(name);
        if (remapEntry != null) {
            if (remapEntry.key() != null) name = remapEntry.key();
        }

        return ProtocolVersion.register(version, name);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget", "InvalidInjectorMethodSignature"}) // Optional injection
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;register(IILjava/lang/String;)Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;"), require = 0)
    private static ProtocolVersion unregisterAndRenameVersions(int version, int snapshotVersion, String name) {
        final Pair<String, VersionRange> remapEntry = protocolhack_remaps.get(name);
        if (remapEntry != null) {
            if (remapEntry.key() != null) name = remapEntry.key();
        }

        return ProtocolVersion.register(version, snapshotVersion, name);
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;register(ILjava/lang/String;Lcom/viaversion/viaversion/api/protocol/version/VersionRange;)Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;"))
    private static ProtocolVersion unregisterAndRenameVersions(int version, String name, VersionRange versionRange) {
        final Pair<String, VersionRange> remapEntry = protocolhack_remaps.get(name);
        if (remapEntry != null) {
            if (remapEntry.key() != null) name = remapEntry.key();
            if (remapEntry.value() != null) versionRange = remapEntry.value();
        }

        return ProtocolVersion.register(version, name, versionRange);
    }
}
