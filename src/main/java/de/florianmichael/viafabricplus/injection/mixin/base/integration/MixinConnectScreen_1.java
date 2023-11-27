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

package de.florianmichael.viafabricplus.injection.mixin.base.integration;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import de.florianmichael.viafabricplus.settings.impl.AuthenticationSettings;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import net.lenni0451.mcping.MCPing;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public abstract class MixinConnectScreen_1 {

    @Shadow
    @Final
    ServerInfo field_40415;

    @Shadow
    @Final
    ConnectScreen field_2416;

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/session/Session;getUsername()Ljava/lang/String;"))
    private String useClassiCubeUsername(Session instance) {
        if (AuthenticationSettings.global().setSessionNameToClassiCubeNameInServerList.getValue() && ViaFabricPlusClassicMPPassProvider.classiCubeMPPass != null) {
            final var account = ViaFabricPlus.global().getSaveManager().getAccountsSave().getClassicubeAccount();
            if (account != null) {
                return account.username();
            }
        }

        return instance.getUsername();
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void setServerInfo(CallbackInfo ci, InetSocketAddress inetSocketAddress) {
        final VersionEnum serverVersion = ((IServerInfo) this.field_40415).viaFabricPlus$forcedVersion();
        if (serverVersion != null) {
            ProtocolHack.setTargetVersion(serverVersion);
        } else if (GeneralSettings.global().autoDetectVersion.getValue()) {
            this.field_2416.setStatus(Text.translatable("base.viafabricplus.detecting_server_version"));
            MCPing
                    .pingModern(-1)
                    .address(inetSocketAddress.getHostString(), inetSocketAddress.getPort())
                    .noResolve()
                    .timeout(1000, 1000)
                    .exceptionHandler(t -> {
                    })
                    .responseHandler(r -> {
                        if (ProtocolVersion.isRegistered(r.version.protocol)) {
                            ProtocolHack.setTargetVersion(VersionEnum.fromProtocolId(r.version.protocol));
                        }
                    })
                    .getSync();
        }
    }

}
