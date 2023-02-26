package de.florianmichael.viafabricplus.injection.mixin.vialegacy;

import de.florianmichael.viafabricplus.definition.ChatLengthDefinition;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.data.ClassicProtocolExtension;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.storage.ExtensionProtocolMetadataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExtensionProtocolMetadataStorage.class, remap = false)
public class MixinExtensionProtocolMetadataStorage {

    @Inject(method = "addServerExtension", at = @At("RETURN"))
    public void updateChatLengthDefinition(ClassicProtocolExtension extension, int version, CallbackInfo ci) {
        if (extension == ClassicProtocolExtension.LONGER_MESSAGES) {
            ChatLengthDefinition.expand();
        }
    }
}
