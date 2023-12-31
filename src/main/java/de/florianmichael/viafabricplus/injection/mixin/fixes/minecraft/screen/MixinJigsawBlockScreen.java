/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2024      RK_01/RaphiMC and contributors
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

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JigsawBlockScreen.class)
public abstract class MixinJigsawBlockScreen extends Screen {

    @Shadow
    private TextFieldWidget nameField;

    @Shadow
    private CyclingButtonWidget<JigsawBlockEntity.Joint> jointRotationButton;

    @Shadow
    private TextFieldWidget targetField;

    @Shadow
    private TextFieldWidget selectionPriorityField;

    @Shadow
    private TextFieldWidget placementPriorityField;

    public MixinJigsawBlockScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void disableWidgets(CallbackInfo ci) {
        if (VisualSettings.global().removeNewerFeaturesFromJigsawScreen.isEnabled()) {
            if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_15_2)) {
                nameField.active = false;
                jointRotationButton.active = false;
                int index = children().indexOf(jointRotationButton);
                ((ClickableWidget) children().get(index + 1)).active = false; // levels slider
                ((ClickableWidget) children().get(index + 2)).active = false; // keep jigsaws toggle
                ((ClickableWidget) children().get(index + 3)).active = false; // generate button
            }

            selectionPriorityField.active = false;
            placementPriorityField.active = false;
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void copyText(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (VisualSettings.global().removeNewerFeaturesFromJigsawScreen.isEnabled() && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_15_2)) {
            nameField.setText(targetField.getText());
        }
    }

}
