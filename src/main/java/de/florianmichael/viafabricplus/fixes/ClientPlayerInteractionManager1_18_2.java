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

package de.florianmichael.viafabricplus.fixes;

import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.util.BlockStateTranslator;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.Objects;
import java.util.function.Consumer;

public class ClientPlayerInteractionManager1_18_2 {
    public static final Consumer<PacketByteBuf> OLD_PACKET_HANDLER = data -> {
        try {
            final var pos = data.readBlockPos();
            final var blockState = Block.STATE_IDS.get(BlockStateTranslator.translateBlockState1_18(data.readVarInt()));
            final var action = data.readEnumConstant(PlayerActionC2SPacket.Action.class);
            final var allGood = data.readBoolean();

            ClientPlayerInteractionManager1_18_2.handleBlockBreakAck(pos, blockState, action, allGood);
        } catch (Exception e) {
            ViaFabricPlus.LOGGER.error("Failed to read BlockBreakAck packet data", e);
        }
    };

    private static final Object2ObjectLinkedOpenHashMap<Pair<BlockPos, PlayerActionC2SPacket.Action>, PositionAndRotation> UN_ACKED_ACTIONS = new Object2ObjectLinkedOpenHashMap<>();

    public static void trackBlockAction(final PlayerActionC2SPacket.Action action, final BlockPos blockPos) {
        final var player = MinecraftClient.getInstance().player;
        if (player == null) return;

        var rotation = new Vec2f(player.getYaw(), player.getPitch());
        if (ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_16_2)) {
            rotation = null;
        }
        UN_ACKED_ACTIONS.put(new Pair<>(blockPos, action), new PositionAndRotation(player.getPos().x, player.getPos().y, player.getPos().z, rotation));
    }

    public static void handleBlockBreakAck(final BlockPos blockPos, final BlockState blockState, final PlayerActionC2SPacket.Action action, final boolean allGood) {
        final var player = MinecraftClient.getInstance().player;
        if (player == null) return;

        final var world = Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getWorld();

        final var next = UN_ACKED_ACTIONS.remove(new Pair<>(blockPos, action));
        final var blockStateFromPos = world.getBlockState(blockPos);

        if ((next == null || !allGood || action != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK && blockStateFromPos != blockState) && blockStateFromPos != blockState) {
            world.setBlockState(blockPos, blockState);
            if (next != null && world == player.getWorld() && player.collidesWithStateAtPos(blockPos, blockState)) {
                if (next.rotation != null) {
                    player.updatePositionAndAngles(next.x, next.y, next.z, next.rotation.x, next.rotation.y);
                } else {
                    player.updatePosition(next.x, next.y, next.z);
                }
            }
        }

        while (UN_ACKED_ACTIONS.size() >= 50) {
            ViaFabricPlus.LOGGER.error("Too many unacked block actions, dropping {}", UN_ACKED_ACTIONS.firstKey());
            UN_ACKED_ACTIONS.removeFirst();
        }
    }

    public record PositionAndRotation(double x, double y, double z, Vec2f rotation) {
    }
}
