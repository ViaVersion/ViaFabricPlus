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

package com.viaversion.viafabricplus.injection.mixin.features.v1_21_5.movement;

import com.mojang.authlib.GameProfile;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.packet.ServerboundPackets1_21_5;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.Protocol1_21_5To1_21_6;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.chat.ChatAbilities;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer {

    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    public abstract boolean isShiftKeyDown();

    @Unique
    private boolean viaFabricPlus$lastSneaking;

    public MixinLocalPlayer(final ClientLevel level, final GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initLastSneaking(Minecraft minecraft, ClientLevel level, ClientPacketListener connection, StatsCounter stats, ClientRecipeBook recipeBook, Input lastSentInput, boolean wasSprinting, ChatAbilities chatAbilities, CallbackInfo ci) {
        viaFabricPlus$lastSneaking = lastSentInput.shift();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V", shift = At.Shift.AFTER))
    private void sendSneakingPacket(CallbackInfo ci) {
        if (ViaFabricPlus.api().targetVersion().betweenInclusive(ProtocolVersion.v1_21_2, ProtocolVersion.v1_21_5)) {
            this.viaFabricPlus$sendSneakingPacket();
        }
    }

    @Inject(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sendIsSprintingIfNeeded()V", shift = At.Shift.AFTER))
    private void sendSneakingAfterSprinting(CallbackInfo ci) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            this.viaFabricPlus$sendSneakingPacket();
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0))
    private void skipVVProtocol(ClientPacketListener instance, Packet<?> packet) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_5) && packet instanceof ServerboundPlayerInputPacket(
            Input i
        )) {
            // Directly send the player input packet to bypass the code in the 1.21.5->1.21.6 protocol.
            // This allows mods to directly send raw packets which will then be remapped by VV instead of us.
            this.viaFabricPlus$sendInputPacket(i);
        } else {
            instance.send(packet);
        }
    }

    @Unique
    private void viaFabricPlus$sendInputPacket(final Input playerInput) {
        byte flags = 0;
        flags = (byte) (flags | (playerInput.forward() ? 0x1 : 0));
        flags = (byte) (flags | (playerInput.backward() ? 0x2 : 0));
        flags = (byte) (flags | (playerInput.left() ? 0x4 : 0));
        flags = (byte) (flags | (playerInput.right() ? 0x8 : 0));
        flags = (byte) (flags | (playerInput.jump() ? 0x10 : 0));
        flags = (byte) (flags | (playerInput.shift() ? 0x20 : 0));
        flags = (byte) (flags | (playerInput.sprint() ? 0x40 : 0));

        final PacketWrapper inputPacket = PacketWrapper.create(ServerboundPackets1_21_5.PLAYER_INPUT, ViaFabricPlus.api().userConnection());
        inputPacket.write(Types.BYTE, flags);
        inputPacket.scheduleSendToServer(Protocol1_21_5To1_21_6.class);
    }

    @Unique
    private void viaFabricPlus$sendSneakingPacket() {
        final boolean sneaking = this.isShiftKeyDown();
        if (sneaking == this.viaFabricPlus$lastSneaking) {
            return;
        }

        final PacketWrapper sneakingPacket = PacketWrapper.create(ServerboundPackets1_21_5.PLAYER_COMMAND, ViaFabricPlus.api().userConnection());
        sneakingPacket.write(Types.VAR_INT, getId());
        sneakingPacket.write(Types.VAR_INT, sneaking ? 0 : 1);
        sneakingPacket.write(Types.VAR_INT, 0); // No data
        sneakingPacket.scheduleSendToServer(Protocol1_21_5To1_21_6.class);
        this.viaFabricPlus$lastSneaking = sneaking;
    }

}
