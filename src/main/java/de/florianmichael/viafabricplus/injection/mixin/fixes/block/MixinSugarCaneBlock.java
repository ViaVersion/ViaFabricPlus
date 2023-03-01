package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SugarCaneBlock.class)
public class MixinSugarCaneBlock {

    @Redirect(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean changePlaceTarget(BlockState instance, TagKey<Block> tagKey) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_17)) {
            return instance.isOf(Blocks.GRASS_BLOCK) || instance.isOf(Blocks.DIRT) || instance.isOf(Blocks.COARSE_DIRT) || instance.isOf(Blocks.PODZOL);
        }
        return instance.isIn(tagKey);
    }
}
