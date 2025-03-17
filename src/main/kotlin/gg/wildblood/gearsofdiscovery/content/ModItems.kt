package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.content.items.BackpackItem
import gg.wildblood.gearsofdiscovery.content.items.OtomatonWeapon
import gg.wildblood.gearsofdiscovery.content.items.components.BackpackContent
import gg.wildblood.gearsofdiscovery.content.items.components.ModItemDataComponents
import net.minecraft.core.component.DataComponents
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.BundleContents
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

    val MINERS_BACKPACK: Item by REGISTRY.register("miners_backpack") { ->
        BackpackItem(Item.Properties().rarity(Rarity.COMMON).stacksTo(1).component(
            ModItemDataComponents.BACKPACK_COMPONENT,
            BackpackContent.EMPTY
        ))
    }

    val OTOMATON: OtomatonWeapon by REGISTRY.register("otomaton") { ->
        OtomatonWeapon(Item.Properties())
    }
}