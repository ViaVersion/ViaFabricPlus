/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MooshroomEntity.class)
public abstract class MixinMooshroomEntity extends AnimalEntity {

    protected MixinMooshroomEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/MooshroomEntity;getStewEffectFrom(Lnet/minecraft/item/ItemStack;)Ljava/util/Optional;"), cancellable = true)
    public void checkForItemTags(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            final ItemStack itemStack = player.getStackInHand(hand);
            if (!itemStack.isIn(ItemTags.SMALL_FLOWERS)) {
                cir.setReturnValue(super.interactMob(player, hand));
            }
        }
    }

    @Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/CowEntity;interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", ordinal = 0))
    public ActionResult directPass(CowEntity instance, PlayerEntity player, Hand hand) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            return ActionResult.PASS;
        } else {
            return instance.interactMob(player, hand);
        }
    }

}
