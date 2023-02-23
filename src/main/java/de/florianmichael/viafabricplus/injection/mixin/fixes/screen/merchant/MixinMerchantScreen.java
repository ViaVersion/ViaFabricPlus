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
