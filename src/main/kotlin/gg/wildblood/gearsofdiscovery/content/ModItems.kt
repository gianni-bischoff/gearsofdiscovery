package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.content.items.HyperSpeedTotem
import gg.wildblood.gearsofdiscovery.content.items.Otomaton
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModItems {
    val REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(GearsOfDiscoveryMod.MODID)

    val OTOMATON: Otomaton by REGISTRY.register("otomaton") { ->
        Otomaton(Item.Properties())
    }

    val HYPERSPEED_TOTEM: HyperSpeedTotem by REGISTRY.register("hyperspeed_totem") { ->
        HyperSpeedTotem(Item.Properties())
    }
}