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

package de.florianmichael.viafabricplus.injection.mixin.base.perserverversion;

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
public abstract class MixinServerInfo implements IServerInfo {

    @Shadow
    public String name;

    @Unique
    private VersionEnum viaFabricPlus$forcedVersion = null;

    @Unique
    private boolean viaFabricPlus$passedDirectConnectScreen;

    @Unique
    private VersionEnum viaFabricPlus$translatingVersion;

    @Inject(method = "toNbt", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void saveForcedVersion(CallbackInfoReturnable<NbtCompound> cir, NbtCompound nbtCompound) {
        if (viaFabricPlus$forcedVersion == null) {
            return;
        }

        nbtCompound.putInt("viafabricplus_forcedversion", viaFabricPlus$forcedVersion.getOriginalVersion());
    }

    @Inject(method = "fromNbt", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void loadForcedVersion(NbtCompound root, CallbackInfoReturnable<ServerInfo> cir, ServerInfo serverInfo) {
        if (root.contains("viafabricplus_forcedversion")) {
            final VersionEnum version = VersionEnum.fromProtocolId(root.getInt("viafabricplus_forcedversion"));
            if (VersionEnum.UNKNOWN.equals(version)) {
                ((IServerInfo) serverInfo).viaFabricPlus$forceVersion(null);
            } else {
                ((IServerInfo) serverInfo).viaFabricPlus$forceVersion(version);
            }
        }
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void syncForcedVersion(ServerInfo serverInfo, CallbackInfo ci) {
        viaFabricPlus$forceVersion(((IServerInfo) serverInfo).viaFabricPlus$forcedVersion());
    }

    @Override
    public VersionEnum viaFabricPlus$forcedVersion() {
        return viaFabricPlus$forcedVersion;
    }

    @Override
    public void viaFabricPlus$forceVersion(VersionEnum version) {
        viaFabricPlus$forcedVersion = version;
    }

    @Override
    public boolean viaFabricPlus$passedDirectConnectScreen() {
        final boolean previous = viaFabricPlus$passedDirectConnectScreen;
        viaFabricPlus$passedDirectConnectScreen = false;

        return previous;
    }

    @Override
    public void viaFabricPlus$passDirectConnectScreen() {
        viaFabricPlus$passedDirectConnectScreen = true;
    }

    @Override
    public VersionEnum viaFabricPlus$translatingVersion() {
        return viaFabricPlus$translatingVersion;
    }

    @Override
    public void viaFabricPlus$setTranslatingVersion(VersionEnum version) {
        viaFabricPlus$translatingVersion = version;
    }

}
