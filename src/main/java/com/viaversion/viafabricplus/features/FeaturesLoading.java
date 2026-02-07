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

package com.viaversion.viafabricplus.features;

import com.viaversion.viaaprilfools.api.AprilFoolsProtocolVersion;
import com.viaversion.viafabricplus.base.Events;
import com.viaversion.viafabricplus.features.block.connections.BlockConnectionsEmulation1_12_2;
import com.viaversion.viafabricplus.features.block.shape.CollisionShapes;
import com.viaversion.viafabricplus.features.classic.cpe_extension.CPEAdditions;
import com.viaversion.viafabricplus.features.entity.EntityDimensionDiff;
import com.viaversion.viafabricplus.features.entity.attribute.EnchantmentAttributesEmulation1_20_6;
import com.viaversion.viafabricplus.features.font.FontCacheReload;
import com.viaversion.viafabricplus.features.font.RenderableGlyphDiff;
import com.viaversion.viafabricplus.features.footstep_particle.FootStepParticle1_12_2;
import com.viaversion.viafabricplus.features.item.filter_creative_tabs.VersionedRegistries;
import com.viaversion.viafabricplus.features.networking.armor_hud.ArmorHudEmulation1_8;
import com.viaversion.viafabricplus.features.networking.resource_pack_header.ResourcePackHeaderDiff;
import com.viaversion.viafabricplus.features.recipe.Recipes1_11_2;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.world.attribute.EnvironmentAttributes;

public final class FeaturesLoading {

    // Initialize various data classes required for feature mixins
    public static void init() {
        ResourcePackHeaderDiff.init();
        RenderableGlyphDiff.init();
        FootStepParticle1_12_2.init();
        CPEAdditions.init();

        Events.CHANGE_PROTOCOL_VERSION.register((oldVersion, newVersion) -> Minecraft.getInstance().execute(() -> {
            CollisionShapes.reloadBlockShapes();

            if (oldVersion.equals(AprilFoolsProtocolVersion.s3d_shareware) || newVersion.equals(AprilFoolsProtocolVersion.s3d_shareware)) {
                Minecraft.getInstance().getSoundManager().reload();
            }

            FontCacheReload.reload();

            if (newVersion.olderThanOrEqualTo(ProtocolVersion.v1_11_1)) {
                Recipes1_11_2.reset();
            }

            EnvironmentAttributes.RESPAWN_ANCHOR_WORKS.isSyncable = newVersion.olderThanOrEqualTo(ProtocolVersion.v1_21_9);
        }));
    }

    // Make sure this is called *after* ViaVersion has been initialized
    public static void postInit() {
        VersionedRegistries.init();
        EntityDimensionDiff.init();
        EnchantmentAttributesEmulation1_20_6.init();
        BlockConnectionsEmulation1_12_2.init();
        Recipes1_11_2.init();
        ArmorHudEmulation1_8.init();
    }

}
