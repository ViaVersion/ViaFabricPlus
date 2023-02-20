package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_13to1_12_2;

import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.AbstractFenceConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.GlassConnectionHandler;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = GlassConnectionHandler.class, remap = false)
public abstract class MixinGlassConnectionHandler extends AbstractFenceConnectionHandler {

    protected MixinGlassConnectionHandler(String blockConnections) {
        super(blockConnections);
    }

    /**
     * @author ViaVersion, EnZaXD
     * @reason Add support for pre-netty protocols
     */
    @Overwrite
    public byte getStates(UserConnection user, Position position, int blockState) {
        byte states = super.getStates(user, position, blockState);
        if (states != 0) return states;

        ProtocolInfo protocolInfo = user.getProtocolInfo();
        return ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8) && protocolInfo.getServerProtocolVersion() != -1 ? 0xF : states;
    }
}
