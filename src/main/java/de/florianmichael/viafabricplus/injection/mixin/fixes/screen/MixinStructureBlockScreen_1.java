package de.florianmichael.viafabricplus.injection.mixin.fixes.screen;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.screen.ingame.StructureBlockScreen$1")
public class MixinStructureBlockScreen_1 extends TextFieldWidget {

    public MixinStructureBlockScreen_1(TextRenderer textRenderer, int x, int y, int width, int height, TextFieldWidget copyFrom, Text text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
    }

    @Inject(method = "charTyped(CI)Z", at = @At("HEAD"), cancellable = true)
    private void onCharTyped(char chr, int keyCode, CallbackInfoReturnable<Boolean> ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) ci.setReturnValue(super.charTyped(chr, keyCode));
    }
}
