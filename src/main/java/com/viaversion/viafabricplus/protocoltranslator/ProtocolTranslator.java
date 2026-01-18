/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.protocoltranslator;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.viaversion.viaaprilfools.ViaAprilFoolsPlatformImpl;
import com.viaversion.viabackwards.ViaBackwardsPlatformImpl;
import com.viaversion.viafabricplus.base.Events;
import com.viaversion.viafabricplus.injection.access.base.IConnection;
import com.viaversion.viafabricplus.protocoltranslator.impl.command.ViaFabricPlusCommandHandler;
import com.viaversion.viafabricplus.protocoltranslator.impl.platform.ViaFabricPlusViaLegacyPlatform;
import com.viaversion.viafabricplus.protocoltranslator.impl.platform.ViaFabricPlusViaVersionPlatform;
import com.viaversion.viafabricplus.protocoltranslator.impl.viaversion.ViaFabricPlusPlatformLoader;
import com.viaversion.viafabricplus.protocoltranslator.netty.NoReadFlowControlHandler;
import com.viaversion.viafabricplus.protocoltranslator.netty.ViaFabricPlusDecoder;
import com.viaversion.viafabricplus.protocoltranslator.protocol.ViaFabricPlusProtocol;
import com.viaversion.viafabricplus.protocoltranslator.util.ConfigPatcher;
import com.viaversion.viafabricplus.protocoltranslator.util.NoPacketSendChannel;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionType;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.platform.NoopInjector;
import com.viaversion.viaversion.platform.ViaChannelInitializer;
import com.viaversion.viaversion.platform.ViaDecodeHandler;
import com.viaversion.viaversion.platform.ViaEncodeHandler;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import dev.kastle.netty.channel.nethernet.config.NetherChannelOption;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.util.AttributeKey;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.lenni0451.reflect.stream.RStream;
import net.lenni0451.reflect.stream.field.FieldWrapper;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;
import net.minecraft.util.Util;
import net.raphimc.viabedrock.ViaBedrockPlatformImpl;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.netty.BatchLengthCodec;
import net.raphimc.viabedrock.netty.DisconnectHandler;
import net.raphimc.viabedrock.netty.PacketCodec;
import net.raphimc.viabedrock.netty.raknet.MessageCodec;
import net.raphimc.viabedrock.protocol.data.ProtocolConstants;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.netty.PreNettyLengthPrepender;
import net.raphimc.vialegacy.netty.PreNettyLengthRemover;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;

public final class ProtocolTranslator {

    /**
     * Internal state tracking for the game client connection, used in various Via providers
     */
    public static final AttributeKey<Connection> CLIENT_CONNECTION_ATTRIBUTE_KEY = AttributeKey.newInstance("viafabricplus-clientconnection");

    /**
     * Stores the target version ViaVersion is translating to. This will be always set, even if the {@link #NATIVE_VERSION} is set.
     */
    public static final AttributeKey<ProtocolVersion> TARGET_VERSION_ATTRIBUTE_KEY = AttributeKey.newInstance("viafabricplus-targetversion");

    /**
     * The native version of the client
     */
    public static final ProtocolVersion NATIVE_VERSION = ProtocolVersion.v1_21_11;

    /**
     * Name of the {@link FlowControlHandler} added to the pipeline
     */
    public static final String VIA_FLOW_CONTROL = "via-flow-control";

    /**
     * Protocol version that is used to enable protocol auto-detect
     */
    public static final ProtocolVersion AUTO_DETECT_PROTOCOL = new ProtocolVersion(VersionType.SPECIAL, -2, -1, "Auto Detect (1.7+ servers)", null) {
        @Override
        protected Comparator<ProtocolVersion> customComparator() {
            return (o1, o2) -> {
                if (o1 == AUTO_DETECT_PROTOCOL) {
                    return 1;
                } else if (o2 == AUTO_DETECT_PROTOCOL) {
                    return -1;
                } else {
                    return 0;
                }
            };
        }

        @Override
        public boolean isKnown() {
            return false;
        }
    };

    /**
     * This field stores the target version that you set in the GUI
     */
    private static ProtocolVersion targetVersion = NATIVE_VERSION;

    /**
     * This field stores the previous selected version if {@link #setTargetVersion(ProtocolVersion, boolean)} is called with revertOnDisconnect set to true
     */
    private static ProtocolVersion previousVersion = null;

