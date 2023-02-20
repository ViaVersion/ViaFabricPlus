/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_9to1_8;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.viaversion.viaversion.protocols.protocol1_9to1_8.packets.EntityPackets$6$1", remap = false)
public abstract class MixinEntityPackets_6_1 {

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "transform(Lcom/viaversion/viaversion/api/protocol/packet/PacketWrapper;Ljava/lang/Short;)Ljava/lang/Integer;", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/data/entity/EntityTracker;clientEntityId()I"), cancellable = true)
    private void fixOutOfBoundsSlot(PacketWrapper wrapper, Short slot, CallbackInfoReturnable<Integer> cir) throws Exception {
        final int entityId = wrapper.get(Type.VAR_INT, 0);
        final int clientPlayerId = wrapper.user().getEntityTracker(Protocol1_9To1_8.class).clientEntityId();
        if (slot < 0 || slot > 4 || (entityId == clientPlayerId && slot > 3)) {
            wrapper.cancel();
            cir.setReturnValue(0);
        }
    }
}
