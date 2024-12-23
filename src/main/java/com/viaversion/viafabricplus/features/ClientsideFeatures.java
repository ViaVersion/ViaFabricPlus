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

package com.viaversion.viafabricplus.features;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.event.ChangeProtocolVersionCallback;
import com.viaversion.viafabricplus.event.PostGameLoadCallback;
import com.viaversion.viafabricplus.features.data.EntityDimensionDiff;
import com.viaversion.viafabricplus.features2.networking.resource_pack_header.ResourcePackHeaderDiff;
import com.viaversion.viafabricplus.features2.recipe_emulation.Recipes1_11_2;
import com.viaversion.viafabricplus.features.versioned.EnchantmentAttributesEmulation1_20_6;
import com.viaversion.viafabricplus.features2.cpe_extensions.CPEAdditions;
import com.viaversion.viafabricplus.features2.ui.classic_creative_menu.GridItemSelectionScreen;
import com.viaversion.viafabricplus.features2.ui.armor_hud.ArmorHudEmulation1_8;
import com.viaversion.viafabricplus.features2.footstep_particle.FootStepParticle1_12_2;
import com.viaversion.viafabricplus.features2.text_rendering.non_existing_characters.UnicodeFontFix1_12_2;
import com.viaversion.viafabricplus.settings.impl.VisualSettings;
import com.viaversion.viafabricplus.base.DataCustomPayload;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.raphimc.viaaprilfools.api.AprilFoolsProtocolVersion;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

/**
 * This class contains random fields and methods that are used to fix bugs on the client side
 */
public class ClientsideFeatures {

    static {
        // Register additional CPE features
        CPEAdditions.modifyMappings();

        // Check if the pack format mappings are correct
        ResourcePackHeaderDiff.checkOutdated();

        UnicodeFontFix1_12_2.init();

        PostGameLoadCallback.EVENT.register(() -> {
            // Handle clientside enchantment calculations in <= 1.20.6
            EnchantmentAttributesEmulation1_20_6.init();

            // Handles and updates entity dimension changes in <= 1.17
            EntityDimensionDiff.init();

            // Ticks the armor hud manually in <= 1.8.x
            ArmorHudEmulation1_8.init();
        });

        // Reloads some clientside stuff when the protocol version changes
        ChangeProtocolVersionCallback.EVENT.register((oldVersion, newVersion) -> MinecraftClient.getInstance().execute(() -> {
            VisualSettings.global().filterNonExistingGlyphs.onValueChanged();

            // Reloads all bounding boxes of the blocks that we changed
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

            // Rebuilds the item selection screen grid
            if (newVersion.olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
                GridItemSelectionScreen.INSTANCE.itemGrid = null;
            }

            // Reloads the clientside recipes
            if (newVersion.olderThanOrEqualTo(ProtocolVersion.v1_11_1)) {
                Recipes1_11_2.reset();
            }

            // Reload sound system when switching between 3D Shareware and normal versions
            if (oldVersion.equals(AprilFoolsProtocolVersion.s3d_shareware) || newVersion.equals(AprilFoolsProtocolVersion.s3d_shareware)) {
                MinecraftClient.getInstance().getSoundManager().reloadSounds();
            }
        }));

        // Register the footstep particle
        FootStepParticle1_12_2.init();

        // Register the custom payload packet for sync tasks
        DataCustomPayload.init();
    }

    public static void init() {
        // Calls the static block
    }

}
