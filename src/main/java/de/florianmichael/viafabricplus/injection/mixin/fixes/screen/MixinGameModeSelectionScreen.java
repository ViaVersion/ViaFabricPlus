package de.florianmichael.viafabricplus.injection.mixin.fixes.screen;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(GameModeSelectionScreen.class)
public class MixinGameModeSelectionScreen {

    @Mutable
    @Shadow @Final private static int UI_WIDTH;

    @Unique
    private GameModeSelectionScreen.GameModeSelection[] protocolhack_unwrappedGameModes;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fixUIWidth(CallbackInfo ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThan(ProtocolVersion.v1_8)) {
            final List<GameModeSelectionScreen.GameModeSelection> gameModeSelections = Arrays.stream(GameModeSelectionScreen.GameModeSelection.values()).toList();
            if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThan(LegacyProtocolVersion.r1_3_1tor1_3_2)) gameModeSelections.remove(GameModeSelectionScreen.GameModeSelection.ADVENTURE);
            if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThan(ProtocolVersion.v1_8)) gameModeSelections.remove(GameModeSelectionScreen.GameModeSelection.SPECTATOR);

            protocolhack_unwrappedGameModes = gameModeSelections.toArray(GameModeSelectionScreen.GameModeSelection[]::new);
            UI_WIDTH = protocolhack_unwrappedGameModes.length * 31 - 5;
        }
    }

    @Redirect(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;VALUES:[Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;"))
    public GameModeSelectionScreen.GameModeSelection[] removeNewerGameModes() {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThan(ProtocolVersion.v1_8)) {
            return protocolhack_unwrappedGameModes;
        }
        return GameModeSelectionScreen.GameModeSelection.values();
    }
}
