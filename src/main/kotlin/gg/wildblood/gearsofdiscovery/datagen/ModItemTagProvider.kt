package gg.wildblood.gearsofdiscovery.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture

class ModItemTagProvider(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>, blockTags: CompletableFuture<TagLookup<Block>>) : ItemTagsProvider(output, lookupProvider, blockTags) {
    override fun addTags(provider: HolderLookup.Provider) {
    }
}