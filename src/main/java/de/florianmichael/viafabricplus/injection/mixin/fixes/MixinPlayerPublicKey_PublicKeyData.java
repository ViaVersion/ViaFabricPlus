package de.florianmichael.viafabricplus.injection.mixin.fixes;

import de.florianmichael.viafabricplus.injection.access.IPublicKeyData;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;

@Mixin(PlayerPublicKey.PublicKeyData.class)
public class MixinPlayerPublicKey_PublicKeyData implements IPublicKeyData {

    @Unique
    private ByteBuffer protocolhack_1_19_0Key;

    @Override
    public ByteBuffer viafabricplus_get1_19_0Key() {
        return protocolhack_1_19_0Key;
    }

    @Override
    public void viafabricplus_set1_19_0Key(ByteBuffer byteBuffer) {
        this.protocolhack_1_19_0Key = byteBuffer;
    }
}
