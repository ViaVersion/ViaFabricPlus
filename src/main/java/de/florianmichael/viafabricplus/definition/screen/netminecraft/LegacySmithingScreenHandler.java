package de.florianmichael.viafabricplus.definition.screen.netminecraft;

import de.florianmichael.viafabricplus.definition.screen.CustomScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class LegacySmithingScreenHandler extends ForgingScreenHandler {
    private final World world;

    @Nullable
    private LegacySmithingRecipe currentRecipe;
    private final List<LegacySmithingRecipe> recipes;

    public LegacySmithingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public LegacySmithingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(CustomScreenHandler.LEGACY_SMITHING, syncId, playerInventory, context);
        this.world = playerInventory.player.getWorld();
        this.recipes = this.world.getRecipeManager().listAllOfType(RecipeType.SMITHING).stream().filter((recipe) -> {
            return recipe instanceof LegacySmithingRecipe;
        }).map((recipe) -> {
            return (LegacySmithingRecipe)recipe;
        }).toList();
    }

    protected ForgingSlotsManager getForgingSlotsManager() {
        return ForgingSlotsManager.create().input(0, 27, 47, (stack) -> {
            return true;
        }).input(1, 76, 47, (stack) -> {
            return true;
        }).output(2, 134, 47).build();
    }

    protected boolean canUse(BlockState state) {
        return state.isOf(Blocks.SMITHING_TABLE);
    }

    protected boolean canTakeOutput(PlayerEntity player, boolean present) {
        return this.currentRecipe != null && this.currentRecipe.matches(this.input, this.world);
    }

    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        stack.onCraft(player.getWorld(), player, stack.getCount());
        this.output.unlockLastRecipe(player, Collections.emptyList());
        this.decrementStack(0);
        this.decrementStack(1);
        this.context.run((world, pos) -> {
            world.syncWorldEvent(1044, pos, 0);
        });
    }

    private void decrementStack(int slot) {
        ItemStack itemStack = this.input.getStack(slot);
        itemStack.decrement(1);
        this.input.setStack(slot, itemStack);
    }

    public void updateResult() {
        List<LegacySmithingRecipe> list = this.world.getRecipeManager().getAllMatches(RecipeType.SMITHING, this.input, this.world).stream().filter((recipe) -> {
            return recipe instanceof LegacySmithingRecipe;
        }).map((recipe) -> {
            return (LegacySmithingRecipe)recipe;
        }).toList();
        if (list.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
        } else {
            LegacySmithingRecipe legacySmithingRecipe = (LegacySmithingRecipe)list.get(0);
            ItemStack itemStack = legacySmithingRecipe.craft(this.input, this.world.getRegistryManager());
            if (itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
                this.currentRecipe = legacySmithingRecipe;
                this.output.setLastRecipe(legacySmithingRecipe);
                this.output.setStack(0, itemStack);
            }
        }

    }

    public int getSlotFor(ItemStack stack) {
        return this.testAddition(stack) ? 1 : 0;
    }

    protected boolean testAddition(ItemStack stack) {
        return this.recipes.stream().anyMatch((recipe) -> {
            return recipe.testAddition(stack);
        });
    }

    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
    }
}
