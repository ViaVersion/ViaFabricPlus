/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.base.settings.groups.VisualSettings;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantValue")
@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    @Unique
    private final static EntityDimensions viafabricplus_sneaking_dimensions_v1_13_2 = EntityDimensions.changing(0.6f, 1.65f);

    @Unique
    private final static SoundEvent viafabricplus_random_hurt = SoundEvent.of(new Identifier("viafabricplus", "random.hurt"));

    @Shadow
    @Final
    private PlayerAbilities abilities;

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void onUpdatePose(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            EntityPose pose;

            if (isFallFlying())
                pose = EntityPose.FALL_FLYING;
            else if (isSleeping())
                pose = EntityPose.SLEEPING;
            else if (isSwimming())
                pose = EntityPose.SWIMMING;
            else if (isUsingRiptide())
                pose = EntityPose.SPIN_ATTACK;
            else if (isSneaking() && !abilities.flying)
                pose = EntityPose.CROUCHING;
            else
                pose = EntityPose.STANDING;

            this.setPose(pose);
            ci.cancel();
        }
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void onGetDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> ci) {
        if (pose == EntityPose.CROUCHING) {
            if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
                ci.setReturnValue(PlayerEntity.STANDING_DIMENSIONS);
            } else if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2) || ProtocolHack.getTargetVersion().isEqualTo(BedrockProtocolVersion.bedrockLatest)) {
                ci.setReturnValue(viafabricplus_sneaking_dimensions_v1_13_2);
            }
        }
    }

    @Inject(method = "getAttackCooldownProgress", at = @At("HEAD"), cancellable = true)
    private void injectGetAttackCooldownProgress(CallbackInfoReturnable<Float> ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ci.setReturnValue(1f);
        }
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    public void replaceSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        if (VisualSettings.INSTANCE.replaceHurtSoundWithOOFSound.getValue()) {
            cir.setReturnValue(viafabricplus_random_hurt);
        }
    }

    @ModifyConstant(method = "getActiveEyeHeight", constant = @Constant(floatValue = 1.27f))
    private float modifySneakEyeHeight(float prevEyeHeight) {
        if (ProtocolHack.getTargetVersion().isNewerThan(ProtocolVersion.v1_13_2)) {
            return prevEyeHeight;
        } else {
            return 1.54f;
        }
    }

    @Unique
    public boolean viafabricplus_isSprinting;

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setMovementSpeed(F)V"))
    public void trackOldField(CallbackInfo ci) {
        viafabricplus_isSprinting = this.isSprinting();
    }

    @Redirect(method = "getOffGroundSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSprinting()Z"))
    public boolean useOldField(PlayerEntity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_3)) {
            return viafabricplus_isSprinting;
        }
        return instance.isSprinting();
    }
}
