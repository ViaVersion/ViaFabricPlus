package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_13to1_12_2;

import com.viaversion.viaversion.api.minecraft.BlockChangeRecord1_8;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import de.florianmichael.viafabricplus.settings.groups.DebugSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

// Copyright RaphiMC/RK_01 - LICENSE file
@Mixin(value = ConnectionData.NeighbourUpdater.class, remap = false)
public class MixinConnectionData_NeighbourUpdater {

    @Inject(method = "updateBlock", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void preventBlockSpam(int x, int y, int z, List<BlockChangeRecord1_8> records, CallbackInfo ci, int blockState, int newBlockState) {
        if (!DebugSettings.getClassWrapper().preventBlockSpam.getValue()) return;
        if (blockState == newBlockState) ci.cancel();
    }
}
