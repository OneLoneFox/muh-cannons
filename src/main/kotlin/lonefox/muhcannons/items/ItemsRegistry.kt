package lonefox.muhcannons.items

import lonefox.muhcannons.Muhcannons
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ItemsRegistry {
    lateinit var CANNON_BALL: Item

    fun register(){
        CANNON_BALL = Registry.register(
            Registries.ITEM, Identifier(Muhcannons.MOD_ID, "cannon_ball"), CannonBallItem(
                FabricItemSettings()
            ))
    }
}