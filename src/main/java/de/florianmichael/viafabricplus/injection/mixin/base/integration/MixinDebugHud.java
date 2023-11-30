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

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.fixes.tracker.JoinGameTracker;
import de.florianmichael.viafabricplus.injection.ViaFabricPlusMixinPlugin;
import de.florianmichael.viafabricplus.injection.access.IBlobCache;
import de.florianmichael.viafabricplus.injection.access.IChunkTracker;
import de.florianmichael.viafabricplus.injection.access.IRakSessionCodec;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.provider.viabedrock.ViaFabricPlusBlobCacheProvider;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import de.florianmichael.viafabricplus.util.ChatUtil;
import de.florianmichael.viafabricplus.util.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import net.raphimc.viabedrock.protocol.data.enums.bedrock.ServerMovementModes;
import net.raphimc.viabedrock.protocol.providers.BlobCacheProvider;
import net.raphimc.viabedrock.protocol.storage.BlobCache;
import net.raphimc.viabedrock.protocol.storage.ChunkTracker;
import net.raphimc.viabedrock.protocol.storage.GameSessionStorage;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.storage.ExtensionProtocolMetadataStorage;
import net.raphimc.vialegacy.protocols.release.protocol1_2_1_3to1_1.storage.SeedStorage;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.storage.EntityTracker;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.handler.codec.raknet.common.RakSessionCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
@Mixin(DebugHud.class)
public abstract class MixinDebugHud {

    @Inject(method = "getLeftText", at = @At("RETURN"))
    public void addViaFabricPlusInformation(CallbackInfoReturnable<List<String>> cir) {
        if (!GeneralSettings.global().showExtraInformationInDebugHud.getValue()) {
            return;
        }
        if (MinecraftClient.getInstance().isInSingleplayer() && MinecraftClient.getInstance().player != null) {
            return;
        }
        final UserConnection userConnection = ProtocolHack.getPlayNetworkUserConnection();
        if (userConnection == null) { // Via is not translating this session
            return;
        }

        final List<String> information = new ArrayList<>();
        information.add("");

        // Title
        information.add(ChatUtil.PREFIX + Formatting.RESET + " " + ViaFabricPlusMixinPlugin.VFP_VERSION);

        // common
        final ProtocolInfo info = userConnection.getProtocolInfo();
        information.add(
                "P: " + info.getPipeline().pipes().size() +
                        " C: " + ProtocolVersion.getProtocol(info.getProtocolVersion()) +
                        " S: " + ProtocolVersion.getProtocol(info.getServerProtocolVersion())
        );

        // r1_7_10
        final EntityTracker entityTracker1_7_10 = userConnection.get(EntityTracker.class);
        if (entityTracker1_7_10 != null) {
            information.add(
                    "1.7 Entities: " + entityTracker1_7_10.getTrackedEntities().size() +
                            ", Virtual holograms: " + entityTracker1_7_10.getVirtualHolograms().size()
            );
        }

        // r1_1
        final SeedStorage seedStorage = userConnection.get(SeedStorage.class);
        if (seedStorage != null) {
            information.add("World Seed: " + seedStorage.seed);
        }

        // c0.30cpe
        final ExtensionProtocolMetadataStorage extensionProtocolMetadataStorage = userConnection.get(ExtensionProtocolMetadataStorage.class);
        if (extensionProtocolMetadataStorage != null) {
            information.add("CPE extensions: " + extensionProtocolMetadataStorage.getExtensionCount());
        }

        // bedrock
        final JoinGameTracker joinGameTracker = userConnection.get(JoinGameTracker.class);
        if (joinGameTracker != null) {
            final int movementMode = userConnection.get(GameSessionStorage.class).getMovementMode();
            String movement = "Server with rewind";
            if (movementMode == ServerMovementModes.CLIENT) {
                movement = "Client";
            } else if (movementMode == ServerMovementModes.SERVER) {
                movement = "Server";
            }

            information.add("Bedrock Level: " + joinGameTracker.getLevelId() + ", Enchantment Seed: " + joinGameTracker.getEnchantmentSeed() + ", Movement: " + movement);
        }
        if (joinGameTracker != null) {
            information.add("World Seed: " + joinGameTracker.getSeed());
        }
        final BlobCache blobCache = userConnection.get(BlobCache.class);
        if (blobCache != null) {
            final var blobCacheProvider = (ViaFabricPlusBlobCacheProvider) Via.getManager().getProviders().get(BlobCacheProvider.class);

            final long totalSize = blobCacheProvider.getSize();
            final int blobCount = blobCacheProvider.getBlobs().size();
            final int pendingCount = ((IBlobCache) blobCache).viaFabricPlus$getPending().size();

            information.add("Blob Cache: S: " + StringUtil.formatBytes(totalSize) + ", C: " + blobCount + ", P: " + pendingCount);
        }
        final ChunkTracker chunkTracker = userConnection.get(ChunkTracker.class);
        if (chunkTracker != null) {
            final int subChunkRequests = ((IChunkTracker) chunkTracker).viaFabricPlus$getSubChunkRequests();
            final int pendingSubChunks = ((IChunkTracker) chunkTracker).viaFabricPlus$getPendingSubChunks();
            final int chunks = ((IChunkTracker) chunkTracker).viaFabricPlus$getChunks();
            cir.getReturnValue().add("Chunk Tracker: R: " + subChunkRequests + ", P: " + pendingSubChunks + ", C: " + chunks);
        }
        if (userConnection.getChannel() instanceof RakClientChannel rakClientChannel) {
            final RakSessionCodec rakSessionCodec = rakClientChannel.parent().pipeline().get(RakSessionCodec.class);
            if (rakSessionCodec != null) {
                final int transmitQueue = ((IRakSessionCodec) rakSessionCodec).viaFabricPlus$getOutgoingPackets();
                final int retransmitQueue = ((IRakSessionCodec) rakSessionCodec).viaFabricPlus$SentDatagrams();
                cir.getReturnValue().add("RTT: " + Math.round(rakSessionCodec.getRTT()) + " ms, P: " + rakSessionCodec.getPing() + " ms" + ", TQ: " + transmitQueue + ", RTQ: " + retransmitQueue);
            }
        }

        information.add("");

        cir.getReturnValue().addAll(information);
    }
}
