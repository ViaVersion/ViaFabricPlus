package de.florianmichael.viafabricplus.injection.mixin.bridge;

import de.florianmichael.viafabricplus.screen.settings.SettingsScreen;
import de.florianmichael.viafabricplus.settings.groups.BridgeSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class MixinOptionsScreen {

    @Shadow protected abstract ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier);

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/ClickableWidget;)Lnet/minecraft/client/gui/widget/ClickableWidget;", ordinal = 10, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void addValuesButton(CallbackInfo ci, GridWidget gridWidget, GridWidget.Adder adder) {
        if (BridgeSettings.getClassWrapper().optionsButtonInGameOptions.getValue()) {
            adder.add(this.createButton(Text.literal("ViaFabricPlus").styled(style -> style.withColor(Formatting.GOLD)).append(" ").append("Settings..."), () -> {
                SettingsScreen.INSTANCE.prevScreen = (OptionsScreen) (Object) this;
                return SettingsScreen.INSTANCE;
            }));
        }
    }
}
