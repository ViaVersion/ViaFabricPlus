package de.florianmichael.viafabricplus.injection.mixin.fixes.vialegacy;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import de.florianmichael.viafabricplus.definition.v1_7_10.TeleportTracker;
import net.raphimc.vialegacy.protocols.release.protocol1_7_6_10to1_7_2_5.ClientboundPackets1_7_2;
import net.raphimc.vialegacy.protocols.release.protocol1_7_6_10to1_7_2_5.ServerboundPackets1_7_2;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.Protocol1_8to1_7_6_10;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Protocol1_8to1_7_6_10.class)
public class MixinProtocol1_8to1_7_6_10 extends AbstractProtocol<ClientboundPackets1_7_2, ClientboundPackets1_8, ServerboundPackets1_7_2, ServerboundPackets1_8> {

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void addTeleportTracker(CallbackInfo ci) {
        this.registerClientbound(ClientboundPackets1_7_2.PLAYER_POSITION, ClientboundPackets1_8.PLAYER_POSITION, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE, Type.DOUBLE, stance -> stance - 1.62F); // y
                map(Type.DOUBLE); // z
                map(Type.FLOAT); // yaw
                map(Type.FLOAT); // pitch
                read(Type.BOOLEAN); // onGround
                handler(wrapper -> {
                    final boolean onGround = wrapper.read(Type.BOOLEAN); // On Ground
                    final TeleportTracker teleportTracker = wrapper.user().get(TeleportTracker.class);
                    if (teleportTracker != null) {
                        teleportTracker.setPending(onGround);
                    }

                    wrapper.write(Type.BYTE, (byte) 0); // flags
                });
            }
        }, true);
        this.registerServerbound(ServerboundPackets1_8.PLAYER_POSITION_AND_ROTATION, ServerboundPackets1_7_2.PLAYER_POSITION_AND_ROTATION, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE); // y
                handler(wrapper -> wrapper.write(Type.DOUBLE, wrapper.get(Type.DOUBLE, 1) + 1.62)); // stance
                map(Type.DOUBLE); // z
                map(Type.FLOAT); // yaw
                map(Type.FLOAT); // pitch
                map(Type.BOOLEAN); // onGround
                handler(wrapper -> {
                    final TeleportTracker teleportTracker = wrapper.user().get(TeleportTracker.class);
                    if (teleportTracker != null) {
                        Boolean pendingTeleport = teleportTracker.getPending();
                        if (pendingTeleport != null) {
                            wrapper.set(Type.BOOLEAN, 0, pendingTeleport);
                            teleportTracker.setPending(null);
                        }
                    }
                });
            }
        }, true);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void initPipeline(UserConnection userConnection, CallbackInfo ci) {
        userConnection.put(new TeleportTracker(userConnection));
    }
}
