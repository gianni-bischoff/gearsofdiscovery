package gg.wildblood.gearsofdiscovery.items

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.Companion.ItemRegister
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem

object ModItems {
    val SMALL_ENGINEERING_BUNDLE: DeferredItem<Item> = ItemRegister.registerSimpleItem(
        "small_engineering_bundle", Item.Properties().food(
            FoodProperties.Builder()
                .alwaysEdible().nutrition(1).saturationModifier(2f).build()
        )
    )

    fun register() {}
}