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

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.definition.v1_19_0.provider.CommandArgumentsProvider;
import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.event.FinishViaLoadingBaseStartupCallback;
import de.florianmichael.viafabricplus.protocolhack.platform.ViaAprilFoolsPlatformImpl;
import de.florianmichael.viafabricplus.protocolhack.platform.ViaBedrockPlatformImpl;
import de.florianmichael.viafabricplus.protocolhack.platform.ViaLegacyPlatformImpl;
import de.florianmichael.viafabricplus.protocolhack.provider.*;
import de.florianmichael.viafabricplus.protocolhack.provider.viabedrock.ViaFabricPlusBlobCacheProvider;
import de.florianmichael.viafabricplus.protocolhack.provider.viabedrock.ViaFabricPlusNettyPipelineProvider;
import de.florianmichael.viafabricplus.protocolhack.provider.vialegacy.*;
import de.florianmichael.viafabricplus.protocolhack.provider.viaversion.ViaFabricPlusHandItemProvider;
import de.florianmichael.viafabricplus.protocolhack.provider.viaversion.ViaFabricPlusMovementTransmitterProvider;
import de.florianmichael.viafabricplus.protocolhack.provider.vialoadingbase.ViaFabricPlusVLBBaseVersionProvider;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.model.ComparableProtocolVersion;
import de.florianmichael.vialoadingbase.model.Platform;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.Lazy;
import net.raphimc.viaaprilfools.api.AprilFoolsProtocolVersion;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.providers.BlobCacheProvider;
import net.raphimc.viabedrock.protocol.providers.NettyPipelineProvider;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicCustomCommandProvider;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicWorldHeightProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.providers.EncryptionProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ProtocolHack {
    public final static AttributeKey<UserConnection> LOCAL_VIA_CONNECTION = AttributeKey.newInstance("viafabricplus-via-connection");
    public final static AttributeKey<ClientConnection> LOCAL_MINECRAFT_CONNECTION = AttributeKey.newInstance("viafabricplus-minecraft-connection");
    public final static AttributeKey<ComparableProtocolVersion> FORCED_VERSION = AttributeKey.newInstance("viafabricplus-forced-version");

    private final static Map<InetSocketAddress, ComparableProtocolVersion> forcedVersions = new HashMap<>();
    private final static List<InetSocketAddress> rakNetPingSessions = new ArrayList<>();

    public static ComparableProtocolVersion getTargetVersion() {
        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().getNetworkHandler() == null) return getTargetVersion(null);

        return getTargetVersion(MinecraftClient.getInstance().getNetworkHandler().getConnection().channel);
    }

    public static ComparableProtocolVersion getTargetVersion(final Channel channel) {
        if (channel != null && channel.hasAttr(FORCED_VERSION)) return channel.attr(FORCED_VERSION).get();

        return ViaLoadingBase.getInstance().getTargetVersion();
    }

    public static Map<InetSocketAddress, ComparableProtocolVersion> getForcedVersions() {
        return forcedVersions;
    }

    public static List<InetSocketAddress> getRakNetPingSessions() {
        return rakNetPingSessions;
    }

    public static boolean isEqualToOrForced(final InetSocketAddress socketAddress, final ProtocolVersion version) {
        if (forcedVersions.containsKey(socketAddress)) return forcedVersions.get(socketAddress).isEqualTo(version);

        return ProtocolHack.getTargetVersion().isEqualTo(version);
    }

    public static boolean isOlderThanOrEqualToOrForced(final InetSocketAddress socketAddress, final ProtocolVersion version) {
        if (forcedVersions.containsKey(socketAddress)) return forcedVersions.get(socketAddress).isOlderThanOrEqualTo(version);

        return ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(version);
    }

    /**
     * This method adds the channel handlers required for ViaVersion, ViaBackwards, ViaAprilFools and ViaLegacy, it also tracks the Via and Minecraft Connection
     */
    public static void hookProtocolHack(final ClientConnection connection, final Channel channel, final InetSocketAddress address) {
        if (ProtocolHack.getForcedVersions().containsKey(address)) {
            channel.attr(ProtocolHack.FORCED_VERSION).set(ProtocolHack.getForcedVersions().get(address));
            ProtocolHack.getForcedVersions().remove(address);
        }
        final UserConnection user = new UserConnectionImpl(channel, true);
        channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).set(user);
        channel.attr(ProtocolHack.LOCAL_MINECRAFT_CONNECTION).set(connection);

        new ProtocolPipelineImpl(user);

        channel.pipeline().addLast(new ViaFabricPlusVLBPipeline(user, address, ProtocolHack.getTargetVersion(channel)));
    }

    /**
     * This method represents the rak net connection for bedrock edition, it's a replacement of the ClientConnection#connect method
     */
    public static void connectRakNet(final ClientConnection clientConnection, final InetSocketAddress address, final Lazy lazy, final Class channelType) {
        Bootstrap nettyBoostrap = new Bootstrap();
        nettyBoostrap = nettyBoostrap.group((EventLoopGroup) lazy.get());
        nettyBoostrap = nettyBoostrap.handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(@NotNull Channel channel) throws Exception {
                try {
                    channel.config().setOption(RakChannelOption.RAK_PROTOCOL_VERSION, 11);
                    channel.config().setOption(RakChannelOption.RAK_CONNECT_TIMEOUT, 4_000L);
                    channel.config().setOption(RakChannelOption.RAK_SESSION_TIMEOUT, 30_000L);
                    channel.config().setOption(RakChannelOption.RAK_GUID, ThreadLocalRandom.current().nextLong());
                } catch (Exception ignored) {
                }
                ChannelPipeline channelPipeline = channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
                ClientConnection.addHandlers(channelPipeline, NetworkSide.CLIENTBOUND);

                channelPipeline.addLast("packet_handler", clientConnection);

                hookProtocolHack(clientConnection, channel, address);
            }
        });
        nettyBoostrap = nettyBoostrap.channelFactory(channelType == EpollSocketChannel.class ? RakChannelFactory.client(EpollDatagramChannel.class) : RakChannelFactory.client(NioDatagramChannel.class));

        if (ProtocolHack.getRakNetPingSessions().contains(address)) {
            nettyBoostrap.bind(new InetSocketAddress(0)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE).syncUninterruptibly();
        } else {
            nettyBoostrap.connect(address.getAddress(), address.getPort()).syncUninterruptibly();
        }
    }

    public ProtocolHack() {
        ViaLoadingBase.ViaLoadingBaseBuilder builder = ViaLoadingBase.ViaLoadingBaseBuilder.create();

        builder = builder.platform(new Platform("ViaBedrock", () -> true, ViaBedrockPlatformImpl::new, protocolVersions -> protocolVersions.add(BedrockProtocolVersion.bedrockLatest)), 0);
        builder = builder.platform(new Platform("ViaLegacy", () -> true, ViaLegacyPlatformImpl::new, protocolVersions -> {
            final List<ProtocolVersion> legacyProtocols = new ArrayList<>(LegacyProtocolVersion.PROTOCOLS);
            Collections.reverse(legacyProtocols);

            legacyProtocols.remove(LegacyProtocolVersion.c0_30cpe);
            legacyProtocols.add(legacyProtocols.indexOf(LegacyProtocolVersion.c0_28toc0_30) + 1, LegacyProtocolVersion.c0_30cpe);

            protocolVersions.addAll(legacyProtocols);
        }));
        builder = builder.platform(new Platform("ViaAprilFools", () -> true, ViaAprilFoolsPlatformImpl::new, protocolVersions -> {
            protocolVersions.add(protocolVersions.indexOf(ProtocolVersion.v1_14) + 1, AprilFoolsProtocolVersion.s3d_shareware);
            protocolVersions.add(protocolVersions.indexOf(ProtocolVersion.v1_16) + 1, AprilFoolsProtocolVersion.s20w14infinite);
            protocolVersions.add(protocolVersions.indexOf(ProtocolVersion.v1_16_2) + 1, AprilFoolsProtocolVersion.sCombatTest8c);
        }));

        builder = builder.runDirectory(ViaFabricPlus.RUN_DIRECTORY);
        builder = builder.nativeVersion(SharedConstants.getProtocolVersion());
        builder = builder.forceNativeVersionCondition(() -> {
            if (MinecraftClient.getInstance() == null) return true;

            return MinecraftClient.getInstance().isInSingleplayer();
        });
        builder = builder.providers(providers -> {
            providers.use(VersionProvider.class, new ViaFabricPlusVLBBaseVersionProvider());

            providers.use(MovementTransmitterProvider.class, new ViaFabricPlusMovementTransmitterProvider());
            providers.use(HandItemProvider.class, new ViaFabricPlusHandItemProvider());

            providers.use(CommandArgumentsProvider.class, new ViaFabricPlusCommandArgumentsProvider());

            providers.use(OldAuthProvider.class, new ViaFabricPlusOldAuthProvider());
            providers.use(ClassicWorldHeightProvider.class, new ViaFabricPlusClassicWorldHeightProvider());
            providers.use(EncryptionProvider.class, new ViaFabricPlusEncryptionProvider());
            providers.use(GameProfileFetcher.class, new ViaFabricPlusGameProfileFetcher());
            providers.use(ClassicMPPassProvider.class, new ViaFabricPlusClassicMPPassProvider());
            providers.use(ClassicCustomCommandProvider.class, new ViaFabricPlusClassicCustomCommandProvider());

            providers.use(NettyPipelineProvider.class, new ViaFabricPlusNettyPipelineProvider());
            providers.use(BlobCacheProvider.class, new ViaFabricPlusBlobCacheProvider());
        });
        builder = builder.onProtocolReload(protocolVersion -> ChangeProtocolVersionCallback.EVENT.invoker().onChangeProtocolVersion(protocolVersion));
        builder.build();

        FinishViaLoadingBaseStartupCallback.EVENT.invoker().onFinishViaLoadingBaseStartup();
    }
}
