package gg.wildblood.gearsofdiscovery.datagen

import gg.wildblood.gearsofdiscovery.content.ModItems
import gg.wildblood.gearsofdiscovery.content.ModTags
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.world.level.block.Block
import top.theillusivec4.curios.api.CuriosTags
import java.util.concurrent.CompletableFuture

class ModItemTagProvider(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>, blockTags: CompletableFuture<TagLookup<Block>>) : ItemTagsProvider(output, lookupProvider, blockTags) {
    override fun addTags(provider: HolderLookup.Provider) {
        this.tag(ModTags.BACKPACK_TAG_KEY)
            .add(ModItems.MINERS_BACKPACK)

        this.tag(CuriosTags.BACK)
            .add(ModItems.MINERS_BACKPACK)
    }
}