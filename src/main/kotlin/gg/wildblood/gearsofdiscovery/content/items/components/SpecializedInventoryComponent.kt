package gg.wildblood.gearsofdiscovery.content.items.components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import gg.wildblood.gearsofdiscovery.network.NetworkUtils.BYTEBUFCODEC_ITEMSTACK_LIST
import net.minecraft.network.RegistryFriendlyByteBuf
import net.neoforged.neoforge.items.IItemHandlerModifiable


data class SpecializedInventoryComponent(
    var items: MutableList<ItemStack> = mutableListOf(
        ItemStack.EMPTY
    )
) {
    companion object {
        val BASIC_CODEC: Codec<SpecializedInventoryComponent> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<SpecializedInventoryComponent> ->
            instance.group(
                Codec.list(ItemStack.CODEC).fieldOf("items").forGetter(SpecializedInventoryComponent::items)
            ).apply(instance, ::SpecializedInventoryComponent)
        }

        val BASIC_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SpecializedInventoryComponent> = StreamCodec.composite(
            BYTEBUFCODEC_ITEMSTACK_LIST,
            SpecializedInventoryComponent::items,
            ::SpecializedInventoryComponent
        )
    }

    fun isEmpty(): Boolean = items.isEmpty() || items.all { it.isEmpty }
}
