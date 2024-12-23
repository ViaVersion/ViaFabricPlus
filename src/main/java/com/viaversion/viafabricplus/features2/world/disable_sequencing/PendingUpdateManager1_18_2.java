/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.features2.world.disable_sequencing;

import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

/**
 * No-op implementation of {@link PendingUpdateManager} for 1.18.2 and lower since those versions don't have the
 * {@link PendingUpdateManager} class.
 */
public class PendingUpdateManager1_18_2 extends PendingUpdateManager {

    @Override
    public void addPendingUpdate(BlockPos pos, BlockState state, ClientPlayerEntity player) {
    }

    @Override
    public boolean hasPendingUpdate(BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void processPendingUpdates(int maxProcessableSequence, ClientWorld world) {
    }

    @Override
    public PendingUpdateManager incrementSequence() {
        return this;
    }

    @Override
    public void close() {
    }

    @Override
    public int getSequence() {
        return 0;
    }

    @Override
    public boolean hasPendingSequence() {
        return false;
    }

}
