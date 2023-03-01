package de.florianmichael.viafabricplus.injection.mixin.fixes.screen;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public class MixinDownloadingTerrainScreen extends Screen {

    @Shadow @Final private long loadStartTime;
    @Shadow private boolean closeOnNextTick;
    @Shadow private boolean ready;
    @Unique
    private int protocolhack_tickCounter;

    public MixinDownloadingTerrainScreen(Text title) {
        super(title);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void injectTick(CallbackInfo ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_1)) {
            protocolhack_tickCounter++;

            if (protocolhack_tickCounter % 20 == 0) {
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new KeepAliveC2SPacket(0));
            }
        }
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            final boolean isTimeOver = this.closeOnNextTick || System.currentTimeMillis() > this.loadStartTime + 2000L;

            if (isTimeOver && this.client != null && this.client.player != null) {
                final BlockPos blockPos = this.client.player.getBlockPos();
                final boolean isWorldLoaded = this.client.world != null && this.client.world.isOutOfHeightLimit(blockPos.getY());

                if (isWorldLoaded || this.client.worldRenderer.isRenderingReady(blockPos)) {
                    this.close();
                }

                if (this.ready) {
                    this.closeOnNextTick = true;
                }

            }
            ci.cancel();
        }
    }
}
