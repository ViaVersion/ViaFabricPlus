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

package de.florianmichael.viafabricplus.injection.mixin.base.integration;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import de.florianmichael.viafabricplus.protocoltranslator.util.ProtocolVersionDetector;
import de.florianmichael.viafabricplus.settings.impl.AuthenticationSettings;
import io.netty.channel.ChannelFuture;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.Session;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.gui.screen.multiplayer.ConnectScreen$1")
public abstract class MixinConnectScreen_1 {

    @Shadow
    @Final
    ServerInfo field_40415;

    @Shadow
    @Final
    ConnectScreen field_2416;

    @Unique
    private boolean viaFabricPlus$useClassiCubeAccount;

    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;"))
    private ChannelFuture setServerInfoAndHandleDisconnect(InetSocketAddress address, boolean useEpoll, ClientConnection connection, Operation<ChannelFuture> original) {
        final IServerInfo mixinServerInfo = (IServerInfo) this.field_40415;

        ProtocolVersion targetVersion = ProtocolTranslator.getTargetVersion();
        if (mixinServerInfo.viaFabricPlus$forcedVersion() != null && !mixinServerInfo.viaFabricPlus$passedDirectConnectScreen()) {
            targetVersion = mixinServerInfo.viaFabricPlus$forcedVersion();
            mixinServerInfo.viaFabricPlus$passDirectConnectScreen(false); // reset state
        }
        if (targetVersion == ProtocolTranslator.AUTO_DETECT_PROTOCOL) {
            this.field_2416.setStatus(Text.translatable("base.viafabricplus.detecting_server_version"));
            targetVersion = ProtocolVersionDetector.get(address, ProtocolTranslator.NATIVE_VERSION);
        }
        ProtocolTranslator.setTargetVersion(targetVersion, true);

        this.viaFabricPlus$useClassiCubeAccount = AuthenticationSettings.global().setSessionNameToClassiCubeNameInServerList.getValue() && ViaFabricPlusClassicMPPassProvider.classicubeMPPass != null;

        final ChannelFuture future = original.call(address, useEpoll, connection);
        ProtocolTranslator.injectPreviousVersionReset(future.channel());

        return future;
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/session/Session;getUsername()Ljava/lang/String;"))
    private String useClassiCubeUsername(Session instance) {
        if (this.viaFabricPlus$useClassiCubeAccount) {
            final var account = ViaFabricPlus.global().getSaveManager().getAccountsSave().getClassicubeAccount();
            if (account != null) return account.username();
        }
        return instance.getUsername();
    }

}
