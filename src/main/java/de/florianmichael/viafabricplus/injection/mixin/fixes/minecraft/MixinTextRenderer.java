/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.raphimc.vialoader.util.VersionEnum;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TextRenderer.class)
public abstract class MixinTextRenderer {

    @Shadow
    public abstract List<OrderedText> wrapLines(StringVisitable text, int width);

    @Shadow
    public abstract int draw(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType, int backgroundColor, int light);

    @Shadow
    public abstract String mirror(String text);

    @Shadow
    @Final
    public int fontHeight;

    @Shadow
    protected abstract int drawInternal(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType, int backgroundColor, int light);

    @Shadow
    public abstract int getWidth(OrderedText text);

    @Inject(method = "draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I", at = @At("HEAD"), cancellable = true)
    private void allowNewLines_String(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType, int backgroundColor, int light, boolean rightToLeft, CallbackInfoReturnable<Integer> cir) {
        if (ProtocolHack.getTargetVersion() == VersionEnum.bedrockLatest) {
            final List<OrderedText> lines = wrapLines(StringVisitable.plain(rightToLeft ? this.mirror(text) : text), Integer.MAX_VALUE);
            if (lines.size() > 1) {
                int offsetX = 0;
                for (int i = 0; i < lines.size(); i++) {
                    offsetX = this.drawInternal(lines.get(i), x, y - (lines.size() * (fontHeight + 2)) + (i * (fontHeight + 2)), color, shadow, new Matrix4f(matrix), vertexConsumers, layerType, backgroundColor, light);
                }
                cir.setReturnValue(offsetX);
            }
        }
    }

    @Inject(method = "draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", at = @At("HEAD"), cancellable = true)
    private void allowNewLines_Text(Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType, int backgroundColor, int light, CallbackInfoReturnable<Integer> cir) {
        if (ProtocolHack.getTargetVersion() == VersionEnum.bedrockLatest) {
            final List<OrderedText> lines = wrapLines(text, Integer.MAX_VALUE);
            if (lines.size() > 1) {
                int offsetX = 0;
                for (int i = 0; i < lines.size(); i++) {
                    offsetX = this.draw(lines.get(i), x, y - (lines.size() * (fontHeight + 2)) + (i * (fontHeight + 2)), color, shadow, new Matrix4f(matrix), vertexConsumers, layerType, backgroundColor, light);
                }
                cir.setReturnValue(offsetX);
            }
        }
    }

    @Inject(method = "getWidth(Lnet/minecraft/text/StringVisitable;)I", at = @At("HEAD"), cancellable = true)
    private void allowNewLines_getWidth(StringVisitable text, CallbackInfoReturnable<Integer> cir) {
        if (MinecraftClient.getInstance().world != null && ProtocolHack.getTargetVersion() == VersionEnum.bedrockLatest) {
            int i = 0;
            for (OrderedText wrapLine : this.wrapLines(text, Integer.MAX_VALUE)) {
                if (getWidth(wrapLine) >= i) i = getWidth(wrapLine);
            }
            cir.setReturnValue(MathHelper.ceil(i));
        }
    }

}
