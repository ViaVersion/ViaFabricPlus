package de.florianmichael.viafabricplus.injection.mixin.fixes.item;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Map;

@Mixin(ShovelItem.class)
public class MixinShovelItem {

    @Redirect(method = "useOnBlock",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/item/ShovelItem;PATH_STATES:Ljava/util/Map;")))
    private Object redirectUseOnBlock(Map<Object, Object> map, Object grassBlock) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return null;
        } else {
            return map.get(grassBlock);
        }
    }
}
