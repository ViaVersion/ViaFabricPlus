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

package com.viaversion.viafabricplus.injection.mixin.features.v26_2;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelEventHandler.class)
public abstract class MixinLevelEventHandler {

    @Shadow
    public abstract void levelEvent(int eventType, BlockPos pos, int data);

    @Inject(method = "levelEvent", at = @At("HEAD"))
    private void playPotionSplashSound(int eventType, BlockPos pos, int data, CallbackInfo ci) {
        // 26.3 servers send the splash sound as a separate level event, older servers relied on the particle event playing it
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v26_2)) {
            if (eventType == LevelEvent.PARTICLES_SPELL_POTION_SPLASH) {
                this.levelEvent(LevelEvent.SOUND_SPELL_POTION_SPLASH, pos, data);
            } else if (eventType == LevelEvent.PARTICLES_INSTANT_POTION_SPLASH) {
                this.levelEvent(LevelEvent.SOUND_INSTANT_POTION_SPLASH, pos, data);
            }
        }
    }

}
