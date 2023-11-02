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
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.storage.InventoryTracker1_16;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.storage.ConfigurationState;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.event.FinishViaVersionStartupCallback;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.protocolhack.command.ViaFabricPlusVLCommandHandler;
import de.florianmichael.viafabricplus.protocolhack.impl.ViaFabricPlusVLInjector;
import de.florianmichael.viafabricplus.protocolhack.impl.ViaFabricPlusVLLoader;
import de.florianmichael.viafabricplus.protocolhack.impl.platform.ViaFabricPlusViaLegacyPlatformImpl;
import de.florianmichael.viafabricplus.protocolhack.impl.platform.ViaFabricPlusViaVersionPlatformImpl;
import de.florianmichael.viafabricplus.protocolhack.netty.ViaFabricPlusVLLegacyPipeline;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.raphimc.vialoader.ViaLoader;
import net.raphimc.vialoader.impl.platform.ViaAprilFoolsPlatformImpl;
import net.raphimc.vialoader.impl.platform.ViaBackwardsPlatformImpl;
import net.raphimc.vialoader.impl.platform.ViaBedrockPlatformImpl;
import net.raphimc.vialoader.util.VersionEnum;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the whole Protocol Translator, here all important variables are stored
 */
public class ProtocolHack {
    /**
     * These attribute keys are used to track the main connections of Minecraft and ViaVersion, so that they can be used later during the connection to send packets.
     */
    public final static AttributeKey<ClientConnection> LOCAL_MINECRAFT_CONNECTION = AttributeKey.newInstance("viafabricplus-minecraft-connection");
    public final static AttributeKey<UserConnection> LOCAL_VIA_CONNECTION = AttributeKey.newInstance("viafabricplus-via-connection");

    /**
     * This list is temporary and is used during the connection to the server to create the FORCED_VERSION attribute.
     */
    private final static Map<InetSocketAddress, VersionEnum> forcedVersions = new HashMap<>();

    /**
     * This attribute stores the forced version for the current connection (if you set a specific version in the Edit Server screen)
     */
    public final static AttributeKey<VersionEnum> FORCED_VERSION = AttributeKey.newInstance("viafabricplus-forced-version");

    /**
     * This field stores the target version that you set in the GUI
     */
    public static VersionEnum targetVersion = ViaFabricPlus.NATIVE_VERSION;

    /**
     * This method is used when you need the target version after connecting to the server.
     *
     * @return the target version
     */
    public static VersionEnum getTargetVersion() {
        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().getNetworkHandler() == null) {
            return getTargetVersion((Channel) null);
        }

        return getTargetVersion(MinecraftClient.getInstance().getNetworkHandler().getConnection().channel);
    }

    /**
     * This method is used when you need the target version while connecting to the server before Netty is started
     *
     * @param socketAddress the target address
     * @return the target version
     */
    public static VersionEnum getTargetVersion(final InetSocketAddress socketAddress) {
        if (forcedVersions.containsKey(socketAddress)) {
            return forcedVersions.get(socketAddress);
        }
        return getTargetVersion();
    }

    /**
     * This method is used when you need the target version while connecting to the server after Netty is started and before ViaVersion is finished loading.
     *
     * @param channel channel of the current connection
     * @return the target version
     */
    public static VersionEnum getTargetVersion(final Channel channel) {
        if (channel != null && channel.hasAttr(FORCED_VERSION)) {
            return channel.attr(FORCED_VERSION).get();
        }

        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().isInSingleplayer()) return ViaFabricPlus.NATIVE_VERSION;

        return targetVersion;
    }

    /**
     * This method is used when you need the target version while connecting to the server after Netty is started and after ViaVersion is finished loading.
     *
     * @param serverInfo the current server info
     * @return the target version
     */
    public static VersionEnum getTargetVersion(final ServerInfo serverInfo) {
        final var forcedVersion = ((IServerInfo) serverInfo).viafabricplus_forcedVersion();
        if (forcedVersion == null) return  getTargetVersion();

        return forcedVersion;
    }

    public static Map<InetSocketAddress, VersionEnum> getForcedVersions() {
        return forcedVersions;
    }

    /**
     * Injects the ViaFabricPlus pipeline with all ViaVersion elements into a Minecraft pipeline
     *
     * @param connection the Minecraft connection
     * @param channel the current channel
     * @param address the target address
     */
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

    /**
     * Adding ViaVersion's command system into Fabric
     */
    private static void initCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            final ViaFabricPlusVLCommandHandler commandHandler = (ViaFabricPlusVLCommandHandler) Via.getManager().getCommandHandler();

            final RequiredArgumentBuilder<FabricClientCommandSource, String> executor = RequiredArgumentBuilder.
                    <FabricClientCommandSource, String>argument("args", StringArgumentType.greedyString()).executes(commandHandler::execute).suggests(commandHandler::suggestion);

            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("viaversion").then(executor).executes(commandHandler::execute));
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("viafabricplus").then(executor).executes(commandHandler::execute));
        });
    }

    /**
     * Sets the target version of the GUI
     *
     * @param targetVersion the target version
     */
    public static void setTargetVersion(VersionEnum targetVersion) {
        ProtocolHack.targetVersion = targetVersion;
        ChangeProtocolVersionCallback.EVENT.invoker().onChangeProtocolVersion(targetVersion);
    }

    /**
     * @return Creates a Fake UserConnection class with a valid protocol pipeline to emulate packets
     */
    public static UserConnection createFakerUserConnection() {
        return createFakerUserConnection(getMainUserConnection().getChannel());
    }

    /**
     * @param channel the current channel
     * @return Creates a Fake UserConnection class with a valid protocol pipeline to emulate packets
     */
    public static UserConnection createFakerUserConnection(final Channel channel) {
        final var fake = new UserConnectionImpl(channel, true);
        fake.getProtocolInfo().setPipeline(new ProtocolPipelineImpl(fake));

        fake.put(new InventoryTracker1_16());
        fake.put(new ConfigurationState());

        //noinspection DataFlowIssue
        fake.get(ConfigurationState.class).setBridgePhase(ConfigurationState.BridgePhase.NONE);

        return fake;
    }

    /**
     * @return Returns the current ViaVersion UserConnection via the LOCAL_VIA_CONNECTION channel attribute
     */
    public static UserConnection getMainUserConnection() {
        final MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) return null;
        final var channel = client.getNetworkHandler().getConnection().channel;
        if (!channel.hasAttr(LOCAL_VIA_CONNECTION)) return null;

        return channel.attr(LOCAL_VIA_CONNECTION).get();
    }

    /**
     * Starts ViaVersion
     */
    public static void init() {
        ViaLoader.init(new ViaFabricPlusViaVersionPlatformImpl(ViaFabricPlus.RUN_DIRECTORY), new ViaFabricPlusVLLoader(), new ViaFabricPlusVLInjector(), new ViaFabricPlusVLCommandHandler(), ViaBackwardsPlatformImpl::new, ViaFabricPlusViaLegacyPlatformImpl::new, ViaAprilFoolsPlatformImpl::new, ViaBedrockPlatformImpl::new);
        initCommands();

        FinishViaVersionStartupCallback.EVENT.invoker().onFinishViaVersionStartup();
    }
}
