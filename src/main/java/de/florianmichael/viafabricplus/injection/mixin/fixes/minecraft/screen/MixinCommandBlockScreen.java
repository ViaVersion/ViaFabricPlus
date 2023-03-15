/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.screen;

import de.florianmichael.viafabricplus.settings.groups.VisualSettings;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandBlockScreen.class)
public abstract class MixinCommandBlockScreen {

    @Shadow
    private CyclingButtonWidget<CommandBlockBlockEntity.Type> modeButton;

    @Shadow
    private CyclingButtonWidget<Boolean> conditionalModeButton;
    @Shadow
    private CyclingButtonWidget<Boolean> redstoneTriggerButton;

    @Shadow
    public abstract void updateCommandBlock();

    @Inject(method = "init", at = @At("TAIL"))
    private void injectInit(CallbackInfo ci) {
        if (VisualSettings.INSTANCE.removeNewerFeaturesFromCommandBlockScreen.getValue()) {
            modeButton.visible = false;
            conditionalModeButton.visible = false;
            redstoneTriggerButton.visible = false;

            updateCommandBlock();
        }
    }
}
