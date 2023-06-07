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
package de.florianmichael.viafabricplus.protocolhack;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.viafabricplus.base.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.base.event.FinishViaVersionStartupCallback;
import de.florianmichael.viafabricplus.protocolhack.command.ViaFabricPlusVLCommandHandler;
import de.florianmichael.viafabricplus.protocolhack.impl.ViaFabricPlusVLInjector;
import de.florianmichael.viafabricplus.protocolhack.impl.ViaFabricPlusVLLoader;
import de.florianmichael.viafabricplus.protocolhack.impl.platform.ViaFabricPlusViaLegacyPlatformImpl;
import de.florianmichael.viafabricplus.protocolhack.impl.platform.ViaFabricPlusViaVersionPlatformImpl;
import de.florianmichael.viafabricplus.protocolhack.netty.ViaFabricPlusVLLegacyPipeline;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.raphimc.vialoader.ViaLoader;
import net.raphimc.vialoader.impl.platform.ViaAprilFoolsPlatformImpl;
import net.raphimc.vialoader.impl.platform.ViaBackwardsPlatformImpl;
import net.raphimc.vialoader.impl.platform.ViaBedrockPlatformImpl;
import net.raphimc.vialoader.util.VersionEnum;

import java.net.InetSocketAddress;
import java.util.*;

public class ProtocolHack {
    public final static AttributeKey<UserConnection> LOCAL_VIA_CONNECTION = AttributeKey.newInstance("viafabricplus-via-connection");
    public final static AttributeKey<ClientConnection> LOCAL_MINECRAFT_CONNECTION = AttributeKey.newInstance("viafabricplus-minecraft-connection");
    public final static AttributeKey<VersionEnum> FORCED_VERSION = AttributeKey.newInstance("viafabricplus-forced-version");

    private final static Map<InetSocketAddress, VersionEnum> forcedVersions = new HashMap<>();
    public static VersionEnum targetVersion = VersionEnum.r1_20;

    public static VersionEnum getTargetVersion() {
        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().getNetworkHandler() == null) {
            return getTargetVersion((Channel) null);
        }

        return getTargetVersion(MinecraftClient.getInstance().getNetworkHandler().getConnection().channel);
    }

    public static VersionEnum getTargetVersion(final InetSocketAddress socketAddress) {
        if (forcedVersions.containsKey(socketAddress)) {
            return forcedVersions.get(socketAddress);
        }
        return getTargetVersion();
    }

    public static VersionEnum getTargetVersion(final Channel channel) {
        if (channel != null && channel.hasAttr(FORCED_VERSION)) {
            return channel.attr(FORCED_VERSION).get();
        }

        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().isInSingleplayer()) return VersionEnum.r1_20;

        return targetVersion;
    }

    public static Map<InetSocketAddress, VersionEnum> getForcedVersions() {
        return forcedVersions;
    }

    public static void injectVLBPipeline(final ClientConnection connection, final Channel channel, final InetSocketAddress address) {
        if (ProtocolHack.getForcedVersions().containsKey(address)) {
            channel.attr(ProtocolHack.FORCED_VERSION).set(ProtocolHack.getForcedVersions().get(address));
            ProtocolHack.getForcedVersions().remove(address);
        }
        final UserConnection user = new UserConnectionImpl(channel, true);
        channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).set(user);
        channel.attr(ProtocolHack.LOCAL_MINECRAFT_CONNECTION).set(connection);

        new ProtocolPipelineImpl(user);

        channel.pipeline().addLast(new ViaFabricPlusVLLegacyPipeline(user, ProtocolHack.getTargetVersion(channel), address));
    }

    private static void initCommands() {
        // Adding ViaVersion commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            final ViaFabricPlusVLCommandHandler commandHandler = (ViaFabricPlusVLCommandHandler) Via.getManager().getCommandHandler();

            final RequiredArgumentBuilder<FabricClientCommandSource, String> executor = RequiredArgumentBuilder.
                    <FabricClientCommandSource, String>argument("args", StringArgumentType.greedyString()).executes(commandHandler::execute).suggests(commandHandler::suggestion);

            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("viaversion").then(executor).executes(commandHandler::execute));
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("viafabricplus").then(executor).executes(commandHandler::execute));
        });
    }

    public static void setTargetVersion(VersionEnum targetVersion) {
        ProtocolHack.targetVersion = targetVersion;
        ChangeProtocolVersionCallback.EVENT.invoker().onChangeProtocolVersion(targetVersion);
    }

    public static void init() {
        ViaLoader.init(new ViaFabricPlusViaVersionPlatformImpl(null), new ViaFabricPlusVLLoader(), new ViaFabricPlusVLInjector(), new ViaFabricPlusVLCommandHandler(), ViaBackwardsPlatformImpl::new, ViaFabricPlusViaLegacyPlatformImpl::new, ViaAprilFoolsPlatformImpl::new, ViaBedrockPlatformImpl::new);
        initCommands();

        FinishViaVersionStartupCallback.EVENT.invoker().onFinishViaVersionStartup();
    }
}
