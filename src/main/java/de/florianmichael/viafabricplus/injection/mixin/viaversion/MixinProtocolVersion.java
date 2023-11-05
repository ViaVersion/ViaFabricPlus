/*
 * This file is part of ViaProxy - https://github.com/RaphiMC/ViaProxy
 * Copyright (C) 2023 RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    private static Map<String, Pair<String, VersionRange>> viafabricplus_remaps;

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void initMaps(CallbackInfo ci) {
        viafabricplus_remaps = new HashMap<>();
        viafabricplus_remaps.put("1.7-1.7.5", new Pair<>("1.7.2-1.7.5", new VersionRange("1.7", 2, 5)));
        viafabricplus_remaps.put("1.9.3/4", new Pair<>("1.9.3-1.9.4", null));
        viafabricplus_remaps.put("1.11.1/2", new Pair<>("1.11.1-1.11.2", null));
        viafabricplus_remaps.put("1.16.4/5", new Pair<>("1.16.4-1.16.5", null));
        viafabricplus_remaps.put("1.18/1.18.1", new Pair<>("1.18-1.18.1", null));
        viafabricplus_remaps.put("1.19.1/2", new Pair<>("1.19.1-1.19.2", null));
        viafabricplus_remaps.put("1.20/1.20.1", new Pair<>("1.20-1.20.1", null));
        viafabricplus_remaps.put("1.20.3", new Pair<>("23w44a", null));
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;register(ILjava/lang/String;)Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;"))
    private static ProtocolVersion unregisterAndRenameVersions(int version, String name) {
        final Pair<String, VersionRange> remapEntry = viafabricplus_remaps.get(name);
        if (remapEntry != null) {
            if (remapEntry.key() != null) name = remapEntry.key();
        }

        return ProtocolVersion.register(version, name);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget", "InvalidInjectorMethodSignature"}) // Optional injection
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;register(IILjava/lang/String;)Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;"), require = 0)
    private static ProtocolVersion unregisterAndRenameVersions(int version, int snapshotVersion, String name) {
        final Pair<String, VersionRange> remapEntry = viafabricplus_remaps.get(name);
        if (remapEntry != null) {
            if (remapEntry.key() != null) name = remapEntry.key();
        }

        return ProtocolVersion.register(version, snapshotVersion, name);
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;register(ILjava/lang/String;Lcom/viaversion/viaversion/api/protocol/version/VersionRange;)Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;"))
    private static ProtocolVersion unregisterAndRenameVersions(int version, String name, VersionRange versionRange) {
        final Pair<String, VersionRange> remapEntry = viafabricplus_remaps.get(name);
        if (remapEntry != null) {
            if (remapEntry.key() != null) name = remapEntry.key();
            if (remapEntry.value() != null) versionRange = remapEntry.value();
        }

        return ProtocolVersion.register(version, name, versionRange);
    }
}
