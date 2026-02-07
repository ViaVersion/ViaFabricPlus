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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.movement;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.connection.UserConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.data.enums.bedrock.generated.PlayerAuthInputPacket_InputData;
import net.raphimc.viabedrock.protocol.storage.EntityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    public abstract boolean isSwimming();

    @Shadow
    protected Vec3 stuckSpeedMultiplier;

    @Inject(method = "setSwimming", at = @At("HEAD"))
    private void cancelSwimming(boolean swimming, CallbackInfo ci) {
        if (!ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return;
        }

        final UserConnection connection = ProtocolTranslator.getPlayNetworkUserConnection();
        if (connection != null && swimming != isSwimming()) {
            connection.get(EntityTracker.class).getClientPlayer().addAuthInputData(swimming ? PlayerAuthInputPacket_InputData.StartSwimming : PlayerAuthInputPacket_InputData.StopSwimming);
        }
    }

    @Redirect(method = "makeStuckInBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;stuckSpeedMultiplier:Lnet/minecraft/world/phys/Vec3;"))
    private void prioritySlowestMovementMultiplier(Entity instance, Vec3 value) {
        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) && this.stuckSpeedMultiplier != Vec3.ZERO) {
            this.stuckSpeedMultiplier = new Vec3(Math.min(this.stuckSpeedMultiplier.x, value.x), Math.min(this.stuckSpeedMultiplier.y, value.y), Math.min(this.stuckSpeedMultiplier.z, value.z));
        } else {
            this.stuckSpeedMultiplier = value;
        }
    }

}
