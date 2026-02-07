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

package com.viaversion.viafabricplus.features.interaction.r1_18_2_block_ack_emulation;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public final class ClientPlayerInteractionManager1_18_2 {

    private final Object2ObjectLinkedOpenHashMap<Pair<BlockPos, ServerboundPlayerActionPacket.Action>, Pair<Vec3, Vec2>> unAckedActions = new Object2ObjectLinkedOpenHashMap<>();

    public void trackPlayerAction(final ServerboundPlayerActionPacket.Action action, final BlockPos blockPos) {
        final LocalPlayer player = Minecraft.getInstance().player;

        final Vec2 rotation;
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_16_1)) {
            rotation = null;
        } else {
            rotation = new Vec2(player.getYRot(), player.getXRot());
        }
        unAckedActions.put(Pair.of(blockPos, action), Pair.of(player.position(), rotation));
    }

    public void handleBlockBreakAck(final BlockPos blockPos, final BlockState expectedState, final ServerboundPlayerActionPacket.Action action, final boolean allGood) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        final ClientLevel world = Minecraft.getInstance().getConnection().getLevel();

        final Pair<Vec3, Vec2> oldPlayerState = unAckedActions.remove(Pair.of(blockPos, action));
        final BlockState actualState = world.getBlockState(blockPos);

        if ((oldPlayerState == null || !allGood || action != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK && actualState != expectedState) && (actualState != expectedState || ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2))) {
            world.setBlock(blockPos, expectedState, Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
            if (oldPlayerState != null && (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_16_1) || (world == player.level() && player.isColliding(blockPos, expectedState)))) {
                final Vec3 oldPlayerPosition = oldPlayerState.getKey();
                if (oldPlayerState.getValue() != null) {
                    player.absSnapTo(oldPlayerPosition.x, oldPlayerPosition.y, oldPlayerPosition.z, oldPlayerState.getValue().x, oldPlayerState.getValue().y);
                } else {
                    player.absSnapTo(oldPlayerPosition.x, oldPlayerPosition.y, oldPlayerPosition.z);
                }
            }
        }

        while (unAckedActions.size() >= 50) {
            ViaFabricPlusImpl.INSTANCE.getLogger().warn("Too many unacked block actions, dropping {}", unAckedActions.firstKey());
            unAckedActions.removeFirst();
        }
    }

}
