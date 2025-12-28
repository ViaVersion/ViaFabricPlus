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

package com.viaversion.viafabricplus.injection.mixin.features.remove_newer_screen_features;

import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.components.CycleButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandBlockEditScreen.class)
public abstract class MixinCommandBlockEditScreen {

    @Shadow
    private CycleButton<CommandBlockEntity.Mode> modeButton;

    @Shadow
    private CycleButton<Boolean> conditionalButton;

    @Shadow
    private CycleButton<Boolean> autoexecButton;

    @Shadow
    public abstract void updateGui();

    @Inject(method = "init", at = @At("TAIL"))
    private void removeWidgets(CallbackInfo ci) {
        if (DebugSettings.INSTANCE.hideModernCommandBlockScreenFeatures.isEnabled()) {
            modeButton.visible = false;
            conditionalButton.visible = false;
            autoexecButton.visible = false;

            updateGui();
        }
    }

}
