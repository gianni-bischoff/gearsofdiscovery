package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.content.blocks.PillowBlock
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue


object ModBlocks {
    val REGISTRY: DeferredRegister.Blocks = DeferredRegister.createBlocks(GearsOfDiscoveryMod.MODID)

    val PILLOW_BLOCK: PillowBlock by REGISTRY.register("pillow_block") { ->
        PillowBlock(BlockBehaviour.Properties.of().lightLevel { 15 }.strength(3.0f).noOcclusion())
    }

    val PILLOW_ITEM: Item by ModItems.REGISTRY.register("pillow_item") { ->
        BlockItem(PILLOW_BLOCK, Item.Properties())
    }
}