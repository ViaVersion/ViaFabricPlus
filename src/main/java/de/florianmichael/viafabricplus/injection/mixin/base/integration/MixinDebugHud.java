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

import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.fixes.viaversion.BedrockJoinGameTracker;
import de.florianmichael.viafabricplus.injection.ViaFabricPlusMixinPlugin;
import de.florianmichael.viafabricplus.injection.access.IChunkTracker;
import de.florianmichael.viafabricplus.injection.access.IRakSessionCodec;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import de.florianmichael.viafabricplus.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import net.raphimc.viabedrock.protocol.storage.ChunkTracker;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.storage.ExtensionProtocolMetadataStorage;
import net.raphimc.vialegacy.protocol.release.r1_1tor1_2_1_3.storage.SeedStorage;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.storage.EntityTracker;
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
        if (!GeneralSettings.global().showExtraInformationInDebugHud.getValue()) { // Only show if enabled
            return;
        }
        if (MinecraftClient.getInstance().isInSingleplayer() && MinecraftClient.getInstance().player != null) { // Don't show in singleplayer
            return;
        }
        final UserConnection userConnection = ProtocolTranslator.getPlayNetworkUserConnection();
        if (userConnection == null) { // Only show if ViaVersion is active
            return;
        }

        final List<String> information = new ArrayList<>();
        information.add("");

        // Title
        information.add(ChatUtil.PREFIX + Formatting.RESET + " " + ViaFabricPlusMixinPlugin.VFP_VERSION);

        // Connection
        final ProtocolInfo info = userConnection.getProtocolInfo();
        information.add("P: " + info.getPipeline().pipes().size() + " C: " + info.protocolVersion() + " S: " + info.serverProtocolVersion());

        // 1.7.10
        final EntityTracker entityTracker1_7_10 = userConnection.get(EntityTracker.class);
        if (entityTracker1_7_10 != null) {
            information.add("1.7 Entities: " + entityTracker1_7_10.getTrackedEntities().size() + ", Virtual holograms: " + entityTracker1_7_10.getVirtualHolograms().size());
        }

        // 1.1
        final SeedStorage seedStorage = userConnection.get(SeedStorage.class);
        if (seedStorage != null && userConnection.getProtocolInfo().serverProtocolVersion().newerThanOrEqualTo(LegacyProtocolVersion.a1_2_0toa1_2_1_1)) {
            information.add("World Seed: " + seedStorage.seed);
        }

        // c0.30 cpe
        final ExtensionProtocolMetadataStorage extensionProtocolMetadataStorage = userConnection.get(ExtensionProtocolMetadataStorage.class);
        if (extensionProtocolMetadataStorage != null) {
            information.add("CPE extensions: " + extensionProtocolMetadataStorage.getExtensionCount());
        }

        // Bedrock
        final BedrockJoinGameTracker joinGameDataTracker = userConnection.get(BedrockJoinGameTracker.class);
        if (joinGameDataTracker != null) {
            information.add("Bedrock Level: " + joinGameDataTracker.getLevelId() + ", Enchantment Seed: " + joinGameDataTracker.getEnchantmentSeed());
        }
        if (joinGameDataTracker != null) {
            information.add("World Seed: " + joinGameDataTracker.getSeed());
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

        cir.getReturnValue().addAll(information);
    }

}
