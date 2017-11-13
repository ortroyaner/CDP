package ex1;

import java.util.ArrayList;

/**
 * Created by ortroyaner on 11/11/2017.
 */
/* This class describe a message that threads can produce and consume */
public class Message {
    private ArrayList<Boolean> cells;
    private Direction direction;
    private Integer generation;

    public enum Direction {
        UP, RIGHT, DOWN, LEFT
    }

    public Message(ArrayList<Boolean> cells, Direction direction, Integer generation) {
        this.cells = cells;
        this.direction = direction;
        this.generation = generation;
    }

    public Direction getDirection() {
        return direction;
    }

    public Integer getGeneration() {
        return generation;
    }

    public ArrayList<Boolean> getCells() {
        return cells;
    }
}
