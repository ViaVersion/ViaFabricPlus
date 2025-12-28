/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.visuals.injection.mixin.level_loading_transitions;

import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelLoadingScreen.class)
public abstract class MixinLevelLoadingScreen {

    @Shadow
    private LevelLoadingScreen.Reason reason;

    @Redirect(method = "renderBackground", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/LevelLoadingScreen;reason:Lnet/minecraft/client/gui/screens/LevelLoadingScreen$Reason;"))
    private LevelLoadingScreen.Reason hideDownloadTerrainScreenTransitionEffects(LevelLoadingScreen levelLoadingScreen) {
        if (VisualSettings.INSTANCE.hideDownloadTerrainScreenTransitionEffects.isEnabled()) {
            return LevelLoadingScreen.Reason.OTHER;
        } else {
            return this.reason;
        }
    }

}
