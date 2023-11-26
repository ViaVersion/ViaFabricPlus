package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.Protocol1_19_4To1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.packets.EntityPackets;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityPackets.class, remap = false)
public abstract class MixinEntityPackets1_19_4 extends EntityRewriter<ClientboundPackets1_19_3, Protocol1_19_4To1_19_3> {

    protected MixinEntityPackets1_19_4(Protocol1_19_4To1_19_3 protocol) {
        super(protocol);
    }

    @Inject(method = "registerPackets", at = @At("RETURN"))
    private void fixTeleportBehaviour(CallbackInfo ci) {
        this.protocol.registerClientbound(ClientboundPackets1_19_3.ENTITY_TELEPORT, ClientboundPackets1_19_4.ENTITY_TELEPORT, wrapper -> {
        }, true);
    }

}
