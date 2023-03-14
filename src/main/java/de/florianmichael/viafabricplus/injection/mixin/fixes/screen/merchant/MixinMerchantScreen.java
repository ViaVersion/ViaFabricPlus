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
package de.florianmichael.viafabricplus.injection.mixin.fixes.screen.merchant;

import de.florianmichael.viafabricplus.settings.groups.DebugSettings;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MixinMerchantScreen extends HandledScreen<MerchantScreenHandler> {

    @Shadow
    private int selectedIndex;

    @Unique
    private int viafabricplus_previousRecipeIndex;

    public MixinMerchantScreen(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void reset(CallbackInfo ci) {
        viafabricplus_previousRecipeIndex = 0;
    }

    @Inject(method = "syncRecipeIndex", at = @At("HEAD"))
    public void smoothOutRecipeIndex(CallbackInfo ci) {
        if (DebugSettings.INSTANCE.smoothOutMerchantScreens.getValue()) {
            if (viafabricplus_previousRecipeIndex != selectedIndex) {
                int direction = viafabricplus_previousRecipeIndex < selectedIndex ? 1 : -1;
                for (int smooth = viafabricplus_previousRecipeIndex + direction /* don't send the page we already are on */; smooth != selectedIndex; smooth += direction) {
                    client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(smooth));
                }
                viafabricplus_previousRecipeIndex = selectedIndex;
            }
        }
    }
}
