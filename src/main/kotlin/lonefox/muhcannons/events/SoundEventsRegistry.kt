package lonefox.muhcannons.events

import lonefox.muhcannons.Muhcannons
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

object SoundEventsRegistry {
    val CANNON_FIRE_SOUND_ID = Identifier("${Muhcannons.MOD_ID}:cannon_fire")
    val CANNON_FIRE_SOUND_EVENT = SoundEvent.of(CANNON_FIRE_SOUND_ID)

    fun register(){
        Registry.register(Registries.SOUND_EVENT, CANNON_FIRE_SOUND_ID, CANNON_FIRE_SOUND_EVENT)
    }

}