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

package com.viaversion.viafabricplus.injection.mixin.features.remove_newer_screen_features;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JigsawBlockEditScreen.class)
public abstract class MixinJigsawBlockEditScreen extends Screen {

    @Shadow
    private EditBox nameEdit;

    @Shadow
    private CycleButton<JigsawBlockEntity.JointType> jointButton;

    @Shadow
    private EditBox targetEdit;

    @Shadow
    private EditBox selectionPriorityEdit;

    @Shadow
    private EditBox placementPriorityEdit;

    public MixinJigsawBlockEditScreen(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void disableWidgets(CallbackInfo ci) {
        if (!DebugSettings.INSTANCE.hideModernJigsawScreenFeatures.getValue()) {
            return;
        }

        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_2)) {
            selectionPriorityEdit.active = false;
            placementPriorityEdit.active = false;
        }
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            nameEdit.active = false;
            jointButton.active = false;
            int index = children().indexOf(jointButton);
            ((AbstractWidget) children().get(index + 1)).active = false; // levels slider
            ((AbstractWidget) children().get(index + 2)).active = false; // keep jigsaws toggle
            ((AbstractWidget) children().get(index + 3)).active = false; // generate button
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void copyText(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (DebugSettings.INSTANCE.hideModernJigsawScreenFeatures.getValue() && ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            nameEdit.setValue(targetEdit.getValue());
        }
    }

}
