package lonefox.muhcannons.entities

import lonefox.muhcannons.Muhcannons
import lonefox.muhcannons.blocks.BlocksRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BlockEntitiesRegistry {
    lateinit var CANNON_BLOCK_ENTITY: BlockEntityType<CannonBlockEntity>

    public fun register(){
        CANNON_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier(Muhcannons.MOD_ID, "cannon_block_entity"),
            FabricBlockEntityTypeBuilder.create(::CannonBlockEntity, BlocksRegistry.CANNON_BLOCK).build()
        )
    }
}