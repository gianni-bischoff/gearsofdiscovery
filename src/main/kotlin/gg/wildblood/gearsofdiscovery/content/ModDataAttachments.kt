package gg.wildblood.gearsofdiscovery.content

import com.mojang.serialization.Codec
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.*
import java.util.function.Supplier

object ModDataAttachments {
    val ATTACHMENT_TYPE = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, GearsOfDiscoveryMod.MODID)

    val LAST_JOIN = ATTACHMENT_TYPE.register("last_join_date", Supplier {
        AttachmentType.builder(Supplier { Date().time })
            .serialize(Codec.LONG)
            .copyOnDeath()
            .build()
    })

    val FIRST_JOIN = ATTACHMENT_TYPE.register("first_join", Supplier {
        AttachmentType.builder(Supplier { true })
            .serialize(Codec.BOOL)
            .copyOnDeath()
            .build()
    })
}