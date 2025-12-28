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

package com.viaversion.viafabricplus.visuals.injection.mixin.oof_sound;

import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer {

    @Unique
    private static final SoundEvent viaFabricPlusVisuals$oof_hurt = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("viafabricplus-visuals", "oof.hurt"));

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void replaceSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        if (VisualSettings.INSTANCE.replaceHurtSoundWithOOFSound.isEnabled()) {
            cir.setReturnValue(viaFabricPlusVisuals$oof_hurt);
        }
    }

}
