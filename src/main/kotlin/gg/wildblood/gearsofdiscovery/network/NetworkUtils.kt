package gg.wildblood.gearsofdiscovery.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack

object NetworkUtils {
    val BYTEBUFCODEC_STRING_SET: StreamCodec<ByteBuf, Set<String>> = ByteBufCodecs.collection(
        ::HashSet,
        ByteBufCodecs.STRING_UTF8,
    )

    val BYTEBUFCODEC_ITEMSTACK_LIST: StreamCodec<RegistryFriendlyByteBuf, MutableList<ItemStack>> = ByteBufCodecs.collection(
        ::ArrayList,
        ItemStack.STREAM_CODEC,
    )
}
