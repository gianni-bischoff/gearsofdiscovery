package gg.wildblood.gearsofdiscovery.network

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.network.NetworkUtils.BYTEBUFCODEC_STRING_SET
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation


data class UnlockList(val lockNames: Set<String>) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<UnlockList> = CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(GearsOfDiscoveryMod.MODID, "unlock_list"))

        val STREAM_CODEC: StreamCodec<ByteBuf, UnlockList> = StreamCodec.composite(
            BYTEBUFCODEC_STRING_SET,
            UnlockList::lockNames,
            ::UnlockList
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}


