package gg.wildblood.gearsofdiscovery.content.registry.quests.rewards

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import gg.wildblood.gearsofdiscovery.content.registry.quests.RewardDefinition
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object RewardCodecs {
    val BY_ID = ConcurrentHashMap<String, Codec<out RewardDefinition>>();
    val BY_CLASS = ConcurrentHashMap<KClass<out RewardDefinition>, Codec<out RewardDefinition>>();


    fun <T : RewardDefinition> register(codec: Codec<T>, id: String, clazz: KClass<T>) {
        if(BY_ID.putIfAbsent(id, codec) != null) {
            throw IllegalStateException("Duplicated Reward id: '$id'");
        }
        if(BY_CLASS.putIfAbsent(clazz, codec) != null) {
            throw IllegalStateException("Duplicated Reward Class: '$clazz'");
        }
    }

    fun <T : RewardDefinition> codecFor(clazz: KClass<T>) = Optional.ofNullable(BY_CLASS[clazz])
    fun <T : RewardDefinition> codecFor(id: String) = Optional.ofNullable(BY_ID[id])

    val POLYMORPHIC_CODEC: Codec<RewardDefinition> = object : Codec<RewardDefinition> {
        override fun <T> encode(input: RewardDefinition, ops: DynamicOps<T>, prefix: T): DataResult<T> {
            val codec = BY_CLASS[input::class] as? Codec<RewardDefinition>
                ?: return DataResult.error { "No codec found for reward class: ${input::class}" }
            
            return codec.encode(input, ops, prefix)
        }

        override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<com.mojang.datafixers.util.Pair<RewardDefinition, T>> {
            val typeResult = Codec.STRING.decode(ops, ops.get(input, "type").result().orElse(null))
            if (typeResult.error().isPresent) {
                return DataResult.error { "Missing or invalid 'type' field in reward" }
            }

            val type = typeResult.result().get().first
            val codec = BY_ID[type] as? Codec<RewardDefinition>
                ?: return DataResult.error { "No codec found for reward type: $type" }

            return codec.decode(ops, input)
        }
    }
}