package de.florianmichael.viafabricplus.injection.mixin.fixes.item;

import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.definition.ItemReleaseVersionDefinition;
import de.florianmichael.viafabricplus.value.ValueHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.item.ItemGroup$EntriesImpl")
public class MixinItemGroup_EntriesImpl {

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"))
    public boolean removeUnknownItems(Item instance, FeatureSet featureSet) {
        if ((ItemReleaseVersionDefinition.getCurrentMap().contains(instance) && ValueHolder.removeNotAvailableItemsFromCreativeTab.getValue()) || MinecraftClient.getInstance().isInSingleplayer()) {
            return instance.isEnabled(featureSet);
        }
        return false;
    }
}
