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

import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.raphimc.vialoader.util.VersionEnum;

public class BlockFixes {

    private static final float DEFAULT_SOUL_SAND_VELOCITY_MULTIPLIER = Blocks.SOUL_SAND.getVelocityMultiplier();
    private static final float _1_14_4_SOUL_SAND_VELOCITY_MULTIPLIER = 1F;

    public static void init() {
        ChangeProtocolVersionCallback.EVENT.register((oldVersion, newVersion) -> MinecraftClient.getInstance().execute(() -> {
            // Soul sand velocity multiplier
            if (isNewerThan(oldVersion, newVersion, VersionEnum.r1_14_4)) {
                Blocks.SOUL_SAND.velocityMultiplier = DEFAULT_SOUL_SAND_VELOCITY_MULTIPLIER;
            }
            if (isOlderThanOrEqualTo(oldVersion, newVersion, VersionEnum.r1_14_4)) {
                Blocks.SOUL_SAND.velocityMultiplier = _1_14_4_SOUL_SAND_VELOCITY_MULTIPLIER;
            }

            // Reloads all bounding boxes
            for (Block block : Registries.BLOCK) {
                if (block instanceof AnvilBlock || block instanceof BedBlock || block instanceof BrewingStandBlock
                        || block instanceof CarpetBlock || block instanceof CauldronBlock || block instanceof ChestBlock
                        || block instanceof EnderChestBlock || block instanceof EndPortalBlock || block instanceof EndPortalFrameBlock
                        || block instanceof FarmlandBlock || block instanceof FenceBlock || block instanceof FenceGateBlock
                        || block instanceof HopperBlock || block instanceof LadderBlock || block instanceof LeavesBlock
                        || block instanceof LilyPadBlock || block instanceof PaneBlock || block instanceof PistonBlock
                        || block instanceof PistonHeadBlock || block instanceof SnowBlock || block instanceof WallBlock
                        || block instanceof CropBlock || block instanceof FlowerbedBlock
                ) {
                    for (BlockState state : block.getStateManager().getStates()) {
                        state.initShapeCache();
                    }
                }
            }
        }));
    }

    private static boolean isOlderThanOrEqualTo(final VersionEnum oldVersion, final VersionEnum newVersion, final VersionEnum toCheck) {
        return oldVersion.isNewerThan(toCheck) && newVersion.isOlderThanOrEqualTo(toCheck);
    }

    private static boolean isNewerThan(final VersionEnum oldVersion, final VersionEnum newVersion, final VersionEnum toCheck) {
        return newVersion.isNewerThan(toCheck) && oldVersion.isOlderThanOrEqualTo(toCheck);
    }

    private static boolean didCrossBoundary(final VersionEnum oldVersion, final VersionEnum newVersion, final VersionEnum toCheck) {
        return isNewerThan(oldVersion, newVersion, toCheck) || isOlderThanOrEqualTo(oldVersion, newVersion, toCheck);
    }

}
