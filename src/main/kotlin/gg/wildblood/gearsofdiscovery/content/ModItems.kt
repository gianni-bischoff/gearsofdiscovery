package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.content.items.BasicTooltipItem
import gg.wildblood.gearsofdiscovery.content.items.HyperSpeedTotem
import gg.wildblood.gearsofdiscovery.content.items.Otomaton
import net.minecraft.network.chat.Component
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModItems {
    val REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(GearsOfDiscoveryMod.MODID)

    val OTOMATON: Otomaton by REGISTRY.register("otomaton") { ->
        Otomaton(Item.Properties())
    }

    val WETZEL_PRETZEL: Item by REGISTRY.register("wetzelpretzel") { ->
        BasicTooltipItem(Item.Properties().food(FoodProperties.Builder()
                .alwaysEdible()
                .nutrition(6)
                .saturationModifier(6.0f)
                .build()
            ).rarity(Rarity.EPIC)
        ).withTooltip(Component.translatable("item.${GearsOfDiscoveryMod.MODID}.wetzelpretzel.tooltip"))
    }

    val HYPERSPEED_TOTEM: HyperSpeedTotem by REGISTRY.register("hyperspeed_totem") { ->
        HyperSpeedTotem(Item.Properties())
    }
}