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

package com.viaversion.viafabricplus.features.block.interaction;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.TrapDoorBlock;

public final class Block1_14 {

    public static boolean isExceptBlockForAttachWithPiston(final Block block) {
        return block instanceof ShulkerBoxBlock
            || block instanceof LeavesBlock
            || block instanceof TrapDoorBlock
            || block instanceof StainedGlassBlock
            || block == Blocks.BEACON
            || block == Blocks.CAULDRON
            || block == Blocks.GLASS
            || block == Blocks.GLOWSTONE
            || block == Blocks.ICE
            || block == Blocks.SEA_LANTERN
            || block == Blocks.PISTON
            || block == Blocks.STICKY_PISTON
            || block == Blocks.PISTON_HEAD;
    }

}
