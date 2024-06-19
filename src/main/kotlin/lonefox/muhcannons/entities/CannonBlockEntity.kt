package lonefox.muhcannons.entities

import lonefox.muhcannons.Muhcannons
import lonefox.muhcannons.blocks.CannonBlock
import lonefox.muhcannons.items.CannonBallItem
import lonefox.muhcannons.items.ItemsRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class CannonBlockEntity(pos: BlockPos?, state: BlockState?) : BlockEntity(BlockEntitiesRegistry.CANNON_BLOCK_ENTITY, pos, state), ImplementedInventory {
    private val inventory = DefaultedList.ofSize(1, ItemStack.EMPTY)
    private var cooldown = 0

    override fun markDirty() {
        super<BlockEntity>.markDirty()
    }

    override fun getItems(): DefaultedList<ItemStack> {
        return inventory
    }

    override fun writeNbt(nbt: NbtCompound?) {
        nbt?.putInt("cannon_block.cooldown", cooldown)
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inventory)
    }

    override fun readNbt(nbt: NbtCompound?) {
        Inventories.readNbt(nbt, inventory)
        super.readNbt(nbt)
        if(nbt != null){
            cooldown = nbt.getInt("cannon_block.cooldown")
        }
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }

    fun fire() {
        val cannonBallStack = getStack(0)
        val cannonBallCoolDown = (cannonBallStack.item as CannonBallItem).cooldown
        cannonBallStack.decrement(1)
        cooldown = cannonBallCoolDown
    }

    fun canFire(): Boolean {
        return cooldown == 0 && hasLoadedCannonBall()
    }

    private fun hasLoadedCannonBall(): Boolean {
        return getStack(0).item == ItemsRegistry.CANNON_BALL
    }

    fun getLoadedCannonBall(): CannonBallItem? {
        return getStack(0).item as CannonBallItem
    }

    override fun canInsert(slot: Int, stack: ItemStack?, side: Direction?): Boolean {
        if(slot == 0){
            val cannonBallItem = ItemsRegistry.CANNON_BALL
            return stack?.item == cannonBallItem
        }
        return true
    }

    companion object {
        fun tick(world: World, pos: BlockPos, state: BlockState, entity: CannonBlockEntity) {
            if(entity.cooldown > 0) {
                val cannonBlock = state.block
                if(cannonBlock is CannonBlock){
                    val outputPos = cannonBlock.getOutputLocation(world, pos, state)
                    if(outputPos != null){
                        world.addParticle(ParticleTypes.SMOKE, outputPos.x, outputPos.y + 0.25, outputPos.z, 0.0, 0.02, 0.0)
                    }
                }
                entity.cooldown -= 1
                if(entity.cooldown <= 0){
                    world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 4.0f, 0.75f)
                }
            }
        }
    }
}