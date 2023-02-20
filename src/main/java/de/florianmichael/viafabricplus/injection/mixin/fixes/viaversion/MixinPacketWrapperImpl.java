package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.util.Pair;
import de.florianmichael.viafabricplus.injection.access.IPacketWrapperImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Deque;

@Mixin(value = PacketWrapperImpl.class, remap = false)
public class MixinPacketWrapperImpl implements IPacketWrapperImpl {
    @Shadow @Final private Deque<Pair<Type<?>, Object>> readableObjects;

    @Override
    public Deque<Pair<Type<?>, Object>> viafabricplus_readableObjects() {
        return this.readableObjects;
    }
}
