package lonefox.muhcannons.blocks

import lonefox.muhcannons.Muhcannons
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier

object BlocksRegistry {
    lateinit var CANNON_BLOCK: CannonBlock
    lateinit var CANNON_BLOCK_ITEM: BlockItem
    lateinit var LARGE_CANNON_BARREL_BLOCK: LargeCannonBarrelBlock
    lateinit var LARGE_CANNON_BARREL_BLOCK_ITEM: BlockItem
    lateinit var MEDIUM_CANNON_BARREL_BLOCK: MediumCannonBarrelBlock
    lateinit var MEDIUM_CANNON_BARREL_BLOCK_ITEM: BlockItem
    lateinit var SMALL_CANNON_BARREL_BLOCK: SmallCannonBarrelBlock
    lateinit var SMALL_CANNON_BARREL_BLOCK_ITEM: BlockItem

    private fun registerBlockItem(name: String, block: Block): BlockItem{
        return Registry.register(Registries.ITEM, Identifier(Muhcannons.MOD_ID, name), BlockItem(block, FabricItemSettings()))
    }

    fun register(){
        CANNON_BLOCK = Registry.register(
            Registries.BLOCK,
            Identifier(Muhcannons.MOD_ID, "cannon"),
            CannonBlock(FabricBlockSettings.create().hardness(1.5f).resistance(6.0f).sounds(BlockSoundGroup.METAL))
        )
        CANNON_BLOCK_ITEM = registerBlockItem("cannon", CANNON_BLOCK)

        LARGE_CANNON_BARREL_BLOCK = Registry.register(
            Registries.BLOCK,
            Identifier(Muhcannons.MOD_ID, "cannon_barrel_large"),
            LargeCannonBarrelBlock(FabricBlockSettings.create().hardness(1.5f).resistance(6.0f).sounds(BlockSoundGroup.METAL))
        )
        LARGE_CANNON_BARREL_BLOCK_ITEM = registerBlockItem("cannon_barrel_large", LARGE_CANNON_BARREL_BLOCK)

        MEDIUM_CANNON_BARREL_BLOCK = Registry.register(
            Registries.BLOCK,
            Identifier(Muhcannons.MOD_ID, "cannon_barrel_medium"),
            MediumCannonBarrelBlock(FabricBlockSettings.create().hardness(1.5f).resistance(6.0f).sounds(BlockSoundGroup.METAL))
        )
        MEDIUM_CANNON_BARREL_BLOCK_ITEM = registerBlockItem("cannon_barrel_medium", MEDIUM_CANNON_BARREL_BLOCK)

        SMALL_CANNON_BARREL_BLOCK = Registry.register(
            Registries.BLOCK,
            Identifier(Muhcannons.MOD_ID, "cannon_barrel_small"),
            SmallCannonBarrelBlock(FabricBlockSettings.create().hardness(1.5f).resistance(6.0f).sounds(BlockSoundGroup.METAL))
        )
        SMALL_CANNON_BARREL_BLOCK_ITEM = registerBlockItem("cannon_barrel_small", SMALL_CANNON_BARREL_BLOCK)
    }
}