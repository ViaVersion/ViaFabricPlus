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

package de.florianmichael.viafabricplus.injection.mixin.viaversion.protocol1_17to1_16_4;

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