    /**
     * Injects the ViaFabricPlus pipeline with all ViaVersion elements into a Minecraft pipeline
     *
     * @param connection the Minecraft connection
     */
    public static void injectViaPipeline(final Connection connection, final Channel channel) {
        final IConnection mixinClientConnection = (IConnection) connection;
        final ProtocolVersion serverVersion = mixinClientConnection.viaFabricPlus$getTargetVersion();

        channel.attr(ProtocolTranslator.CLIENT_CONNECTION_ATTRIBUTE_KEY).set(connection);
        channel.attr(ProtocolTranslator.TARGET_VERSION_ATTRIBUTE_KEY).set(serverVersion);

        if (serverVersion.equals(BedrockProtocolVersion.bedrockLatest)) {
            final ChannelConfig config = channel.config();
            // RakNet config
            config.setOption(RakChannelOption.RAK_PROTOCOL_VERSION, ProtocolConstants.BEDROCK_RAKNET_PROTOCOL_VERSION);
            config.setOption(RakChannelOption.RAK_COMPATIBILITY_MODE, true);
            config.setOption(RakChannelOption.RAK_CLIENT_INTERNAL_ADDRESSES, 20);
            config.setOption(RakChannelOption.RAK_TIME_BETWEEN_SEND_CONNECTION_ATTEMPTS_MS, 500);
            config.setOption(RakChannelOption.RAK_CONNECT_TIMEOUT, config.getOption(ChannelOption.CONNECT_TIMEOUT_MILLIS).longValue());
            config.setOption(RakChannelOption.RAK_SESSION_TIMEOUT, 30_000L);
            config.setOption(RakChannelOption.RAK_GUID, ThreadLocalRandom.current().nextLong());

            // NetherNet config
            config.setOption(NetherChannelOption.NETHER_CLIENT_HANDSHAKE_TIMEOUT_MS, config.getOption(ChannelOption.CONNECT_TIMEOUT_MILLIS));
            config.setOption(NetherChannelOption.NETHER_CLIENT_MAX_HANDSHAKE_ATTEMPTS, 1);
        }

        final UserConnection user = ViaChannelInitializer.createUserConnection(channel, true);
        mixinClientConnection.viaFabricPlus$setUserConnection(user);

        final ChannelPipeline pipeline = channel.pipeline();

        // ViaVersion
        pipeline.addBefore(HandlerNames.INBOUND_CONFIG, ViaDecodeHandler.NAME, new ViaFabricPlusDecoder(user));
        pipeline.addBefore(HandlerNames.ENCODER, ViaEncodeHandler.NAME, new ViaEncodeHandler(user));

        if (serverVersion.olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            // ViaLegacy
            pipeline.addBefore(HandlerNames.SPLITTER, PreNettyLengthPrepender.NAME, new PreNettyLengthPrepender(user));
            pipeline.addBefore(HandlerNames.PREPENDER, PreNettyLengthRemover.NAME, new PreNettyLengthRemover(user));
        } else if (serverVersion.equals(BedrockProtocolVersion.bedrockLatest)) {
            // ViaBedrock
            pipeline.addBefore(HandlerNames.SPLITTER, DisconnectHandler.NAME, new DisconnectHandler());
            pipeline.addBefore(HandlerNames.SPLITTER, MessageCodec.NAME, new MessageCodec());
            pipeline.replace(HandlerNames.SPLITTER, HandlerNames.SPLITTER, new BatchLengthCodec());
            pipeline.remove(HandlerNames.PREPENDER);
            pipeline.addBefore(ViaDecodeHandler.NAME, PacketCodec.NAME, new PacketCodec());
        }

        pipeline.addAfter(ViaDecodeHandler.NAME, ProtocolTranslator.VIA_FLOW_CONTROL, new NoReadFlowControlHandler());
        user.getProtocolInfo().getPipeline().add(ViaFabricPlusProtocol.INSTANCE);
    }

    public static void reorderPipeline(final ChannelPipeline pipeline) {
        final int decoderIndex = pipeline.names().indexOf(HandlerNames.DECOMPRESS);
        if (decoderIndex == -1) {
            return;
        }

        if (decoderIndex > pipeline.names().indexOf(ViaDecodeHandler.NAME)) {
            final ChannelHandler decoderHandler = pipeline.get(ViaDecodeHandler.NAME);
            final ChannelHandler encoderHandler = pipeline.get(ViaEncodeHandler.NAME);

            pipeline.remove(decoderHandler);
            pipeline.remove(encoderHandler);

            pipeline.addAfter(HandlerNames.DECOMPRESS, ViaDecodeHandler.NAME, decoderHandler);
            pipeline.addAfter(HandlerNames.COMPRESS, ViaEncodeHandler.NAME, encoderHandler);
        }
    }

    public static ProtocolVersion getTargetVersion() {
        return targetVersion;
    }

