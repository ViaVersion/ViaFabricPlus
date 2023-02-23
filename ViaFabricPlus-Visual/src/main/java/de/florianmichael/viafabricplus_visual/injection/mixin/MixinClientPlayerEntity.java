package de.florianmichael.viafabricplus_visual.injection.mixin;

import com.mojang.authlib.GameProfile;
import de.florianmichael.viafabricplus_visual.ViaFabricPlusVisual;
import de.florianmichael.viafabricplus_visual.definition.v1_8_x.ArmorPointsDefinition;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Override
    public int getArmor() {
        if (ViaFabricPlusVisual.emulateArmorHud.getValue()) {
            return ArmorPointsDefinition.sum();
        }
        return super.getArmor();
    }
}
