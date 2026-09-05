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

package com.viaversion.viafabricplus.injection.mixin.features.v1_8;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusHandItemProvider;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Objects;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void cancelOffHandItemInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8) && !InteractionHand.MAIN_HAND.equals(hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void cancelOffHandBlockPlace(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8) && !InteractionHand.MAIN_HAND.equals(hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Redirect(method = "lambda$useItem$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult eitherSuccessOrPass(ItemStack instance, Level level, Player player, InteractionHand hand, @Local(name = "itemStack") ItemStack itemStack) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            final int count = instance.getCount();

            final InteractionResult actionResult = instance.use(level, player, hand);
            final ItemStack output;
            if (actionResult instanceof InteractionResult.Success success) {
                output = Objects.requireNonNullElseGet(success.heldItemTransformedTo(), () -> player.getItemInHand(hand));
            } else {
                output = player.getItemInHand(hand);
            }

            // In <= 1.8, ActionResult weren't a thing, and interactItem simply returned either true or false
            // depending on if the input and output item are equal or not
            final boolean accepted = !output.isEmpty() && (output != itemStack || output.getCount() != count);
            if (actionResult.consumesAction() == accepted) {
                return actionResult;
            } else {
                return accepted ? InteractionResult.SUCCESS.heldItemTransformedTo(output) : InteractionResult.PASS;
            }
        } else {
            return instance.use(level, player, hand);
        }
    }

    @Inject(method = "lambda$useItem$0", at = @At("HEAD"))
    private void trackLastUsedItem(InteractionHand hand, Player player, MutableObject<InteractionResult> interactionResult, int sequence, CallbackInfoReturnable<Packet<?>> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ViaFabricPlusHandItemProvider.lastUsedItem = player.getItemInHand(hand).copy();
        }
    }

}
