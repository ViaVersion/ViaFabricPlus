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
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PlayerLookTargetProvider;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.provider.PlayerAbilitiesProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.definition.v1_19_0.provider.CommandArgumentsProvider;
import de.florianmichael.viafabricplus.base.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.base.event.FinishViaLoadingBaseStartupCallback;
import de.florianmichael.viafabricplus.base.event.ViaLoadingBaseBuilderCallback;
import de.florianmichael.viafabricplus.protocolhack.command.ViaFabricPlusVLBViaCommandHandler;
import de.florianmichael.viafabricplus.protocolhack.netty.ViaFabricPlusVLBPipeline;
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
import de.florianmichael.viafabricplus.protocolhack.provider.viaversion.ViaFabricPlusPlayerAbilitiesProvider;
import de.florianmichael.viafabricplus.protocolhack.provider.viaversion.ViaFabricPlusPlayerLookTargetProvider;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.model.ComparableProtocolVersion;
import de.florianmichael.vialoadingbase.model.Platform;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.raphimc.viaaprilfools.api.AprilFoolsProtocolVersion;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.providers.BlobCacheProvider;
import net.raphimc.viabedrock.protocol.providers.NettyPipelineProvider;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicWorldHeightProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.providers.EncryptionProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;

import java.net.InetSocketAddress;
import java.util.*;

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

    public static void injectVLBPipeline(final ClientConnection connection, final Channel channel, final InetSocketAddress address) {
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

    private static void initCommands() {
        // Adding ViaVersion commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            final ViaFabricPlusVLBViaCommandHandler commandHandler = (ViaFabricPlusVLBViaCommandHandler) Via.getManager().getCommandHandler();

            final RequiredArgumentBuilder<FabricClientCommandSource, String> executor = RequiredArgumentBuilder.
                    <FabricClientCommandSource, String>argument("args", StringArgumentType.greedyString()).executes(commandHandler::execute).suggests(commandHandler::suggestion);

            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("viaversion").then(executor).executes(commandHandler::execute));
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("viafabricplus").then(executor).executes(commandHandler::execute));
        });
    }

    public static void init() {
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
            providers.use(PlayerLookTargetProvider.class, new ViaFabricPlusPlayerLookTargetProvider());
            providers.use(PlayerAbilitiesProvider.class, new ViaFabricPlusPlayerAbilitiesProvider());

            providers.use(CommandArgumentsProvider.class, new ViaFabricPlusCommandArgumentsProvider());

            providers.use(OldAuthProvider.class, new ViaFabricPlusOldAuthProvider());
            providers.use(ClassicWorldHeightProvider.class, new ViaFabricPlusClassicWorldHeightProvider());
            providers.use(EncryptionProvider.class, new ViaFabricPlusEncryptionProvider());
            providers.use(GameProfileFetcher.class, new ViaFabricPlusGameProfileFetcher());
            providers.use(ClassicMPPassProvider.class, new ViaFabricPlusClassicMPPassProvider());

            providers.use(NettyPipelineProvider.class, new ViaFabricPlusNettyPipelineProvider());
            providers.use(BlobCacheProvider.class, new ViaFabricPlusBlobCacheProvider());
        });
        builder = builder.onProtocolReload(protocolVersion -> ChangeProtocolVersionCallback.EVENT.invoker().onChangeProtocolVersion(protocolVersion));
        builder = builder.dumpSupplier(() -> {
            JsonObject platformSpecific = new JsonObject();
            JsonArray mods = new JsonArray();
            FabricLoader.getInstance().getAllMods().stream().map((mod) -> {
                JsonObject jsonMod = new JsonObject();
                jsonMod.addProperty("id", mod.getMetadata().getId());
                jsonMod.addProperty("name", mod.getMetadata().getName());
                jsonMod.addProperty("version", mod.getMetadata().getVersion().getFriendlyString());
                JsonArray authors = new JsonArray();
                mod.getMetadata().getAuthors().stream().map(it -> {
                    JsonObject info = new JsonObject();
                    JsonObject contact = new JsonObject();
                    it.getContact().asMap().forEach(contact::addProperty);

                    if (contact.size() != 0) info.add("contact", contact);
                    info.addProperty("name", it.getName());

                    return info;
                }).forEach(authors::add);
                jsonMod.add("authors", authors);

                return jsonMod;
            }).forEach(mods::add);

            platformSpecific.add("mods", mods);
            platformSpecific.addProperty("native version", SharedConstants.getProtocolVersion());
            return platformSpecific;
        });
        builder = builder.managerBuilderConsumer(viaManagerBuilder -> viaManagerBuilder.commandHandler(new ViaFabricPlusVLBViaCommandHandler()));

        ViaLoadingBaseBuilderCallback.EVENT.invoker().onBuildViaLoadingBase(builder);
        builder.build();
        initCommands();

        FinishViaLoadingBaseStartupCallback.EVENT.invoker().onFinishViaLoadingBaseStartup();
    }
}
