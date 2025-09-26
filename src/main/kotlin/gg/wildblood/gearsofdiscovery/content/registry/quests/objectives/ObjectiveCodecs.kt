package gg.wildblood.gearsofdiscovery.content.registry.quests.objectives

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.MapCodec
import gg.wildblood.gearsofdiscovery.content.registry.quests.ObjectiveDefinition
import gg.wildblood.gearsofdiscovery.content.registry.quests.RewardDefinition
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object ObjectiveCodecs {
    val BY_ID = ConcurrentHashMap<String, Codec<out ObjectiveDefinition>>();
    val BY_CLASS = ConcurrentHashMap<KClass<out ObjectiveDefinition>, Codec<out ObjectiveDefinition>>();

    fun <T : ObjectiveDefinition> register(codec: Codec<T>, id: String, clazz: KClass<T>) {
        if(BY_ID.putIfAbsent(id, codec) != null) {
            throw IllegalStateException("Duplicated Reward id: '$id'");
        }
        if(BY_CLASS.putIfAbsent(clazz, codec) != null) {
            throw IllegalStateException("Duplicated Reward Class: '$clazz'");
        }
    }

    fun <T : ObjectiveDefinition> codecFor(clazz: KClass<T>) = Optional.ofNullable(BY_CLASS[clazz])
    fun <T : ObjectiveDefinition> codecFor(id: String) = Optional.ofNullable(BY_ID[id])

    val POLYMORPHIC_CODEC: Codec<ObjectiveDefinition> = object : Codec<ObjectiveDefinition> {
        override fun <T> encode(input: ObjectiveDefinition, ops: DynamicOps<T>, prefix: T): DataResult<T> {
            @Suppress("UNCHECKED_CAST")
            val codec = (BY_CLASS[input::class] as? Codec<ObjectiveDefinition>)
                ?: return DataResult.error { "No codec found for objective class: ${input::class}" }
            
            return codec.encode(input, ops, prefix)
        }

        override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<com.mojang.datafixers.util.Pair<ObjectiveDefinition, T>> {
            val typeResult = Codec.STRING.decode(ops, ops.get(input, "type").result().orElse(null))
            if (typeResult.error().isPresent) {
                return DataResult.error { "Missing or invalid 'type' field in objective" }
            }

            val type = typeResult.result().get().first

            @Suppress("UNCHECKED_CAST")
            val codec = (BY_ID[type] as? Codec<ObjectiveDefinition>)
                ?: return DataResult.error { "No codec found for objective type: $type" }

            return codec.decode(ops, input)
        }
    }
}