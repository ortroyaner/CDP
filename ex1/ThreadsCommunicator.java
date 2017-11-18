package ex1;

import static ex1.Message.Direction.*;

/**
 * Created by Or Troyaner on 11/11/2017.
 */
/* This class controls the messages passing and communication between parallel threads */
class ThreadsCommunicator {
    /* holds a bank of messages for thread's produce-consume */
    private MessagesBank[][] messagesBank;
    private int vSplit;
    private int hSplit;

    ThreadsCommunicator(int vSplit, int hSplit) {
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

    Message getMessageFromBank(int row, int col, int generation) {
        Message returnMsg = messagesBank[row][col].getAndRemoveMessage(generation);
        System.out.println("Thread [" + row + "][" + col + "] got a message: \n" + returnMsg.toString()); //TODO: delete
        return returnMsg;
    }

    void insertToBank(int fromThreadRow, int fromThreadCol, Message messageToInsert) {
        System.out.println("Thread [" + fromThreadRow + "][" + fromThreadCol + "]" +
                " inserts a message: " + messageToInsert.toString()); //TODO: delete

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
            // Corners:
            case UPRIGHT:
                messageToInsert.setDirection(DOWNLEFT); //change the message direction perspective
                messagesBank[fromThreadRow - 1][fromThreadCol + 1].insertMessage(messageToInsert);
                break;
            case DOWNRIGHT:
                messageToInsert.setDirection(UPLEFT);
                messagesBank[fromThreadRow + 1][fromThreadCol + 1].insertMessage(messageToInsert);
                break;
            case DOWNLEFT:
                messageToInsert.setDirection(UPRIGHT);
                messagesBank[fromThreadRow + 1][fromThreadCol - 1].insertMessage(messageToInsert);
                break;
            case UPLEFT:
                messageToInsert.setDirection(DOWNRIGHT);
                messagesBank[fromThreadRow - 1][fromThreadCol - 1].insertMessage(messageToInsert);
                break;
            default:
                break;
        }
    }

    int calcNumOfNeighbourThreads(int threadRow, int threadCol) {
        if (vSplit==0 || hSplit == 0) return 0;
        if (vSplit==1 && hSplit ==1) return 0;
        if (vSplit==1) //One row
        {
            if (threadCol==0 || threadCol ==hSplit-1) return 1;
            else return 2;
        }
        if (hSplit==1) //One col
        {
            if (threadRow==0 || threadRow ==vSplit-1) return 1;
            else return 2;
        }
        if (isThreadIsCorner(threadRow, threadCol)) return 3;
        if (isThreadOnEdge(threadRow, threadCol)) return 5;
        return 8;
    }

    SingleThreadGameOfLife.BlockMatrixDirection getThreadBlockMatrixLocation(int threadRow, int threadCol) {
        //Corners
        if (isUpRightCorner(threadRow,threadCol)) return SingleThreadGameOfLife.BlockMatrixDirection.UP_RIGHT_CORNER;
        if (isDownRightCorner(threadRow,threadCol)) return SingleThreadGameOfLife.BlockMatrixDirection.DOWN_RIGHT_CORNER;
        if (isDownLeftCorner(threadRow,threadCol)) return SingleThreadGameOfLife.BlockMatrixDirection.DOWN_LEFT_CORNER;
        if (isUpLeftCorner(threadRow,threadCol)) return SingleThreadGameOfLife.BlockMatrixDirection.UP_LEFT_CORNER;

        //Edges
        if (isRightEdge(threadRow,threadCol)) return SingleThreadGameOfLife.BlockMatrixDirection.RIGHT_EDGE;
        if (isDownEdge(threadRow,threadCol)) return SingleThreadGameOfLife.BlockMatrixDirection.DOWN_EDGE;
        if (isLeftEdge(threadRow,threadCol)) return SingleThreadGameOfLife.BlockMatrixDirection.LEFT_EDGE;
        if (isUpEdge(threadRow,threadCol)) return SingleThreadGameOfLife.BlockMatrixDirection.UP_EDGE;

        //Inner
        return SingleThreadGameOfLife.BlockMatrixDirection.INNER;
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
        return (!isThreadIsCorner(threadRow, threadCol) && threadCol == this.hSplit - 1);
    }

    private boolean isDownEdge(int threadRow, int threadCol) {
        return (!isThreadIsCorner(threadRow, threadCol) && threadRow == this.vSplit - 1);
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
        return (threadRow == 0 && threadCol == this.hSplit - 1);
    }

    private boolean isDownRightCorner(int threadRow, int threadCol) {
        return (threadRow == this.vSplit - 1 && threadCol == this.hSplit - 1);
    }

    private boolean isDownLeftCorner(int threadRow, int threadCol) {
        return (threadRow == this.vSplit - 1 && threadCol == 0);
    }

    private boolean isUpLeftCorner(int threadRow, int threadCol) {
        return (threadRow == 0 && threadCol == 0);
    }
}
