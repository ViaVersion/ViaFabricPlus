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

package com.viaversion.viafabricplus.injection.mixin.features.scoreboard;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerTeam.class)
public abstract class MixinTeam {

    @Shadow
    private Component playerPrefix;

    @Shadow
    private Component playerSuffix;

    @Inject(method = "getFormattedName(Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;", at = @At("HEAD"), cancellable = true)
    private void decorateName1_12_2(Component name, CallbackInfoReturnable<MutableComponent> cir) {
        // All components were legacy strings prior to 1.13, meaning their styles are not separated but used across the whole component.
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            final Style prefixStyle = viaFabricPlus$getLastStyle(this.playerPrefix);
            final Component nameWithStyle = viaFabricPlus$fillStyle(name, prefixStyle);
            final Style nameStyle = viaFabricPlus$getLastStyle(nameWithStyle);

            cir.setReturnValue(Component.empty()
                .append(this.playerPrefix)
                .append(nameWithStyle)
                .append(viaFabricPlus$fillStyle(this.playerSuffix, nameStyle))
            );
        }
    }

    @Unique
    private Style viaFabricPlus$getLastStyle(final Component text) {
        for (int i = text.getSiblings().size() - 1; i >= 0; i--) {
            final Component sibling = text.getSiblings().get(i);
            if (sibling.getStyle() != Style.EMPTY) {
                return sibling.getStyle();
            }
        }

        return text.getStyle();
    }

    @Unique
    private Component viaFabricPlus$fillStyle(final Component text, final Style style) {
        if (text.getStyle() != Style.EMPTY) {
            return text;
        } else {
            return text.copy().withStyle(style);
        }
    }

}
