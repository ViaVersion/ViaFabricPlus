package de.florianmichael.viafabricplus.definition.screen.netminecraft;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LegacySmithingScreen extends ForgingScreen<LegacySmithingScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/legacy_smithing.png");

    public LegacySmithingScreen(LegacySmithingScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title, TEXTURE);
        this.titleX = 60;
        this.titleY = 18;
    }

    @Override
    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack()) {
            context.drawTexture(TEXTURE, x + 99, y + 45, this.backgroundWidth, 0, 28, 21);
        }
    }
}
