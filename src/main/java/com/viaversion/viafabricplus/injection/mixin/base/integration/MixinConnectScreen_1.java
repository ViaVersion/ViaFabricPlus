/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.base.integration;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viafabricplus.injection.access.base.IServerData;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import com.viaversion.viafabricplus.protocoltranslator.util.ProtocolVersionDetector;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.settings.impl.AuthenticationSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianreuth.classic4j.model.classicube.account.CCAccount;
import io.netty.channel.ChannelFuture;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.Optional;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.User;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.EventLoopGroupHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.gui.screens.ConnectScreen$1")
public abstract class MixinConnectScreen_1 {

    @Shadow
    @Final
    ServerData val$server;

    @Final
    @Shadow
    ServerAddress val$hostAndPort;

    @Shadow
    @Final
    ConnectScreen field_2416;

    @Unique
    private boolean viaFabricPlus$useClassiCubeAccount;

    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;", remap = false))
    private Object setServerInfoAndProtocolVersion(Optional<InetSocketAddress> instance, Operation<Object> original) throws Exception {
        final InetSocketAddress address = (InetSocketAddress) original.call(instance);
        final IServerData mixinServerInfo = (IServerData) this.val$server;

        ProtocolVersion targetVersion = ProtocolTranslator.getTargetVersion();
        if (mixinServerInfo.viaFabricPlus$forcedVersion() != null && !mixinServerInfo.viaFabricPlus$passedDirectConnectScreen()) {
            targetVersion = mixinServerInfo.viaFabricPlus$forcedVersion();
            mixinServerInfo.viaFabricPlus$passDirectConnectScreen(false); // reset state
        }
        if (targetVersion == ProtocolTranslator.AUTO_DETECT_PROTOCOL) {
            // If the server got already pinged, try to use that version if it's valid. Otherwise, perform auto-detect
            final boolean serverPinged = this.val$server.state() == ServerData.State.SUCCESSFUL || this.val$server.state() == ServerData.State.INCOMPATIBLE;
            if (serverPinged) {
                targetVersion = ProtocolVersion.getProtocol(this.val$server.protocol);
            }
            if (!serverPinged || !targetVersion.isKnown()) {
                this.field_2416.updateStatus(Component.translatable("base.viafabricplus.detecting_server_version"));
                try {
                    targetVersion = ProtocolVersionDetector.get(this.val$hostAndPort, address, ProtocolTranslator.NATIVE_VERSION);
                } catch (final ConnectException ignored) {
                    // Don't let this one through as not relevant
                }
            }
        }
        ProtocolTranslator.setTargetVersion(targetVersion, true);
        this.viaFabricPlus$useClassiCubeAccount = AuthenticationSettings.INSTANCE.setSessionNameToClassiCubeNameInServerList.getValue() && ViaFabricPlusClassicMPPassProvider.classicubeMPPass != null;

        return address;
    }

    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connect(Ljava/net/InetSocketAddress;Lnet/minecraft/server/network/EventLoopGroupHolder;Lnet/minecraft/network/Connection;)Lio/netty/channel/ChannelFuture;"))
    private ChannelFuture resetProtocolVersionAfterDisconnect(InetSocketAddress inetSocketAddress, EventLoopGroupHolder eventLoopGroupHolder, Connection connection, Operation<ChannelFuture> original) {
        final ChannelFuture future = original.call(inetSocketAddress, eventLoopGroupHolder, connection);
        ProtocolTranslator.injectPreviousVersionReset(future.channel());
        return future;
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/User;getName()Ljava/lang/String;"))
    private String useClassiCubeUsername(User instance) {
        if (this.viaFabricPlus$useClassiCubeAccount) {
            final CCAccount account = SaveManager.INSTANCE.getAccountsSave().getClassicubeAccount();
            if (account != null) {
                return account.username();
            }
        }
        return instance.getName();
    }

}
