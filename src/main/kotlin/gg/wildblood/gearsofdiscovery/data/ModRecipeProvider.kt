package gg.wildblood.gearsofdiscovery.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.neoforged.neoforge.common.conditions.IConditionBuilder
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) : RecipeProvider(output, registries), IConditionBuilder {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
        /**
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SMALL_ENGINEERING_BUNDLE)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .define('#', Items.DIRT)
            .unlockedBy("has_dirt", has(Items.DIRT)).save(recipeOutput)
        **/
    }
}