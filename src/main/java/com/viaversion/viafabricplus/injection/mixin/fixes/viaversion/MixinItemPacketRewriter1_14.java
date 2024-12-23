/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.fixes.viaversion;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.v1_12_2to1_13.packet.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.v1_12_2to1_13.packet.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.v1_13_2to1_14.Protocol1_13_2To1_14;
import com.viaversion.viaversion.protocols.v1_13_2to1_14.packet.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.v1_13_2to1_14.packet.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.v1_13_2to1_14.rewriter.ItemPacketRewriter1_14;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import com.viaversion.viafabricplus.fixes.ClientsideFixes;
import com.viaversion.viafabricplus.protocoltranslator.translator.TextComponentTranslator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemPacketRewriter1_14.class, remap = false)
public abstract class MixinItemPacketRewriter1_14 extends ItemRewriter<ClientboundPackets1_13, ServerboundPackets1_14, Protocol1_13_2To1_14> {

    public MixinItemPacketRewriter1_14(Protocol1_13_2To1_14 protocol, Type<Item> itemType, Type<Item[]> itemArrayType) {
        super(protocol, itemType, itemArrayType);
    }

    @Inject(method = "registerPackets", at = @At("RETURN"))
    private void dontResyncInventory(CallbackInfo ci) {
        this.protocol.registerServerbound(ServerboundPackets1_14.SELECT_TRADE, ServerboundPackets1_13.SELECT_TRADE, null, true);
    }

    @Inject(method = "lambda$registerPackets$0", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/util/ProtocolLogger;warning(Ljava/lang/String;)V", remap = false), cancellable = true)
    private void supportLargeContainers(PacketWrapper wrapper, CallbackInfo ci, @Local(ordinal = 0) Short windowId, @Local String type, @Local JsonElement title, @Local(ordinal = 1) Short slots) {
        if ((type.equals("minecraft:container") || type.equals("minecraft:chest")) && (slots > 54 || slots <= 0)) {
            ci.cancel();

            final String uuid = ClientsideFixes.executeSyncTask(data -> {
                final MinecraftClient mc = MinecraftClient.getInstance();

                try {
                    final int syncId = data.readUnsignedByte();
                    final int size = data.readUnsignedByte();
                    final Text mcTitle = TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(data);

                    final GenericContainerScreenHandler screenHandler = new GenericContainerScreenHandler(null, syncId, mc.player.getInventory(), new SimpleInventory(size), MathHelper.ceil(size / 9F));
                    mc.player.currentScreenHandler = screenHandler;
                    mc.setScreen(new GenericContainerScreen(screenHandler, mc.player.getInventory(), mcTitle));
                } catch (Throwable t) {
                    throw new RuntimeException("Failed to handle OpenWindow packet data", t);
                }
            });

            wrapper.clearPacket();
            wrapper.setPacketType(ClientboundPackets1_14.CUSTOM_PAYLOAD);
            wrapper.write(Types.STRING, ClientsideFixes.PACKET_SYNC_IDENTIFIER); // sync task header
            wrapper.write(Types.STRING, uuid); // sync task id
            wrapper.write(Types.UNSIGNED_BYTE, windowId);
            wrapper.write(Types.UNSIGNED_BYTE, slots);
            wrapper.write(Types.TAG, TextComponentTranslator.via1_14toViaLatest(title));
        }
    }

}
