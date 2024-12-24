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

package com.viaversion.viafabricplus.injection.mixin.features.ui.downloading_terrain_transitions;

import com.viaversion.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DownloadingTerrainScreen.class)
public abstract class MixinDownloadingTerrainScreen {

    @Shadow @Final private DownloadingTerrainScreen.WorldEntryReason worldEntryReason;

    @Redirect(method = "renderBackground", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen;worldEntryReason:Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen$WorldEntryReason;"))
    private DownloadingTerrainScreen.WorldEntryReason hideDownloadTerrainScreenTransitionEffects(DownloadingTerrainScreen downloadingTerrainScreen) {
        if (VisualSettings.INSTANCE.hideDownloadTerrainScreenTransitionEffects.isEnabled()) {
            return DownloadingTerrainScreen.WorldEntryReason.OTHER;
        } else {
            return this.worldEntryReason;
        }
    }

}
