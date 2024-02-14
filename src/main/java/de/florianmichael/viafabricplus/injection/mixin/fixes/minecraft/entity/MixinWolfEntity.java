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
import de.florianmichael.viafabricplus.fixes.tracker.WolfHealthTracker;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class MixinWolfEntity extends TameableEntity implements Angerable {

    @Shadow
    public abstract DyeColor getCollarColor();

    @Shadow
    public abstract void setCollarColor(DyeColor color);

    protected MixinWolfEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void fixWolfInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolHack.getTargetVersion().olderThanOrEquals(ProtocolVersion.v1_14_4)) {
            final ItemStack itemStack = player.getStackInHand(hand);
            final Item item = itemStack.getItem();
            if (this.isTamed()) {
                if (item.isFood()) {
                    if (item.getFoodComponent().isMeat() && this.viaFabricPlus$getWolfHealth() < 20.0F) {
                        if (!player.getAbilities().creativeMode) itemStack.decrement(1);
                        this.heal((float) item.getFoodComponent().getHunger());
                        cir.setReturnValue(ActionResult.SUCCESS);
                        return;
                    }
                } else if (item instanceof DyeItem dyeItem) {
                    final DyeColor dyeColor = dyeItem.getColor();
                    if (dyeColor != this.getCollarColor()) {
                        this.setCollarColor(dyeColor);
                        if (!player.getAbilities().creativeMode) itemStack.decrement(1);
                        cir.setReturnValue(ActionResult.SUCCESS);
                        return;
                    }
                }
            } else if (item == Items.BONE && !this.hasAngerTime()) {
                if (!player.getAbilities().creativeMode) itemStack.decrement(1);
                cir.setReturnValue(ActionResult.SUCCESS);
                return;
            }

            cir.setReturnValue(super.interactMob(player, hand));
        }
    }

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/WolfEntity;getHealth()F"))
    private float fixWolfHealth(WolfEntity instance) {
        if (ProtocolHack.getTargetVersion().olderThanOrEquals(ProtocolVersion.v1_14_4)) {
            return this.viaFabricPlus$getWolfHealth();
        } else {
            return instance.getHealth();
        }
    }

    @Unique
    private float viaFabricPlus$getWolfHealth() {
        return WolfHealthTracker.get(ProtocolHack.getPlayNetworkUserConnection()).getWolfHealth(this.getId(), this.getHealth());
    }

}
