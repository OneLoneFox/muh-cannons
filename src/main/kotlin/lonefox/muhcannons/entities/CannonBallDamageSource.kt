package lonefox.muhcannons.entities

import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.entry.RegistryEntry

class CannonBallDamageSource(type: RegistryEntry<DamageType>?) : DamageSource(type) {
}