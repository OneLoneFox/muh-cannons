package lonefox.muhcannons.entities

import lonefox.muhcannons.Muhcannons
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object EntitiesRegistry {
    lateinit var CANNON_BALL_ENTITY: EntityType<CannonBallEntity>

    fun register() {
        CANNON_BALL_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier(Muhcannons.MOD_ID, "cannon_ball"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::CannonBallEntity)
                .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                .trackRangeChunks(4).trackedUpdateRate(20)
                .build()
        )
    }
}