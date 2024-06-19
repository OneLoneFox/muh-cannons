package lonefox.muhcannons

import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.registry.Registerable
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World

class ModDamageTypes : DamageTypes{
    companion object {
        val CANNON_BALL_HIT: RegistryKey<DamageType> = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier(Muhcannons.MOD_ID, "cannon_hit"))
        fun of(world: World, key: RegistryKey<DamageType>): DamageSource {
            return DamageSource(world.registryManager.get(RegistryKeys.DAMAGE_TYPE).entryOf(key))
        }
    }
}