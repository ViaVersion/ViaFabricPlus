package de.florianmichael.viafabricplus.injection.mixin.fixes.visual;

import com.mojang.authlib.GameProfile;
import de.florianmichael.viafabricplus.definition.v1_8_x.ArmorPointsDefinition;
import de.florianmichael.viafabricplus.settings.groups.VisualSettings;
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
        if (VisualSettings.getClassWrapper().emulateArmorHud.getValue()) {
            return ArmorPointsDefinition.sum();
        }
        return super.getArmor();
    }
}
