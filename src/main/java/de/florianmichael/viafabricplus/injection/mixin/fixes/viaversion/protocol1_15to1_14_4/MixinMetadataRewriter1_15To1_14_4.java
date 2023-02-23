package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_15to1_14_4;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.metadata.MetadataRewriter1_15To1_14_4;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import de.florianmichael.viafabricplus.definition.v1_14_4.Meta18Storage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
@Mixin(MetadataRewriter1_15To1_14_4.class)
public abstract class MixinMetadataRewriter1_15To1_14_4 extends EntityRewriter<ClientboundPackets1_14, Protocol1_15To1_14_4> {

    public MixinMetadataRewriter1_15To1_14_4(Protocol1_15To1_14_4 protocol) {
        super(protocol);
    }

    @Inject(method = "handleMetadata", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE), remap = false)
    public void trackHealth(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection, CallbackInfo ci) {
        final Meta18Storage meta18Storage = protocol.get(Meta18Storage.class);
        if (meta18Storage == null) {
            Via.getPlatform().getLogger().severe("Metadata 18 storage is missing!");
            return;
        }
        meta18Storage.getHealthDataMap().put(entityId, (Float) metadata.getValue());
    }
}
