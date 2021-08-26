package online.kalkr.slapmap.func;

import net.minecraft.util.math.*;

import java.util.function.Function;

public class OrientationManager {

    public BlockBox box;
    public int rotation = 0;
    private Function<BlockPos, Integer> relPosFunc;

    public OrientationManager(BlockPos position, int width, int height, Direction blockFace, Direction playerDirection) {
        switch (blockFace) {
            case NORTH:
                box = new BlockBox(position.getX(), position.getY(), position.getZ()-1,  position.getX()-width+1, position.getY()+height-1, position.getZ()-1);
                relPosFunc = (newPos) -> (newPos.getY() - position.getY())*width + position.getX() - newPos.getX();
                return;
            case SOUTH:
                box =  new BlockBox(position.getX(), position.getY(), position.getZ()+1,  position.getX()+width-1, position.getY()+height-1, position.getZ()+1);
                relPosFunc = (newPos) -> (newPos.getY() - position.getY())*width + newPos.getX() - position.getX();
                return;
            case EAST:
                box =  new BlockBox(position.getX()+1, position.getY(), position.getZ() ,position.getX()+1, position.getY()+height-1, position.getZ()-width+1);
                relPosFunc = (newPos) -> (newPos.getY() - position.getY())*width + position.getZ() - newPos.getZ();
                return;
            case WEST:
                box =  new BlockBox(position.getX()-1, position.getY(), position.getZ(), (position.getX()-1), position.getY()+height-1, position.getZ()+width-1);
                relPosFunc = (newPos) -> (newPos.getY() - position.getY())*width + newPos.getZ() - position.getZ();
                return;
            case UP:
                switch (playerDirection) {
                    case NORTH:
                        box =  new BlockBox(position.getX(), position.getY()+1, position.getZ(), position.getX()+width-1, position.getY()+1, position.getZ()-height+1);
                        relPosFunc = (newPos) -> (position.getZ() - newPos.getZ())*width + newPos.getX() - position.getX();
                        return;
                    case SOUTH:
                        box =  new BlockBox(position.getX(), position.getY()+1, position.getZ(), position.getX()-width+1, position.getY()+1, position.getZ()+height-1);
                        relPosFunc = (newPos) -> (newPos.getZ() - position.getZ())*width + position.getX() - newPos.getX();
                        rotation = 2;
                        return;
                    case EAST:
                        box =  new BlockBox(position.getX(), position.getY()+1, position.getZ(), position.getX()+height-1, position.getY()+1, position.getZ()+width-1);
                        relPosFunc = (newPos) -> (newPos.getX() - position.getX())*width + newPos.getZ() - position.getZ();
                        rotation = 1;
                        return;
                    case WEST:
                        box =  new BlockBox(position.getX(), position.getY()+1, position.getZ(), position.getX()-height+1, position.getY()+1, position.getZ()-width+1);
                        relPosFunc = (newPos) -> (position.getX() - newPos.getX())*width + position.getZ() - newPos.getZ();
                        rotation = 3;
                        return;
                }
            case DOWN:
                switch (playerDirection) {
                    case NORTH:
                        box =  new BlockBox(position.getX(), position.getY()-1, position.getZ(), position.getX()+width-1, position.getY()-1, position.getZ()+height-1);
                        relPosFunc = (newPos) -> (newPos.getZ() - position.getZ())*width + newPos.getX() - position.getX();
                        return;
                    case SOUTH:
                        box =  new BlockBox(position.getX(), position.getY()-1, position.getZ(), position.getX()-width+1, position.getY()-1, position.getZ()-height+1);
                        relPosFunc = (newPos) -> (position.getZ() - newPos.getZ())*width + position.getX() - newPos.getX();
                        rotation = 2;
                        return;
                    case EAST:
                        box =  new BlockBox(position.getX(), position.getY()-1, position.getZ(), position.getX()-height+1, position.getY()-1, position.getZ()+width-1);
                        relPosFunc = (newPos) -> (position.getX() - newPos.getX())*width + newPos.getZ() - position.getZ();
                        rotation = 3;
                        return;
                    case WEST:
                        box =  new BlockBox(position.getX(), position.getY()-1, position.getZ(), position.getX()+height-1, position.getY()-1, position.getZ()-width+1);
                        relPosFunc = (newPos) -> (newPos.getX() - position.getX())*width + position.getZ() - newPos.getZ();
                        rotation = 1;
                }
        }
    }

    public final int relPos(BlockPos newPos) {
        return relPosFunc.apply(newPos);
    }
}
