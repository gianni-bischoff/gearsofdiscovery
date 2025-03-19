package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredRegister

object ModSounds {
    val SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, GearsOfDiscoveryMod.MODID)

    val OTOMATON_SQUEAL = SOUND_EVENTS.register("otomaton_squeal", SoundEvent::createVariableRangeEvent)

    val OTOMATON_NOTE_1 = SOUND_EVENTS.register("otomaton_note_1", SoundEvent::createVariableRangeEvent)
    val OTOMATON_NOTE_2 = SOUND_EVENTS.register("otomaton_note_2", SoundEvent::createVariableRangeEvent)
    val OTOMATON_NOTE_3 = SOUND_EVENTS.register("otomaton_note_3", SoundEvent::createVariableRangeEvent)
    val OTOMATON_NOTE_4 = SOUND_EVENTS.register("otomaton_note_4", SoundEvent::createVariableRangeEvent)
    val OTOMATON_NOTE_5 = SOUND_EVENTS.register("otomaton_note_5", SoundEvent::createVariableRangeEvent)
    val OTOMATON_NOTE_6 = SOUND_EVENTS.register("otomaton_note_6", SoundEvent::createVariableRangeEvent)

}