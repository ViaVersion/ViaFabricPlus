/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.base;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.injection.access.base.bedrock.IChunkTracker;
import com.viaversion.viafabricplus.injection.access.base.bedrock.IRakSessionCodec;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.protocol.storage.BedrockJoinGameTracker;
import com.viaversion.viafabricplus.util.ChatUtil;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.raphimc.viabedrock.protocol.storage.ChunkTracker;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.storage.ExtensionProtocolMetadataStorage;
import net.raphimc.vialegacy.protocol.release.r1_1tor1_2_1_3.storage.SeedStorage;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.storage.EntityTracker;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.handler.codec.raknet.common.RakSessionCodec;
import org.jetbrains.annotations.Nullable;

public final class VFPDebugHudEntry implements DebugScreenEntry {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("viafabricplus", "viafabricplus");

    @Override
    public void display(final DebugScreenDisplayer lines, @Nullable final Level world, @Nullable final LevelChunk clientChunk, @Nullable final LevelChunk chunk) {
        final List<String> information = new ArrayList<>();
        information.add(ChatUtil.PREFIX + ChatFormatting.RESET + " " + ViaFabricPlusImpl.INSTANCE.getVersion());

        final UserConnection connection = ProtocolTranslator.getPlayNetworkUserConnection();
        if (connection == null) {
            lines.addLine(information.getFirst());
            return;
        }

        final ProtocolInfo info = connection.getProtocolInfo();
        information.add("P: " + info.getPipeline().pipes().size() + " C: " + info.protocolVersion() + " S: " + info.serverProtocolVersion());
        final EntityTracker entityTracker1_7_10 = connection.get(EntityTracker.class);
        if (entityTracker1_7_10 != null) {
            information.add("1.7 Entities: " + entityTracker1_7_10.getTrackedEntities().size() + ", Virtual holograms: " + entityTracker1_7_10.getVirtualHolograms().size());
        }
        final SeedStorage seedStorage = connection.get(SeedStorage.class);
        if (seedStorage != null && connection.getProtocolInfo().serverProtocolVersion().newerThanOrEqualTo(LegacyProtocolVersion.a1_2_0toa1_2_1_1)) {
            information.add("World Seed: " + seedStorage.seed);
        }
        final ExtensionProtocolMetadataStorage extensionProtocolMetadataStorage = connection.get(ExtensionProtocolMetadataStorage.class);
        if (extensionProtocolMetadataStorage != null) {
            information.add("CPE extensions: " + extensionProtocolMetadataStorage.getExtensionCount());
        }
        final BedrockJoinGameTracker joinGameDataTracker = connection.get(BedrockJoinGameTracker.class);
        if (joinGameDataTracker != null) {
            information.add("Bedrock Level: " + joinGameDataTracker.getLevelId() + ", Enchantment Seed: " + joinGameDataTracker.getEnchantmentSeed());
        }
        if (joinGameDataTracker != null) {
            information.add("World Seed: " + joinGameDataTracker.getSeed());
        }
        final ChunkTracker chunkTracker = connection.get(ChunkTracker.class);
        if (chunkTracker != null) {
            final IChunkTracker mixinChunkTracker = (IChunkTracker) chunkTracker;
            final int subChunkRequests = mixinChunkTracker.viaFabricPlus$getSubChunkRequests();
            final int pendingSubChunks = mixinChunkTracker.viaFabricPlus$getPendingSubChunks();
            final int chunks = mixinChunkTracker.viaFabricPlus$getChunks();
            information.add("Chunk Tracker: R: " + subChunkRequests + ", P: " + pendingSubChunks + ", C: " + chunks);
        }
        if (connection.getChannel() instanceof RakClientChannel rakClientChannel) {
            final RakSessionCodec rakSessionCodec = rakClientChannel.parent().pipeline().get(RakSessionCodec.class);
            if (rakSessionCodec != null) {
                final IRakSessionCodec mixinRakSessionCodec = (IRakSessionCodec) rakSessionCodec;
                final int transmitQueue = mixinRakSessionCodec.viaFabricPlus$getOutgoingPackets();
                final int retransmitQueue = mixinRakSessionCodec.viaFabricPlus$SentDatagrams();
                information.add("RTT: " + Math.round(rakSessionCodec.getRTT()) + " ms, P: " + rakSessionCodec.getPing() + " ms" + ", TQ: " + transmitQueue + ", RTQ: " + retransmitQueue);
            }
        }

        lines.addToGroup(ID, information);
    }

    @Override
    public boolean isAllowed(final boolean reducedDebugInfo) {
        return true;
    }

}
