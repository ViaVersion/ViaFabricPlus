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

package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.features.v1_12_2.ActionResultException1_12_2;
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusHandItemProvider;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private ClientPacketListener connection;

    @Shadow
    private GameType localPlayerMode;

    @Shadow
    protected abstract void startPrediction(final ClientLevel level, final PredictiveAction predictiveAction);

    @Shadow
    protected abstract InteractionResult performUseItemOn(final LocalPlayer player, final InteractionHand hand, final BlockHitResult blockHit);

    @Inject(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 2))
    private void interactBlock1_12_2(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            final ItemStack itemStack = player.getItemInHand(hand);
            BlockHitResult checkHitResult = blockHit;
            if (itemStack.getItem() instanceof BlockItem) {
                final BlockState clickedBlock = this.minecraft.level.getBlockState(blockHit.getBlockPos());
                if (clickedBlock.getBlock().equals(Blocks.SNOW)) {
                    if (clickedBlock.getValue(SnowLayerBlock.LAYERS) == 1) {
                        checkHitResult = blockHit.withDirection(Direction.UP);
                    }
                }
                final UseOnContext itemUsageContext = new UseOnContext(player, hand, checkHitResult);
                final BlockPlaceContext itemPlacementContext = new BlockPlaceContext(itemUsageContext);
                if (!itemPlacementContext.canPlace() || ((BlockItem) itemPlacementContext.getItemInHand().getItem()).getPlacementState(itemPlacementContext) == null) {
                    throw new ActionResultException1_12_2(InteractionResult.PASS);
                }
            }

            this.connection.send(new ServerboundUseItemOnPacket(hand, blockHit, 0));
            if (itemStack.isEmpty()) {
                throw new ActionResultException1_12_2(InteractionResult.PASS);
            }
            final UseOnContext itemUsageContext = new UseOnContext(player, hand, checkHitResult);
            InteractionResult actionResult;
            if (this.localPlayerMode.isCreative()) {
                final int count = itemStack.getCount();
                actionResult = itemStack.useOn(itemUsageContext);
                itemStack.setCount(count);
            } else {
                actionResult = itemStack.useOn(itemUsageContext);
            }
            if (!actionResult.consumesAction()) {
                actionResult = InteractionResult.PASS; // In <= 1.12.2 FAIL is the same as PASS
            }
            throw new ActionResultException1_12_2(actionResult);
        }
    }

    /**
     * @author RK_01
     * @reason Block place fix
     */
    @Overwrite
    private Packet<?> lambda$useItemOn$0(MutableObject<InteractionResult> mutableObject, LocalPlayer clientPlayerEntity, InteractionHand hand, BlockHitResult blockHitResult, int sequence) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ViaFabricPlusHandItemProvider.lastUsedItem = clientPlayerEntity.getItemInHand(hand).copy();
        }
        try {
            mutableObject.setValue(this.performUseItemOn(clientPlayerEntity, hand, blockHitResult));
            return new ServerboundUseItemOnPacket(hand, blockHitResult, sequence);
        } catch (ActionResultException1_12_2 e) {
            mutableObject.setValue(e.getActionResult());
            throw e;
        }
    }

    @Redirect(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V"))
    private void catchPacketCancelException(MultiPlayerGameMode instance, ClientLevel level, PredictiveAction predictiveAction) {
        try {
            this.startPrediction(level, predictiveAction);
        } catch (ActionResultException1_12_2 ignored) {
        }
    }

}
