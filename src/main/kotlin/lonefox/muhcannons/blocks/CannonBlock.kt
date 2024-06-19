package lonefox.muhcannons.blocks

import lonefox.muhcannons.Muhcannons
import lonefox.muhcannons.entities.BlockEntitiesRegistry
import lonefox.muhcannons.entities.CannonBallEntity
import lonefox.muhcannons.entities.CannonBlockEntity
import lonefox.muhcannons.events.SoundEventsRegistry
import lonefox.muhcannons.items.ItemsRegistry
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class CannonBlock(settings: Settings?) : BlockWithEntity(settings) {

    init {
        defaultState = (stateManager.defaultState.with(FACING, Direction.NORTH)).with(FIRED, false)
    }

    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity?,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        if(!world?.isClient!!){
            val isHoldingCannonBall = player?.isHolding(ItemsRegistry.CANNON_BALL)
            val cannonBlockEntity: Inventory = world.getBlockEntity(pos) as Inventory
            if(player != null && isHoldingCannonBall == true){
                val cannonBallStack = player.getStackInHand(hand)
                if(cannonBallStack.isEmpty) return ActionResult.SUCCESS
                if(cannonBlockEntity.isEmpty){
                    cannonBlockEntity.setStack(0, cannonBallStack.copyAndEmpty())
                    world.updateComparators(pos, this)
                }else if(cannonBlockEntity.getStack(0).count != cannonBlockEntity.maxCountPerStack){
                    val availableStackAmount = cannonBlockEntity.maxCountPerStack - cannonBlockEntity.getStack(0).count
                    if(availableStackAmount > cannonBallStack.count){
                        cannonBlockEntity.getStack(0).increment(cannonBallStack.count)
                        cannonBallStack.count = 0
                    } else{
                        cannonBlockEntity.getStack(0).increment(availableStackAmount)
                        cannonBallStack.decrement(availableStackAmount)
                    }
                    world.updateComparators(pos, this)
                }
            }else{
                fireProjectile(state, pos, world)
            }
        }
        return ActionResult.SUCCESS
    }

    private fun fireProjectile(
        state: BlockState?,
        pos: BlockPos?,
        world: World?
    ) {
        val cannonBlockEntity = world?.getBlockEntity(pos) as CannonBlockEntity?
        if(cannonBlockEntity?.canFire() == false) return
        val position = getOutputLocation(world, pos, state)
        if (position != null) {
            //val arrowEntity = ArrowEntity(world, position.x, position.y, position.z)
            val cannonBallItem = cannonBlockEntity?.getLoadedCannonBall() ?: return
            val cannonBallEntity = CannonBallEntity(world, cannonBallItem.entityDamage, cannonBallItem.piercingValue, cannonBlockEntity, position.x, position.y, position.z)
            val facingDirection = getFacingDirection(state)
            if (facingDirection != null) {
                cannonBallEntity.setVelocity(
                    facingDirection.vector.x.toDouble(),
                    facingDirection.vector.y.toDouble(),
                    facingDirection.vector.z.toDouble(),
                    2.0f,
                    0.0f
                )
            }
            world?.playSound(null, pos, SoundEventsRegistry.CANNON_FIRE_SOUND_EVENT, SoundCategory.BLOCKS, 4f, 1.0f)
            world?.spawnEntity(cannonBallEntity)
            cannonBlockEntity.fire()
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(FACING, FIRED)
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return defaultState.with(FACING, ctx?.horizontalPlayerFacing)
    }

    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity? {
        return CannonBlockEntity(pos, state)
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    private fun getFacingDirection(state: BlockState?): Direction? {
        return state?.get(FACING)
    }

    fun getOutputLocation(world: World?, pos: BlockPos?, state: BlockState?): Vec3d? {
        if(pos == null) return null
        val facingDirection = getFacingDirection(state) ?: return null
        val barrelsOffset = getFirstNonBarrelBlock(world, pos, facingDirection)
        val x = barrelsOffset.x + 0.5 + 0.75 * facingDirection.offsetX
        val y = barrelsOffset.y + 0.25
        val z = barrelsOffset.z + 0.5 + 0.75 * facingDirection.offsetZ
        return Vec3d(x, y, z)
    }

    private fun getFirstNonBarrelBlock(world: World?, pos: BlockPos, facingDirection: Direction, currentIteration: Int = 0): BlockPos {
        val MAX_ITERATIONS = 10
        val nextPos = pos.add(facingDirection.vector)
        val nextState = world?.getBlockState(nextPos)
        val nextBlock = nextState?.block
        return if((nextBlock is AbstractCannonBarrelBlock || nextBlock is CannonBlock) && currentIteration < MAX_ITERATIONS){
            getFirstNonBarrelBlock(world, nextPos, facingDirection, currentIteration + 1)
        }else{
            /*if(currentIteration > 0){
                    Muhcannons.logger.info("Iterations to find output location $currentIteration")
                }*/
            // if the next block is anything but a barrel or a cannon return the current position
            pos
        }
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState? {
        return state.with(FACING, rotation.rotate(state.get(FACING))) as BlockState
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState? {
        return state.rotate(mirror.getRotation(state.get(FACING)))
    }

    override fun hasComparatorOutput(state: BlockState?): Boolean {
        return true
    }

    override fun onStateReplaced(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        newState: BlockState?,
        moved: Boolean
    ) {
        if(state?.isOf(newState?.block) == true) return
        val blockEntity = world?.getBlockEntity(pos)
        if(blockEntity is Inventory){
            if(pos == null) return
            ItemScatterer.spawn(world, pos, blockEntity)
            world.updateComparators(pos, this)
        }
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun getComparatorOutput(state: BlockState?, world: World?, pos: BlockPos?): Int {
        val cannonBlockEntity = world?.getBlockEntity(pos) as CannonBlockEntity
        return ((cannonBlockEntity.getStack(0).count.toFloat() / cannonBlockEntity.getStack(0).maxCount.toFloat()) * 15).toInt()
    }

    override fun neighborUpdate(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        sourceBlock: Block?,
        sourcePos: BlockPos?,
        notify: Boolean
    ) {
        if(pos == null) return
        val isPowered = world?.isReceivingRedstonePower(pos) == true || world?.isReceivingRedstonePower(pos.up()) == true
        val fired = state?.get(FIRED)
        if(isPowered && fired == false){
            fireProjectile(state, pos, world)
            world?.setBlockState(pos, state.with(FIRED, true), Block.NOTIFY_LISTENERS or Block.FORCE_STATE)
        } else if (!isPowered && fired == true) {
            world?.setBlockState(pos, state.with(FIRED, false), Block.NOTIFY_LISTENERS or Block.FORCE_STATE)
        }
    }

    override fun getStateForNeighborUpdate(
        state: BlockState?,
        direction: Direction?,
        neighborState: BlockState?,
        world: WorldAccess?,
        pos: BlockPos?,
        neighborPos: BlockPos?
    ): BlockState {
        if(neighborState?.block is AbstractCannonBarrelBlock){
        }
        return state!!
    }

    override fun <T : BlockEntity?> getTicker(
        world: World?,
        state: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        return checkType(type, BlockEntitiesRegistry.CANNON_BLOCK_ENTITY, CannonBlockEntity.Companion::tick)
    }

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return VoxelShapes.fullCube()
    }

    companion object {
        val FACING: DirectionProperty = Properties.HORIZONTAL_FACING
        val FIRED: BooleanProperty = Properties.TRIGGERED
    }
}