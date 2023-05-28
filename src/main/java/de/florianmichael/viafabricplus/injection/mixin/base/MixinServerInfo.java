/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerInfo.class)
public class MixinServerInfo implements IServerInfo {

    @Shadow public String name;

    @Unique
    private VersionEnum viafabricplus_forcedVersion = null;

    @Override
    public VersionEnum viafabricplus_forcedVersion() {
        return viafabricplus_forcedVersion;
    }

    @Override
    public void viafabricplus_forceVersion(VersionEnum version) {
        viafabricplus_forcedVersion = version;
    }

    @Inject(method = "toNbt", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void saveForcedVersion(CallbackInfoReturnable<NbtCompound> cir, NbtCompound nbtCompound) {
        if (viafabricplus_forcedVersion == null) return;

        nbtCompound.putInt("viafabricplus_forcedversion", viafabricplus_forcedVersion.getVersion());
    }

    @Inject(method = "fromNbt", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void loadForcedVersion(NbtCompound root, CallbackInfoReturnable<ServerInfo> cir, ServerInfo serverInfo) {
        if (root.contains("viafabricplus_forcedversion")) {
            try {
                ((IServerInfo) serverInfo).viafabricplus_forceVersion(VersionEnum.fromProtocolId(root.getInt("viafabricplus_forcedversion")));
            } catch (Exception ignored) {
                // Version doesn't exist anymore
            }
        }
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    public void trackForcedVersion(ServerInfo serverInfo, CallbackInfo ci) {
        viafabricplus_forceVersion(((IServerInfo) serverInfo).viafabricplus_forcedVersion());
    }
}
