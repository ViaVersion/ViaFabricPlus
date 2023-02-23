package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_16to1_15_2;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Protocol1_16To1_15_2.class)
public class MixinProtocol1_16To1_15_2 extends AbstractProtocol<ClientboundPackets1_15, ClientboundPackets1_16, ServerboundPackets1_14, ServerboundPackets1_16> {

    @Inject(method = "registerPackets", at = @At("RETURN"), remap = false)
    public void injectRegisterPackets(CallbackInfo ci) {
        this.registerServerbound(ServerboundPackets1_16.PLAYER_ABILITIES, ServerboundPackets1_14.PLAYER_ABILITIES, new PacketHandlers() {
            public void register() {
                this.handler((wrapper) -> {
                    wrapper.passthrough(Type.BYTE);
                    final PlayerAbilities abilities = MinecraftClient.getInstance().player.getAbilities();

                    wrapper.write(Type.FLOAT, abilities.getFlySpeed());
                    wrapper.write(Type.FLOAT, abilities.getWalkSpeed());
                });
            }
        }, true);
    }
}
