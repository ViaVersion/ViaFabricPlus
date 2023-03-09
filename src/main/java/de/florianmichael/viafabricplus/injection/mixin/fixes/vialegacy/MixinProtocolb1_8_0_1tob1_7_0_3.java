/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.vialegacy;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.raphimc.vialegacy.protocols.beta.protocol1_0_0_1tob1_8_0_1.ClientboundPacketsb1_8;
import net.raphimc.vialegacy.protocols.beta.protocol1_0_0_1tob1_8_0_1.ServerboundPacketsb1_8;
import net.raphimc.vialegacy.protocols.beta.protocolb1_8_0_1tob1_7_0_3.ClientboundPacketsb1_7;
import net.raphimc.vialegacy.protocols.beta.protocolb1_8_0_1tob1_7_0_3.Protocolb1_8_0_1tob1_7_0_3;
import net.raphimc.vialegacy.protocols.beta.protocolb1_8_0_1tob1_7_0_3.ServerboundPacketsb1_7;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Protocolb1_8_0_1tob1_7_0_3.class, remap = false)
public class MixinProtocolb1_8_0_1tob1_7_0_3 extends AbstractProtocol<ClientboundPacketsb1_7, ClientboundPacketsb1_8, ServerboundPacketsb1_7, ServerboundPacketsb1_8> {

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void betterKeepAliveHandling(CallbackInfo ci) {
        this.registerServerbound(ServerboundPacketsb1_8.KEEP_ALIVE, ServerboundPacketsb1_7.KEEP_ALIVE, new PacketHandlers() {
            @Override
            protected void register() {
                handler(wrapper -> {
                    if (!(MinecraftClient.getInstance().currentScreen instanceof DownloadingTerrainScreen)) {
                        wrapper.cancel();
                    }
                });
            }
        }, true);
    }
}
