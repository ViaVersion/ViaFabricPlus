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

package com.viaversion.viafabricplus.features;

import com.viaversion.viaaprilfools.api.AprilFoolsProtocolVersion;
import com.viaversion.viafabricplus.api.events.LoadingCycleCallback;
import com.viaversion.viafabricplus.base.Events;
import com.viaversion.viafabricplus.features.emulation.armor_hud.ArmorHudEmulation1_8;
import com.viaversion.viafabricplus.features.block.shape.CollisionShapes;
import com.viaversion.viafabricplus.features.classic.cpe_extension.CPEAdditions;
import com.viaversion.viafabricplus.features.entity.EntityDimensionDiff;
import com.viaversion.viafabricplus.features.entity.attribute.EnchantmentAttributesEmulation1_20_6;
import com.viaversion.viafabricplus.features.footstep_particle.FootStepParticle1_12_2;
import com.viaversion.viafabricplus.features.networking.resource_pack_header.ResourcePackHeaderDiff;
import com.viaversion.viafabricplus.features.emulation.recipe.Recipes1_11_2;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.MinecraftClient;

public final class FeaturesLoading {

    static {
        // Check if the pack format mappings are correct
        ResourcePackHeaderDiff.init();

        // Register additional CPE features
        CPEAdditions.init();

        // Register the footstep particle
        FootStepParticle1_12_2.init();

        Events.LOADING_CYCLE.register(cycle -> {
            if (cycle == LoadingCycleCallback.LoadingCycle.POST_GAME_LOAD) {
                // Handle clientside enchantment calculations in <= 1.20.6
                EnchantmentAttributesEmulation1_20_6.init();

                // Handles and updates entity dimension changes in <= 1.17
                EntityDimensionDiff.init();

                // Ticks the armor hud manually in <= 1.8.x
                ArmorHudEmulation1_8.init();
            }
        });

        // Reloads some clientside stuff when the protocol version changes
        Events.CHANGE_PROTOCOL_VERSION.register((oldVersion, newVersion) -> MinecraftClient.getInstance().execute(() -> {
            // Reloads all bounding boxes of the blocks that we changed
            CollisionShapes.reloadBlockShapes();

            // Reloads the clientside recipes
            if (newVersion.olderThanOrEqualTo(ProtocolVersion.v1_11_1)) {
                Recipes1_11_2.reset();
            }

            // Reload sound system when switching between 3D Shareware and normal versions
            if (oldVersion.equals(AprilFoolsProtocolVersion.s3d_shareware) || newVersion.equals(AprilFoolsProtocolVersion.s3d_shareware)) {
                MinecraftClient.getInstance().getSoundManager().reloadSounds();
            }
        }));
    }

    public static void init() {
        // Calls the static block
    }

}
