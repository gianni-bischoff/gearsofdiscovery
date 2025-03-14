package gg.wildblood.gearsofdiscovery.items

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredRegister

import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModItems {
    val REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(GearsOfDiscoveryMod.MODID)

    val SMALL_ENGINEERING_BUNDLE: Item by REGISTRY.register("small_engineering_bundle") { ->
        Item(Item.Properties().food(
            FoodProperties.Builder()
                .alwaysEdible().nutrition(1).saturationModifier(2f).build()
        ))
    }

    val OTOMATON: OtomatonWeapon by REGISTRY.register("otomaton") { ->
        OtomatonWeapon(Item.Properties())
    }
}