package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_9to1_8;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// Copyright RaphiMC/RK_01 - GPL v3 LICENSE
@Mixin(value = MetadataRewriter1_9To1_8.class, remap = false)
public abstract class MixinMetadataRewriter1_9To1_8 {

    @Inject(method = "handleMetadata", at = @At(value = "FIELD", target = "Lcom/viaversion/viaversion/protocols/protocol1_9to1_8/metadata/MetaIndex;PLAYER_HAND:Lcom/viaversion/viaversion/protocols/protocol1_9to1_8/metadata/MetaIndex;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void preventMetadataForClientPlayer(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection, CallbackInfo ci) {
        if (connection.getEntityTracker(Protocol1_9To1_8.class).clientEntityId() == entityId) {
            ci.cancel();
        }
    }
}
