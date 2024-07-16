/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.settings.impl.DebugSettings;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.Optional;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class MixinClientCommonNetworkHandler {

    @Inject(method = "savePacketErrorReport", at = @At("HEAD"), cancellable = true)
    private void dontCreatePacketErrorCrashReports(CallbackInfoReturnable<Optional<Path>> cir) {
        if (DebugSettings.global().dontCreatePacketErrorCrashReports.isEnabled()) {
            cir.setReturnValue(Optional.empty());
        }
    }

    @WrapWithCondition(method = "onPacketException", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;disconnect(Lnet/minecraft/network/DisconnectionInfo;)V"))
    private boolean dontDisconnectOnPacketException(ClientConnection instance, DisconnectionInfo disconnectionInfo) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_20_3);
    }

}
