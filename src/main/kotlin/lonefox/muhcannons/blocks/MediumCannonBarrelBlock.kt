package lonefox.muhcannons.blocks

import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class MediumCannonBarrelBlock(settings: Settings?) : AbstractCannonBarrelBlock(settings) {

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        val start = 0.0625
        val end = 0.9375
        return when (state?.get(FACING)) {
            Direction.NORTH, Direction.SOUTH -> VoxelShapes.cuboid(start, start, 0.0, end, end, 1.0)
            Direction.EAST, Direction.WEST -> VoxelShapes.cuboid(0.0, start, start, 1.0, end, end)
            else -> VoxelShapes.fullCube()
        }
    }

}