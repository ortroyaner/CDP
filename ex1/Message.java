package ex1;

import java.util.ArrayList;

import static ex1.Message.Direction.DOWN;
import static ex1.Message.Direction.UP;

/**
 * Created by ortroyaner on 11/11/2017.
 */
/* This class describe a message that threads can produce and consume */
class Message {
    private ArrayList<Boolean> cells;
    private Direction direction;
    private Integer generation;

    enum Direction {
        UP, RIGHT, DOWN, LEFT, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT
    }

    Message(ArrayList<Boolean> cells, Direction direction, Integer generation) {
        this.cells = cells;
        this.direction = direction;
        this.generation = generation;
    }

    Direction getDirection() {
        return direction;
    }

    void setDirection(Direction direction) {
        this.direction = direction;
    }

    Integer getGeneration() {
        return generation;
    }

    ArrayList<Boolean> getCells() {
        return cells;
    }

    public String toString() {
        String msg = "Direction: '" + this.direction + "', Generation: '" + this.generation + "', Cells: \n";
        if (direction==UP || direction == DOWN) {
            for (int i = 0; i < cells.size(); i++) {
                msg = msg + (cells.get(i)==null ? "null" : (cells.get(i)==true? "1" : "0")) + " ";
            }
        }
        else {
            for (int i = 0; i < cells.size(); i++) {
                msg = msg + (cells.get(i)==null ? "null" : (cells.get(i)==true? "1" : "0"));
                msg += "\n";
            }
        }
        return msg;
    }
}
