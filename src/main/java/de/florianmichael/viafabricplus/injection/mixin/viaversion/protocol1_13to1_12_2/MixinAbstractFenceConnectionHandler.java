package de.florianmichael.viafabricplus.injection.mixin.viaversion.protocol1_13to1_12_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.AbstractFenceConnectionHandler;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AbstractFenceConnectionHandler.class, remap = false)
public abstract class MixinAbstractFenceConnectionHandler {

    @Shadow protected abstract boolean connects(BlockFace side, int blockState, boolean pre1_12);

    @Shadow public abstract int getBlockData(UserConnection user, Position position);

    /**
     * @author ViaVersion, EnZaXD
     * @reason Add support for pre-netty protocols
     */
    @Overwrite
    public byte getStates(UserConnection user, Position position, int blockState) {
        byte states = 0;
        boolean pre1_12 = ViaLoadingBase.getTargetVersion().isOlderThan(ProtocolVersion.v1_12);
        if (connects(BlockFace.EAST, getBlockData(user, position.getRelative(BlockFace.EAST)), pre1_12)) states |= 1;
        if (connects(BlockFace.NORTH, getBlockData(user, position.getRelative(BlockFace.NORTH)), pre1_12)) states |= 2;
        if (connects(BlockFace.SOUTH, getBlockData(user, position.getRelative(BlockFace.SOUTH)), pre1_12)) states |= 4;
        if (connects(BlockFace.WEST, getBlockData(user, position.getRelative(BlockFace.WEST)), pre1_12)) states |= 8;
        return states;
    }
}
