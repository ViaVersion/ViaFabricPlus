package de.florianmichael.viafabricplus.injection.mixin.fixes.viaaprilfools;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerAbilities;
import net.raphimc.viaaprilfools.protocols.protocol1_16to20w14infinite.ClientboundPackets20w14infinite;
import net.raphimc.viaaprilfools.protocols.protocol1_16to20w14infinite.Protocol1_16to20w14infinite;
import net.raphimc.viaaprilfools.protocols.protocol1_16to20w14infinite.ServerboundPackets20w14infinite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Protocol1_16to20w14infinite.class, remap = false)
public class MixinProtocol1_16to20w14infinite extends BackwardsProtocol<ClientboundPackets20w14infinite, ClientboundPackets1_16, ServerboundPackets20w14infinite, ServerboundPackets1_16> {

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void fixPlayerAbilities(CallbackInfo ci) {
        this.registerServerbound(ServerboundPackets1_16.PLAYER_ABILITIES, ServerboundPackets20w14infinite.PLAYER_ABILITIES, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    wrapper.passthrough(Type.BYTE);

                    final PlayerAbilities abilities = MinecraftClient.getInstance().player.getAbilities();

                    wrapper.write(Type.FLOAT, abilities.getFlySpeed());
                    wrapper.write(Type.FLOAT, abilities.getWalkSpeed());
                });
            }
        }, true);
    }
}
