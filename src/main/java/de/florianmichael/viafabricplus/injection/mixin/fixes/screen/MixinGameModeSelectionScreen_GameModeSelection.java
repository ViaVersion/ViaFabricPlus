package de.florianmichael.viafabricplus.injection.mixin.fixes.screen;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@SuppressWarnings("DataFlowIssue")
@Mixin(GameModeSelectionScreen.GameModeSelection.class)
public class MixinGameModeSelectionScreen_GameModeSelection {

    @Shadow @Final public static GameModeSelectionScreen.GameModeSelection SURVIVAL;

    @Shadow @Final public static GameModeSelectionScreen.GameModeSelection CREATIVE;

    @Inject(method = "getCommand", at = @At("HEAD"), cancellable = true)
    private void oldCommand(CallbackInfoReturnable<String> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
            cir.setReturnValue(
                    "gamemode " + MinecraftClient.getInstance().getSession().getUsername() + ' ' + switch (((Enum<?>)(Object)this).ordinal()) {
                        case 0, 3 -> 1;
                        case 1, 2 -> 0;
                        default -> throw new AssertionError();
                    }
            );
        }
    }

    @Inject(method = "next", at = @At("HEAD"), cancellable = true)
    public void unwrapGameModes(CallbackInfoReturnable<Optional<GameModeSelectionScreen.GameModeSelection>> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThan(ProtocolVersion.v1_8)) {
            switch ((GameModeSelectionScreen.GameModeSelection)(Object)this) {
                case CREATIVE -> cir.setReturnValue(Optional.of(SURVIVAL));
                case SURVIVAL -> {
                    if (ViaLoadingBase.getTargetVersion().isOlderThan(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
                        cir.setReturnValue(Optional.of(CREATIVE));
                    } else {
                        cir.setReturnValue(Optional.of(GameModeSelectionScreen.GameModeSelection.ADVENTURE));
                    }
                }
                case ADVENTURE -> {
                    cir.setReturnValue(Optional.of(CREATIVE));
                }
            }
        }
    }
}
