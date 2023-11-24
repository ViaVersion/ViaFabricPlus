package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.NoteBlock;
import net.minecraft.util.ActionResult;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public abstract class MixinNoteBlock {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void cancelMobHeadUsage(CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

}
