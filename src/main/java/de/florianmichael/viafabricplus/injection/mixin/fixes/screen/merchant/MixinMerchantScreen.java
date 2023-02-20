/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes.screen.merchant;

import de.florianmichael.viafabricplus.value.ValueHolder;
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
    private int protocolhack_previousRecipeIndex;

    public MixinMerchantScreen(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void reset(CallbackInfo ci) {
        protocolhack_previousRecipeIndex = 0;
    }

    @Inject(method = "syncRecipeIndex", at = @At("HEAD"))
    public void smoothOutRecipeIndex(CallbackInfo ci) {
        if (ValueHolder.smoothOutMerchantScreens.getValue()) {
            if (protocolhack_previousRecipeIndex != selectedIndex) {
                int direction = protocolhack_previousRecipeIndex < selectedIndex ? 1 : -1;
                for (int smooth = protocolhack_previousRecipeIndex + direction /* don't send the page we already are on */; smooth != selectedIndex; smooth += direction) {
                    client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(smooth));
                }
                protocolhack_previousRecipeIndex = selectedIndex;
            }
        }
    }
}
