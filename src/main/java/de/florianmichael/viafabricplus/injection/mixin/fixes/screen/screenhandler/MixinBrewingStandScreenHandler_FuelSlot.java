package de.florianmichael.viafabricplus.injection.mixin.fixes.screen.screenhandler;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.BrewingStandScreenHandler$FuelSlot")
public class MixinBrewingStandScreenHandler_FuelSlot extends Slot {

    public MixinBrewingStandScreenHandler_FuelSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "matches(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private static void removeFuelSlot(CallbackInfoReturnable<Boolean> ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8))
            ci.setReturnValue(false);
    }

    @Override
    public boolean isEnabled() {
        return ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThan(ProtocolVersion.v1_8);
    }
}
