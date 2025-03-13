///*
// * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
// * Copyright (C) 2021-2025 the original authors
// *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
// *                         - RK_01/RaphiMC
// * Copyright (C) 2023-2025 ViaVersion and contributors
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package com.viaversion.viafabricplus.injection.mixin.unapplied;
//
//import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
//import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
//import java.util.Set;
//import net.minecraft.component.type.ConsumableComponent;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.util.ActionResult;
//import net.minecraft.util.Hand;
//import net.raphimc.vialegacy.api.LegacyProtocolVersion;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
// TODO UPDATE-1.21.5
//@Mixin(ConsumableComponent.class)
//public abstract class MixinConsumableComponent {
//
//    @Unique
//    private static final Set<Item> viaFabricPlus$SWORDS = Set.of(Items.DIAMOND_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD);
//
//    @Inject(method = "consume", at = @At("HEAD"), cancellable = true)
//    private void swing(LivingEntity user, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
//        if (ProtocolTranslator.getTargetVersion().betweenInclusive(LegacyProtocolVersion.b1_8tob1_8_1, ProtocolVersion.v1_8) && viaFabricPlus$SWORDS.contains(stack.getItem())) {
//            user.setCurrentHand(hand);
//            cir.setReturnValue(ActionResult.SUCCESS);
//        }
//    }
//
//}
