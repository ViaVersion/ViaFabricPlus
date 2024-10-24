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

//package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.item;
//
//import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
//import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.item.MilkBucketItem;
//import net.minecraft.world.World;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(MilkBucketItem.class)
//public abstract class MixinMilkBucketItem {
//
//    @Inject(method = "finishUsing", at = @At("HEAD"), cancellable = true)
//    private void dontExchangeStack(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
//        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_5)) {
//            stack.decrementUnlessCreative(1, user);
//            cir.setReturnValue(stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack);
//        }
//    }
//
//}
