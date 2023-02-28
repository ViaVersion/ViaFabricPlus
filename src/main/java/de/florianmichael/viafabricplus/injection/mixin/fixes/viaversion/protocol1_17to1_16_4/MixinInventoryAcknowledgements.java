package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_17to1_16_4;

import com.viaversion.viaversion.libs.fastutil.ints.IntList;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.storage.InventoryAcknowledgements;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntLists;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InventoryAcknowledgements.class, remap = false)
public class MixinInventoryAcknowledgements {

    @Mutable
    @Shadow @Final private IntList ids;
    @Unique
    private it.unimi.dsi.fastutil.ints.IntList protocolhack_ids;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fixJavaIssue(CallbackInfo ci) {
        this.ids = null;
        this.protocolhack_ids = IntLists.synchronize(new IntArrayList());
    }

    @Inject(method = "addId", at = @At("HEAD"), cancellable = true)
    public void forwardAdd(int id, CallbackInfo ci) {
        protocolhack_ids.add(id);
        ci.cancel();
    }

    @Inject(method = "removeId", at = @At("HEAD"), cancellable = true)
    public void forwardRemove(int id, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(protocolhack_ids.rem(id));
    }
}
