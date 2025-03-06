package gg.wildblood.gearsofdiscovery.blocks

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredRegister

import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModBlocks {
    val REGISTRY: DeferredRegister.Blocks = DeferredRegister.createBlocks(GearsOfDiscoveryMod.MODID)

    val OFFERING_BLOCK: Block by REGISTRY.register("offering_block") { ->
        Block(BlockBehaviour.Properties.of().lightLevel { 15 }.strength(3.0f))
    }
}