    public static ProtocolVersion getTargetVersion(final Channel channel) {
        if (!channel.hasAttr(TARGET_VERSION_ATTRIBUTE_KEY)) {
            throw new IllegalStateException("ViaFabricPlus has not injected into that channel yet!");
        }

        return channel.attr(TARGET_VERSION_ATTRIBUTE_KEY).get();
    }

    public static void setTargetVersion(final ProtocolVersion newVersion) {
        setTargetVersion(newVersion, false);
    }

    public static void setTargetVersion(final ProtocolVersion newVersion, final boolean revertOnDisconnect) {
        if (newVersion == null) {
            return;
        }

        final ProtocolVersion oldVersion = targetVersion;
        targetVersion = newVersion;
        if (oldVersion != newVersion) {
            if (revertOnDisconnect) {
                previousVersion = oldVersion;
            }
            Events.CHANGE_PROTOCOL_VERSION.invoker().onChangeProtocolVersion(oldVersion, targetVersion);
        }
    }

    /**
     * Resets the previous version if it is set. Calling {@link #setTargetVersion(ProtocolVersion, boolean)} with revertOnDisconnect set to true will set it.
     */
    public static void injectPreviousVersionReset(final Channel channel) {
        if (previousVersion == null) {
            return;
        }
        channel.closeFuture().addListener(future -> {
            setTargetVersion(previousVersion);
            previousVersion = null;
        });
    }

    public static UserConnection createDummyUserConnection(final ProtocolVersion clientVersion, final ProtocolVersion serverVersion) {
        final UserConnection user = new UserConnectionImpl(NoPacketSendChannel.INSTANCE, true);
        final ProtocolPipeline pipeline = new ProtocolPipelineImpl(user);
        final List<ProtocolPathEntry> path = Via.getManager().getProtocolManager().getProtocolPath(clientVersion, serverVersion);
        if (path != null) {
            for (ProtocolPathEntry pair : path) {
                pipeline.add(pair.protocol());
                pair.protocol().init(user);
            }
        }

        final ProtocolInfo info = user.getProtocolInfo();
        info.setState(State.PLAY);
        info.setProtocolVersion(clientVersion);
        info.setServerProtocolVersion(serverVersion);
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            final GameProfile profile = mc.player.getGameProfile();
            info.setUsername(profile.name());
            info.setUuid(profile.id());
        }

        return user;
    }

    public static UserConnection getPlayNetworkUserConnection() {
        final ClientPacketListener handler = Minecraft.getInstance().getConnection();
        if (handler == null) {
            return null;
        }

        return ((IConnection) handler.getConnection()).viaFabricPlus$getUserConnection();
    }

    private static void changeBedrockProtocolName() {
        final ProtocolVersion bedrockLatest = RStream.of(BedrockProtocolVersion.class).fields().by("bedrockLatest").get();

        final FieldWrapper name = RStream.of(bedrockLatest).withSuper().fields().by("name");
        name.set(name.get() + " (Work in progress)");
    }

    /**
     * This method is used to initialize the whole Protocol Translator
     *
     * @param path The path where the ViaVersion config files are located
     * @return A CompletableFuture that will be completed when the initialization is done
     */
    public static CompletableFuture<Void> init(final Path path) {
        if (SharedConstants.getProtocolVersion() != NATIVE_VERSION.getOriginalVersion()) {
            throw new IllegalStateException("Native version is not the same as the current version");
        }

        try {
            ConfigPatcher.patch(path.resolve("viaversion.yml"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to patch ViaVersion config", e);
        }

        // Register command callback for /viafabricplus
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            final ViaFabricPlusCommandHandler commandHandler = (ViaFabricPlusCommandHandler) Via.getManager().getCommandHandler();
            final RequiredArgumentBuilder<FabricClientCommandSource, String> executor = RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("args", StringArgumentType.greedyString()).executes(commandHandler::execute).suggests(commandHandler::suggestion);

            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("viafabricplus").then(executor).executes(commandHandler::execute));
        });

        return CompletableFuture.runAsync(() -> {
            // Load ViaVersion and register all platforms and their components
            ViaManagerImpl.initAndLoad(
                new ViaFabricPlusViaVersionPlatform(path.toFile()),
                new NoopInjector(),
                new ViaFabricPlusCommandHandler(),
                new ViaFabricPlusPlatformLoader(),
                () -> {
                    new ViaBackwardsPlatformImpl();
                    new ViaFabricPlusViaLegacyPlatform();
                    new ViaAprilFoolsPlatformImpl();
                    new ViaBedrockPlatformImpl();
                }
            );
            ProtocolVersion.register(AUTO_DETECT_PROTOCOL);
            changeBedrockProtocolName();
            ViaFabricPlusProtocol.INSTANCE.initialize();
        }, Util.backgroundExecutor());
    }

}
