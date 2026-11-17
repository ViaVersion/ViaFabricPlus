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

package com.viaversion.viafabricplus.injection.mixin.features.v1_13_2.movement;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Avatar.class)
public abstract class MixinAvatar extends LivingEntity {

    @Shadow
    @Final
    protected static EntityDataAccessor<HumanoidArm> DATA_PLAYER_MAIN_HAND;

    @Shadow
    @Final
    protected static EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION;

    @Unique
    private static final EntityDimensions viaFabricPlus$sneaking_dimensions_v1_13_2 = EntityDimensions.scalable(0.6F, 1.65F).withEyeHeight(1.54F).
        withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, Player.DEFAULT_VEHICLE_ATTACHMENT).attach(EntityAttachment.NAME_TAG, 0.0F, 1.4F, 0.0F));

    @Unique
    private static final EntityDimensions viaFabricPlus$sneaking_dimensions_v1_8 = EntityDimensions.scalable(0.6F, 1.8F).withEyeHeight(1.54F).
        withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, Player.DEFAULT_VEHICLE_ATTACHMENT).attach(EntityAttachment.NAME_TAG, 0.0F, 1.55F, 0.0F));

    protected MixinAvatar(final EntityType<? extends LivingEntity> entityType, final Level level) {
        super(entityType, level);
    }

    @ModifyArg(method = "defineSynchedData", at = @At(value = "INVOKE", target = "Ljava/lang/Byte;valueOf(B)Ljava/lang/Byte;"))
    private byte defaultHorizonModelCustomisation(final byte original) {
        if (ViaFabricPlus.api().targetVersion().equals(ProtocolTranslation.HORIZON_26_3)) {
            if ((Object) this instanceof LocalPlayer) {
                return viaFabricPlus$getLocalModelCustomisation();
            }
            return (byte) 0x7F;
        }
        return original;
    }

    @Inject(method = "isModelPartShown", at = @At("HEAD"))
    private void syncHorizonLocalPlayerModelParts(final PlayerModelPart part, final CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof LocalPlayer && ViaFabricPlus.api().targetVersion().equals(ProtocolTranslation.HORIZON_26_3)) {
            this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, viaFabricPlus$getLocalModelCustomisation());
        }
    }

    @Inject(method = "getMainArm", at = @At("HEAD"))
    private void syncHorizonLocalPlayerMainArm(final CallbackInfoReturnable<HumanoidArm> cir) {
        if ((Object) this instanceof LocalPlayer && ViaFabricPlus.api().targetVersion().equals(ProtocolTranslation.HORIZON_26_3)) {
            final Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.options != null) {
                this.entityData.set(DATA_PLAYER_MAIN_HAND, mc.options.mainHand().get());
            }
        }
    }

    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    private void modifyDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (pose == Pose.CROUCHING) {
            if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
                cir.setReturnValue(viaFabricPlus$sneaking_dimensions_v1_8);
            } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
                cir.setReturnValue(viaFabricPlus$sneaking_dimensions_v1_13_2);
            }
        }
    }

    @Unique
    private static byte viaFabricPlus$getLocalModelCustomisation() {
        final Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null) {
            return (byte) 0x7F;
        }
        int mask = 0;
        for (final PlayerModelPart part : PlayerModelPart.values()) {
            if (mc.options.isModelPartEnabled(part)) {
                mask |= part.getMask();
            }
        }
        return (byte) mask;
    }

}
