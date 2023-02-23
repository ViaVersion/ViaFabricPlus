package de.florianmichael.viafabricplus.injection.mixin.fixes.item;

import de.florianmichael.viafabricplus.value.ValueHolder;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemCooldownManager.class)
public class MixinItemCooldownManager {

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    public void injectSet(Item item, int duration, CallbackInfo ci) {
        if (ValueHolder.removeCooldowns.getValue()) {
            ci.cancel();
        }
    }
}
