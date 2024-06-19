package lonefox.muhcannons

import lonefox.muhcannons.entities.EntitiesRegistry
import lonefox.muhcannons.render.entities.CannonBallEntityRenderer
import lonefox.muhcannons.render.entities.model.CannonBallEntityModel
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.Identifier

object MuhcannonsClient : ClientModInitializer {
	public val CANNON_BALL_LAYER = EntityModelLayer(Identifier(Muhcannons.MOD_ID, "cannon_ball"), "main")
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRendererRegistry.register(EntitiesRegistry.CANNON_BALL_ENTITY, ::CannonBallEntityRenderer)

		EntityModelLayerRegistry.registerModelLayer(CANNON_BALL_LAYER, CannonBallEntityModel::getTexturedModelData)
	}
}