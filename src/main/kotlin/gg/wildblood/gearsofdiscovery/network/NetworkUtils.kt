package gg.wildblood.gearsofdiscovery.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

object NetworkUtils {
    val BYTEBUFCODEC_STRING_SET: StreamCodec<ByteBuf, Set<String>> = ByteBufCodecs.collection(
        ::HashSet,
        ByteBufCodecs.STRING_UTF8,
    )
}
