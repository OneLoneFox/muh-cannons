package lonefox.muhcannons

import lonefox.muhcannons.blocks.BlocksRegistry
import lonefox.muhcannons.entities.BlockEntitiesRegistry
import lonefox.muhcannons.entities.EntitiesRegistry
import lonefox.muhcannons.events.SoundEventsRegistry
import lonefox.muhcannons.items.ItemsRegistry
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemGroups
import org.slf4j.LoggerFactory

object Muhcannons : ModInitializer {
    public val logger = LoggerFactory.getLogger("muh-cannons")
	public const val MOD_ID = "muh-cannons"


	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ItemsRegistry.register()
		BlocksRegistry.register()
		BlockEntitiesRegistry.register()
		EntitiesRegistry.register()
		SoundEventsRegistry.register()

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register {
			it.add(ItemsRegistry.CANNON_BALL)
			it.add(BlocksRegistry.CANNON_BLOCK_ITEM)
			it.add(BlocksRegistry.LARGE_CANNON_BARREL_BLOCK_ITEM)
			it.add(BlocksRegistry.MEDIUM_CANNON_BARREL_BLOCK_ITEM)
			it.add(BlocksRegistry.SMALL_CANNON_BARREL_BLOCK_ITEM)
		}
	}
}