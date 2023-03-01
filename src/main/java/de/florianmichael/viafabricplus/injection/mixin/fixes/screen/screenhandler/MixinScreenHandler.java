package de.florianmichael.viafabricplus.injection.mixin.fixes.screen.screenhandler;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.injection.access.IScreenHandler;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class MixinScreenHandler implements IScreenHandler {

    @Unique
    private short protocolhack_lastActionId = 0;

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void injectInternalOnSlotClick(int slot, int clickData, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8) && actionType == SlotActionType.SWAP && clickData == 40) {
            ci.cancel();
        }
    }

    @Override
    public short viafabricplus_getAndIncrementLastActionId() {
        return ++protocolhack_lastActionId;
    }
}
