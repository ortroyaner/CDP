package ex1;

import java.util.ArrayList;

/**
 * Created by ortroyaner on 11/11/2017.
 */
/* This class describe a message that threads can produce and consume */
public class Message {
    public enum Direction {
        UP, RIGHT, DOWN, LEFT, UPPER_RIGHT, LOWER_RIGHT, LOWER_LEFT, UPPER_LEFT
    }
    ArrayList<Boolean> cells;
    Direction direction;
    Integer generation;
}
