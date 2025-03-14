package gg.wildblood.gearsofdiscovery.sounds

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.common.util.DeferredSoundType
import net.neoforged.neoforge.registries.DeferredRegister

object ModSounds {
    val SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, GearsOfDiscoveryMod.MODID)

    val OTOMATON_SQUEAL = SOUND_EVENTS.register("otomaton_squeal", SoundEvent::createVariableRangeEvent)
}