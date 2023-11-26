/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.client.resource.ServerResourcePackProvider;
import net.raphimc.vialoader.util.VersionEnum;
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
public abstract class MixinServerResourcePackProvider {

    @Unique
    private File viaFabricPlus$trackedFile;

    @Redirect(method = "getDownloadHeaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/SharedConstants;getGameVersion()Lnet/minecraft/GameVersion;"))
    private static GameVersion editHeaders() {
//        return PackFormatsMappings.current();
        return SharedConstants.getGameVersion(); // TODO | Fix
    }

    @Inject(method = "getDownloadHeaders", at = @At("TAIL"), cancellable = true)
    private static void removeHeaders(CallbackInfoReturnable<Map<String, String>> cir) {
        final LinkedHashMap<String, String> modifiableMap = new LinkedHashMap<>(cir.getReturnValue());
        if (ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.r1_14)) {
            modifiableMap.remove("X-Minecraft-Version-ID");
        }
        if (ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.r1_13)) {
            modifiableMap.remove("X-Minecraft-Pack-Format");
            modifiableMap.remove("User-Agent");
        }

        cir.setReturnValue(modifiableMap);
    }

    @Inject(method = "verifyFile", at = @At("HEAD"))
    private void keepFile(String expectedSha1, File file, CallbackInfoReturnable<Boolean> cir) {
        viaFabricPlus$trackedFile = file;
    }

    @Redirect(method = "verifyFile", at = @At(value = "INVOKE", target = "Lcom/google/common/hash/HashCode;toString()Ljava/lang/String;", remap = false))
    private String revertHashAlgorithm(HashCode instance) {
        try {
            if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
                //noinspection deprecation
                return Hashing.sha1().hashBytes(Files.toByteArray(viaFabricPlus$trackedFile)).toString();
            } else if (ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.r1_18tor1_18_1)) {
                return DigestUtils.sha1Hex(new FileInputStream(viaFabricPlus$trackedFile));
            }
        } catch (IOException ignored) {
        }
        return instance.toString();
    }

    @Redirect(method = "verifyFile", at = @At(value = "INVOKE", target = "Ljava/lang/String;toLowerCase(Ljava/util/Locale;)Ljava/lang/String;"))
    private String disableIgnoreCase(String instance, Locale locale) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            return instance;
        }
        return instance.toLowerCase(locale);
    }

}
