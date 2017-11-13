package ex1;

import static ex1.Message.Direction.*;

/**
 * Created by Or Troyaner on 11/11/2017.
 */
/* This class controls the messages passing and communication between parallel threads */
public class ThreadsCommunicator {
    /* holds a bank of messages for thread's produce-consume */
    private MessagesBank[][] messagesBank;

    public ThreadsCommunicator(int vSplit, int hSplit) {
        messagesBank = new MessagesBank[vSplit][hSplit];
        /* initiation of empty banks to the banks matrix */
        for (int i = 0; i < vSplit; i++) {
            for (int j = 0; j < hSplit; j++) {
                messagesBank[i][j] = new MessagesBank();
            }
        }
    }

    public Message getMessageFromBank(int row, int col, int generation) throws InterruptedException {
        return messagesBank[row][col].getAndRemoveMessage(generation);
    }

    public void insertToBank(int fromThreadRow, int fromThreadCol, Message messageToInsert) {
        Message.Direction direction = messageToInsert.getDirection();
        switch (direction) {
            case UP:
                messageToInsert.setDirection(DOWN); //change the message direction perspective
                messagesBank[fromThreadRow - 1][fromThreadCol].insertMessage(messageToInsert);
                break;
            case RIGHT:
                messageToInsert.setDirection(LEFT);
                messagesBank[fromThreadRow][fromThreadCol+1].insertMessage(messageToInsert);
                break;
            case DOWN:
                messageToInsert.setDirection(UP);
                messagesBank[fromThreadRow+1][fromThreadCol].insertMessage(messageToInsert);
                break;
            case LEFT:
                messageToInsert.setDirection(RIGHT);
                messagesBank[fromThreadRow][fromThreadCol-1].insertMessage(messageToInsert);
                break;
            default:
                break;
        }
    }
}
