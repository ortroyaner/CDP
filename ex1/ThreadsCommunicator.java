package ex1;

import static ex1.Message.Direction.*;

/**
 * Created by Or Troyaner on 11/11/2017.
 */
/* This class controls the messages passing and communication between parallel threads */
public class ThreadsCommunicator {
    /* holds a bank of messages for thread's produce-consume */
    private MessagesBank[][] messagesBank;
    private int vSplit;
    private int hSplit;

    public ThreadsCommunicator(int vSplit, int hSplit) {
        this.vSplit = vSplit;
        this.hSplit = hSplit;
        messagesBank = new MessagesBank[this.vSplit][this.hSplit];
        /* initiation of empty banks to the banks matrix */
        for (int i = 0; i < this.vSplit; i++) {
            for (int j = 0; j < this.hSplit; j++) {
                messagesBank[i][j] = new MessagesBank();
            }
        }
    }

    public Message getMessageFromBank(int row, int col, int generation) {
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
                messagesBank[fromThreadRow][fromThreadCol + 1].insertMessage(messageToInsert);
                break;
            case DOWN:
                messageToInsert.setDirection(UP);
                messagesBank[fromThreadRow + 1][fromThreadCol].insertMessage(messageToInsert);
                break;
            case LEFT:
                messageToInsert.setDirection(RIGHT);
                messagesBank[fromThreadRow][fromThreadCol - 1].insertMessage(messageToInsert);
                break;
            default:
                break;
        }
    }

    public int calcNumOfNeighbourThreads(int threadRow, int threadCol) {
        if (isThreadIsCorner(threadRow, threadCol)) return 3;
        if (isThreadOnEdge(threadRow, threadCol)) return 5;
        return 8;
    }

    private boolean isThreadOnEdge(int threadRow, int threadCol) {
        return (isUpEdge(threadRow, threadCol)
        || isRightEdge(threadRow, threadCol)
        || isDownEdge(threadRow, threadCol)
        || isLeftEdge(threadRow, threadCol));
    }

    private boolean isUpEdge(int threadRow, int threadCol) {
        return (!isThreadIsCorner(threadRow, threadCol) && threadRow == 0);
    }

    private boolean isRightEdge(int threadRow, int threadCol) {
        return (!isThreadIsCorner(threadRow, threadCol) && threadCol == this.vSplit - 1);
    }

    private boolean isDownEdge(int threadRow, int threadCol) {
        return (!isThreadIsCorner(threadRow, threadCol) && threadRow == this.hSplit - 1);
    }

    private boolean isLeftEdge(int threadRow, int threadCol) {
        return (!isThreadIsCorner(threadRow, threadCol) && threadCol == 0);
    }

    private boolean isThreadIsCorner(int threadRow, int threadCol) {
        return (isUpRightCorner(threadRow, threadCol)
        || isDownRightCorner(threadRow, threadCol)
        || isDownLeftCorner(threadRow, threadCol)
        || isUpLeftCorner(threadRow, threadCol));
    }

    private boolean isUpRightCorner(int threadRow, int threadCol) {
        return (threadRow == 0 && threadCol == this.vSplit - 1);
    }

    private boolean isDownRightCorner(int threadRow, int threadCol) {
        return (threadRow == this.hSplit - 1 && threadCol == this.vSplit - 1);
    }

    private boolean isDownLeftCorner(int threadRow, int threadCol) {
        return (threadRow == this.hSplit - 1 && threadCol == 0);
    }

    private boolean isUpLeftCorner(int threadRow, int threadCol) {
        return (threadRow == 0 && threadCol == 0);
    }
}
