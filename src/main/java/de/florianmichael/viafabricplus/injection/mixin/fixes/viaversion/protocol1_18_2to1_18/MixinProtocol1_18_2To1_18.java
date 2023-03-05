package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_18_2to1_18;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_18_2to1_18.Protocol1_18_2To1_18;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.ClientboundPackets1_18;
import de.florianmichael.viafabricplus.definition.v1_18_0.SpawnPositionTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DataFlowIssue")
// Copyright RaphiMC/RK_01 - LICENSE file
@Mixin(value = Protocol1_18_2To1_18.class, remap = false)
public class MixinProtocol1_18_2To1_18 extends AbstractProtocol<ClientboundPackets1_18, ClientboundPackets1_18, ServerboundPackets1_17, ServerboundPackets1_17> {

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void fixSpawnPositionSending(CallbackInfo ci) {
        this.registerClientbound(ClientboundPackets1_18.PLAYER_POSITION, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> wrapper.user().get(SpawnPositionTracker.class).sendSpawnPosition());
            }
        });
        this.registerClientbound(ClientboundPackets1_18.SPAWN_POSITION, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.POSITION1_14); // position
                map(Type.FLOAT); // angle

                handler(wrapper -> wrapper.user().get(SpawnPositionTracker.class).setSpawnPosition(wrapper.get(Type.POSITION1_14, 0), wrapper.get(Type.FLOAT, 0)));
            }
        });
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        connection.put(new SpawnPositionTracker(connection));
    }
}
