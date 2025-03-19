package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.neoforged.neoforge.registries.DeferredRegister

import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModCreativeTabs {
    val REGISTRY: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GearsOfDiscoveryMod.MODID)

    val gearsOfDiscoveryCreativeTab: CreativeModeTab by REGISTRY.register("gearsofdiscovery_tab") { ->
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.gearsofdiscovery"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon { ModItems.HYPERSPEED_TOTEM.defaultInstance }
            .displayItems { _: ItemDisplayParameters?, output: CreativeModeTab.Output ->
                output.accept(ModBlocks.PILLOW_ITEM)
                output.accept(ModItems.OTOMATON)
                output.accept(ModItems.HYPERSPEED_TOTEM)
            }.build()
    }
}