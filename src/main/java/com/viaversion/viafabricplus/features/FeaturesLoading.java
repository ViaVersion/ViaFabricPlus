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

import com.viaversion.viafabricplus.api.LoadingCycleCallback;
import com.viaversion.viafabricplus.base.Events;
import com.viaversion.viafabricplus.base.settings.impl.VisualSettings;
import com.viaversion.viafabricplus.features.block.CollisionShapes;
import com.viaversion.viafabricplus.features.cpe_extensions.CPEAdditions;
import com.viaversion.viafabricplus.features.footstep_particle.FootStepParticle1_12_2;
import com.viaversion.viafabricplus.features.networking.resource_pack_header.ResourcePackHeaderDiff;
import com.viaversion.viafabricplus.features.recipe_emulation.Recipes1_11_2;
import com.viaversion.viafabricplus.features.filter_non_existing_characters.UnicodeFontFix1_12_2;
import com.viaversion.viafabricplus.features.ui.armor_hud.ArmorHudEmulation1_8;
import com.viaversion.viafabricplus.features.ui.classic_creative_menu.GridItemSelectionScreen;
import com.viaversion.viafabricplus.features.entity.EntityDimensionDiff;
import com.viaversion.viafabricplus.features.entity.enchantment_attributes.EnchantmentAttributesEmulation1_20_6;
import com.viaversion.viafabricplus.util.DataCustomPayload;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.raphimc.viaaprilfools.api.AprilFoolsProtocolVersion;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class FeaturesLoading {

    static {
        ResourcePackHeaderDiff.init();
        CPEAdditions.init();
        DataCustomPayload.init();
        UnicodeFontFix1_12_2.init();
        FootStepParticle1_12_2.init();

        Events.LOADING_CYCLE.register(cycle -> {
            if (cycle == LoadingCycleCallback.LoadingCycle.POST_GAME_LOAD) {
                EntityDimensionDiff.init();
                EnchantmentAttributesEmulation1_20_6.init();
                ArmorHudEmulation1_8.init();
            }
        });

        Events.CHANGE_PROTOCOL_VERSION.register((oldVersion, newVersion) -> MinecraftClient.getInstance().execute(() -> {
            VisualSettings.global().filterNonExistingGlyphs.onValueChanged();
            CollisionShapes.reloadBlockShapes();

            if (newVersion.olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
                GridItemSelectionScreen.INSTANCE.itemGrid = null;
            }
            if (newVersion.olderThanOrEqualTo(ProtocolVersion.v1_11_1)) {
                Recipes1_11_2.reset();
            }
            if (oldVersion.equals(AprilFoolsProtocolVersion.s3d_shareware) || newVersion.equals(AprilFoolsProtocolVersion.s3d_shareware)) {
                MinecraftClient.getInstance().getSoundManager().reloadSounds();
            }
        }));
    }

    public static void init() {
        // Calls the static block
    }

}
