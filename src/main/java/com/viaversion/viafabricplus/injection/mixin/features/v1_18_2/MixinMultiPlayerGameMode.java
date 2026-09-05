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

package com.viaversion.viafabricplus.injection.mixin.features.v1_18_2;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.features.v1_18_2.ClientPlayerInteractionManager1_18_2;
import com.viaversion.viafabricplus.injection.access.v1_18_2.IMultiPlayerGameMode;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode implements IMultiPlayerGameMode {

    @Shadow
    @Final
    private ClientPacketListener connection;

    @Unique
    private final ClientPlayerInteractionManager1_18_2 viaFabricPlus$1_18_2InteractionManager = new ClientPlayerInteractionManager1_18_2();

    @Inject(method = "startPrediction", at = @At("HEAD"))
    private void trackPlayerAction(ClientLevel level, PredictiveAction predictiveAction, CallbackInfo ci) {
        if (ViaFabricPlus.api().targetVersion().betweenInclusive(ProtocolVersion.v1_14_4, ProtocolVersion.v1_18_2) && predictiveAction instanceof ServerboundPlayerActionPacket playerActionC2SPacket) {
            this.viaFabricPlus$1_18_2InteractionManager.trackPlayerAction(playerActionC2SPacket.getAction(), playerActionC2SPacket.getPos());
        }
    }

    @Redirect(method = {"startDestroyBlock", "stopDestroyBlock"}, at = @At(value = "NEW", target = "(Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket$Action;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket;"))
    private ServerboundPlayerActionPacket trackPlayerAction(ServerboundPlayerActionPacket.Action action, BlockPos pos, Direction direction) {
        if (ViaFabricPlus.api().targetVersion().betweenInclusive(ProtocolVersion.v1_14_4, ProtocolVersion.v1_18_2)) {
            this.viaFabricPlus$1_18_2InteractionManager.trackPlayerAction(action, pos);
        }
        return new ServerboundPlayerActionPacket(action, pos, direction);
    }

    @WrapWithCondition(method = "useItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V"))
    private boolean fixPacketOrder(MultiPlayerGameMode instance, ClientLevel level, PredictiveAction predictiveAction, Player player, InteractionHand hand) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_18_2)) {
            this.connection.send(new ServerboundUseItemPacket(hand, 0, player.getYRot(), player.getXRot()));
            predictiveAction.predict(0);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public ClientPlayerInteractionManager1_18_2 viaFabricPlus$get1_18_2InteractionManager() {
        return this.viaFabricPlus$1_18_2InteractionManager;
    }

}
