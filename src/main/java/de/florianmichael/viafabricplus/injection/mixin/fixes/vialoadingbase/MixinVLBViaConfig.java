package de.florianmichael.viafabricplus.injection.mixin.fixes.vialoadingbase;

import com.viaversion.viaversion.configuration.AbstractViaConfig;
import de.florianmichael.vialoadingbase.defaults.viaversion.VLBViaConfig;
import org.spongepowered.asm.mixin.Mixin;

import java.io.File;

@Mixin(value = VLBViaConfig.class, remap = false)
public abstract class MixinVLBViaConfig extends AbstractViaConfig {

    protected MixinVLBViaConfig(File configFile) {
        super(configFile);
    }

    @Override
    public boolean isLeftHandedHandling() {
        return false;
    }

    @Override
    public boolean isShieldBlocking() {
        return false;
    }
}
