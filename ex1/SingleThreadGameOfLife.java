package ex1;

import java.util.ArrayList;
import java.util.Arrays;

public class SingleThreadGameOfLife implements Runnable {

    public enum BlockMatrixDirection {
        INNER, UP_RIGHT_CORNER, RIGHT_EDGE, DOWN_RIGHT_CORNER, DOWN_EDGE, DOWN_LEFT_CORNER, LEFT_EDGE, UP_LEFT_CORNER,
        UP_EDGE
    }

    Boolean[][] currTable, prevTable;
    int currGen, generations;
    Index startIndex, endIndex;
    int rows, cols;
    int threadRow, threadCol;
    int originRows, originCols;
    ThreadsCommunicator communicator;

    public SingleThreadGameOfLife(boolean[][] initialTable, Index startIndex, Index endIndex,
                                  int threadRow, int threadCol, int originRows, int originCols, int generations, ThreadsCommunicator communicator) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.rows = endIndex.row - startIndex.row;
        this.cols = endIndex.col - startIndex.col;
        this.generations = generations;
        this.currGen = 0;
        this.threadRow = threadRow;
        this.threadCol = threadCol;
        this.originRows = originRows;
        this.originCols = originCols;
        this.communicator = communicator;
        this.prevTable = createMiniTable(convertToBoolean(initialTable), startIndex, endIndex, true);
        this.currTable = createBlankTable();
    }

    private Boolean[][] convertToBoolean(boolean[][] initialTable) {
        Boolean[][] tmp = new Boolean[originRows][originCols];
        for (int i = 0; i < originRows; i++) {
            for (int j = 0; j < originCols; j++) {
                tmp[i][j] = initialTable[i][j];
            }
        }
        return tmp;
    }

    // this method will "play the game"
    @Override
    public void run() {
        // as long as we have more generation to compute
        while (this.currGen < this.generations) {
            // calc the cells we can do independently
            System.out.println("Thread [" + threadRow + "][" + threadCol + "] is calling updateCells()"); //TODO: delete
            updateCells();

            // update neighbors on cells in edges
            System.out.println("Thread [" + threadRow + "][" + threadCol + "] is calling updateNeighbors()"); //TODO: delete
            updateNeighbors();

            // if we're in generation 0, we're done. we know everything from gen=-1since this is the initialTable, so we don't need to
            // ask for information from other threads.
            if (currGen == 0) {
                // switch tables (prev <= current , current should be blank)
                System.out.println("Thread [" + threadRow + "][" + threadCol + "] is calling switchTables()"); //TODO: delete
                switchTables();

                continue;
            }
            // check for cells from neighbors
            System.out.println("Thread [" + threadRow + "][" + threadCol + "] is calling updateTableFromNeighbors()"); //TODO: delete
            updateTableFromNeighbors();

            // update neighbors on cells in edges
            System.out.println("Thread [" + threadRow + "][" + threadCol + "] is calling updateNeighbors()"); //TODO: delete
            updateNeighbors();

            // switch tables (prev <= current , current should be blank)
            System.out.println("Thread [" + threadRow + "][" + threadCol + "] is calling switchTables()"); //TODO: delete
            switchTables();
        }

        // update original tables
        System.out.println("Thread [" + threadRow + "][" + threadCol + "] is calling updateTable()"); //TODO: delete
        Results.updateTable(Results.TableKind.LAST, currTable, startIndex, endIndex);
        Results.updateTable(Results.TableKind.PREV, returnToRealSize(prevTable), startIndex, endIndex);

    }

    private Boolean[][] returnToRealSize(Boolean[][] prevTable) {
        Boolean[][] realSizePrev = new Boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                realSizePrev[i][j] = prevTable[i + 1][j + 1];
            }
        }
        return realSizePrev;
    }

    // this method will update the current board form data from other neighbors
    private void updateTableFromNeighbors() {
        int currNeighborsStatus = 0, numOfNeighbors = communicator.calcNumOfNeighbourThreads(threadRow, threadCol);

        // ask for data from communicator about other neighbors
        // keep getting data, until you got all the data needed
        while (currNeighborsStatus < numOfNeighbors) {
            Message message = communicator.getMessageFromBank(threadRow, threadCol, currGen - 1);
            // update prev table with this data
            switch (message.getDirection()) {
                case UP:
                    // fill up the first row in the bigger table
                    for (int i = 0; i < cols + 2; i++) {
                        prevTable[0][i] = message.getCells().get(i);
                    }
                    break;
                case RIGHT:
                    // fill up the right col in the bigger table
                    for (int i = 0; i < rows + 2; i++) {
                        prevTable[rows + 1][i] = message.getCells().get(i);
                    }
                    break;
                case DOWN:
                    // fill up the right col in the bigger table
                    for (int i = 0; i < cols + 2; i++) {
                        prevTable[cols + 1][i] = message.getCells().get(i);
                    }
                    break;
                case LEFT:
                    // fill up the right col in the bigger table
                    for (int i = 0; i < rows + 2; i++) {
                        prevTable[i][0] = message.getCells().get(i);
                    }
                    break;
                case UPLEFT:
                    // fill up the up-left cell in the bigger table
                    prevTable[0][0] = message.getCells().get(0);
                    break;
                case UPRIGHT:
                    // fill up the up-right cell in the bigger table
                    prevTable[0][cols - 1] = message.getCells().get(0);
                    break;
                case DOWNRIGHT:
                    // fill up the down-right cell in the bigger table
                    prevTable[rows - 1][cols - 1] = message.getCells().get(0);
                    break;
                case DOWNLEFT:
                    // fill up the down-left cell in the bigger table
                    prevTable[rows - 1][0] = message.getCells().get(0);
                    break;
            }
        }

        // update current board
        updateCells();

    }

    // send edges to communicator
    private void updateNeighbors() {
        System.out.println("updateNeighbors"); // TODO: delete
        if (startIndex.row != 0) {
            //send the first row in curr table
            ArrayList<Boolean> row = new ArrayList<>(Arrays.asList(currTable[0]));
            Message message = new Message(row, Message.Direction.UP, currGen);
            communicator.insertToBank(threadRow, threadCol, message);
        }
        if (startIndex.col != 0) {
            //send the first col in curr table
            ArrayList<Boolean> col = new ArrayList<>();
            for (int row = 0; row < rows; row++) {
                col.add(currTable[row][0]);
            }
            Message message = new Message(col, Message.Direction.LEFT, currGen);
            communicator.insertToBank(threadRow, threadCol, message);
        }
        if (endIndex.row != originRows) {
            //send the last row in curr table
            ArrayList<Boolean> col = new ArrayList<>();
            for (int row = 0; row < rows; row++) {
                col.add(currTable[row][cols - 1]);
            }
            Message message = new Message(col, Message.Direction.DOWN, currGen);
            communicator.insertToBank(threadRow, threadCol, message);
        }
        if (endIndex.col != originCols) {
            //send the last col in curr table
            ArrayList<Boolean> row = new ArrayList<>(Arrays.asList(currTable[rows - 1]));
            Message message = new Message(row, Message.Direction.RIGHT, currGen);
            communicator.insertToBank(threadRow, threadCol, message);
        }
    }

    // update the cells we already know
    private void updateCells() {
        System.out.println("updateCells"); // TODO: delete
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                updateCell(i, j);
            }
        }
    }

    // update a single cell
    private void updateCell(int row, int col) {
        //System.out.println("Thread ["+threadRow+"]["+threadCol+"] is in updateCell"); // TODO: delete
        int liveNeighbors = 0, deadNeighbors = 0, unknownNeighbors = 0;
        int neiCol, neiRow;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    // we are int the original cell that we're testing
                    continue;
                }
                neiRow = (row == 0) ? 0 : row - i;
                neiCol = (col == 0) ? 0 : col - j;
                if (prevTable[neiRow][neiCol] == false) {
                    deadNeighbors++;
                }
                if (prevTable[neiRow][neiCol] == true) {
                    liveNeighbors++;
                }
                unknownNeighbors = 8 - deadNeighbors - liveNeighbors;

                //dead because of over crowded
                if (liveNeighbors > 3) {
                    currTable[row][col] = false;
                    return;
                }
                //live with exactly 3 live neighbors
                if (liveNeighbors == 3 && unknownNeighbors == 0) {
                    currTable[row][col] = true;
                    return;
                }
                //live with exactly 2 live neighbors + live before
                if (prevTable[row + 1][col + 1] && liveNeighbors == 2 && unknownNeighbors == 0) {
                    currTable[row][col] = true;
                    return;
                }

            }
        }
    }

    // this method will check if we're in the big table and if we are in the current table
    private boolean inBound(int row, int col) {
        if (row < 0 || row < this.startIndex.row || row >= this.endIndex.row ||
                col < 0 || col < this.startIndex.col || col >= this.endIndex.col) {
            return false;
        }
        return true;
    }

    // fill the prev table with values
    private Boolean[][] createMiniTable(Boolean[][] initialTable, Index startIndex, Index endIndex, boolean isInitial) {
        // create the prev table with the borders
        int numRowsOfInitTable = endIndex.row - startIndex.row, numColsOfInitTable = endIndex.col - startIndex.col;
        int numRowsOfExpandedTable = numRowsOfInitTable + 2, numColsOfExpandedTable = numColsOfInitTable + 2;
        Boolean[][] cells = new Boolean[numRowsOfExpandedTable][numColsOfExpandedTable];
        for (int i = 0; i < numRowsOfExpandedTable; i++) {
            for (int j = 0; j < numColsOfExpandedTable; j++) {
                if (i == 0 || i == numRowsOfExpandedTable - 1 || j == 0 || j == numColsOfExpandedTable - 1) {
                    //this is the frame we added. no need to copy from initial board game
                    continue;
                }
                cells[i][j] = initialTable[i + startIndex.row - 1][j + startIndex.col - 1];
            }
        }

        //Fill edges
        BlockMatrixDirection dir = communicator.getThreadBlockMatrixLocation(threadRow, threadCol);
        cells[0][0] = cells[0][numColsOfExpandedTable-1] =
                cells[numRowsOfExpandedTable-1][numColsOfExpandedTable-1] = cells[numRowsOfExpandedTable-1][0]=false;
        switch (dir) {
            case INNER:
                //fill by rows
                for (int i = 0; i < numRowsOfExpandedTable; i++) {
                    if (isInitial) {
                        // left & right cols
                        cells[i][0] = initialTable[i + startIndex.row - 1][startIndex.col - 1];
                        cells[i][numColsOfExpandedTable - 1] = initialTable[i + startIndex.row - 1][endIndex.col];
                    } else {
                        cells[i][0] = null;
                        cells[i][numColsOfExpandedTable - 1] = null;
                    }
                }
                //fill by cols
                for (int j = 0; j < numColsOfExpandedTable; j++) {
                    if (isInitial) {
                        //upper & lower rows
                        cells[0][j] = initialTable[startIndex.row - 1][j + startIndex.col - 1];
                        cells[numRowsOfExpandedTable - 1][j] = initialTable[endIndex.row][j + startIndex.col - 1];
                    } else {
                        cells[0][j] = null;
                        cells[numRowsOfExpandedTable - 1][j] = null;
                    }
                }
                break;
            case UP_RIGHT_CORNER:
                //fill by rows
                for (int i = 0; i < numRowsOfExpandedTable; i++) {
                    cells[i][numColsOfExpandedTable - 1] = false; //no original border
                        if (isInitial && i!=0) {
                            cells[i][0] = initialTable[i + startIndex.row - 1][startIndex.col - 1];
                        } else {
                            cells[i][0] = null;
                        }
                }
                //fill by cols
                for (int j = 0; j < numColsOfExpandedTable; j++) {
                    cells[0][j] = false;
                        if (isInitial && j!=0) {
                            cells[numRowsOfExpandedTable - 1][j] = initialTable[endIndex.row][j + startIndex.col - 1];
                        } else {
                            cells[numRowsOfExpandedTable - 1][j] = null;
                        }
                }
                break;
            case DOWN_RIGHT_CORNER:
                //fill by rows
                for (int i = 0; i < numRowsOfExpandedTable; i++) {
                    cells[i][numColsOfExpandedTable - 1] = false; //no original border
                        if (isInitial && i!=0) {
                            cells[i][0] = initialTable[i + startIndex.row - 1][startIndex.col - 1];
                        } else {
                            cells[i][0] = null;
                        }
                }
                //fill by cols
                for (int j = 0; j < numColsOfExpandedTable; j++) {
                    cells[numRowsOfExpandedTable-1][j] = false;
                        if (isInitial && j!=0) {
                            cells[0][j] = initialTable[startIndex.row-1][j + startIndex.col - 1];
                        } else {
                            cells[0][j] = null;
                        }
                }
                break;
            case DOWN_LEFT_CORNER:
                //fill by rows
                for (int i = 0; i < numRowsOfExpandedTable; i++) {
                    cells[i][0] = false; //no original border
                        if (isInitial && i!=0) {
                            cells[i][numColsOfExpandedTable-1] = initialTable[i + startIndex.row - 1][endIndex.col];
                        } else {
                            cells[i][numColsOfExpandedTable-1] = null;
                        }
                }
                //fill by cols
                for (int j = 0; j < numColsOfExpandedTable; j++) {
                    cells[numRowsOfExpandedTable-1][j] = false;
                        if (isInitial && j!=0) {
                            cells[0][j] = initialTable[startIndex.row - 1][j + startIndex.col - 1];
                        } else {
                            cells[0][j] = null;
                        }
                }
                break;
            case UP_LEFT_CORNER:
                //fill by rows
                for (int i = 0; i < numRowsOfExpandedTable; i++) {
                    cells[i][0] = false; //no original border
                        if (isInitial && i!=0) {
                            cells[i][numColsOfExpandedTable-1] = initialTable[i + startIndex.row - 1][endIndex.col];
                        } else {
                            cells[i][numColsOfExpandedTable-1] = null;
                        }
                }
                //fill by cols
                for (int j = 0; j < numColsOfExpandedTable; j++) {
                    cells[0][j] = false;
                        if (isInitial && j!=0) {
                            cells[numRowsOfExpandedTable-1][j] = initialTable[endIndex.row][j + startIndex.col - 1];
                        } else {
                            cells[numRowsOfExpandedTable-1][j] = null;
                        }
                }
                break;
            case RIGHT_EDGE:
                //fill by rows
                for (int i = 0; i < numRowsOfExpandedTable; i++) {
                    cells[i][numColsOfExpandedTable-1] = false; //no original border
                    if (isInitial && i!=0) {
                        cells[i][0] = initialTable[i + startIndex.row - 1][startIndex.col - 1];
                    } else {
                        cells[i][0] = null;
                    }
                }
                //fill by cols
                for (int j = 0; j < numColsOfExpandedTable-1; j++) {
                    if (isInitial && j!=0) {
                        cells[0][j] = initialTable[startIndex.row - 1][j + startIndex.col - 1];
                        cells[numRowsOfExpandedTable - 1][j] = initialTable[endIndex.row][j + startIndex.col - 1];
                    } else {
                        cells[0][j] = null;
                        cells[numRowsOfExpandedTable - 1][j] = null;
                    }
                }
                break;
            case DOWN_EDGE:
                //fill by rows
                for (int i = 0; i < numRowsOfExpandedTable-1; i++) {
                    if (isInitial && i!=0) {
                        cells[i][0] = initialTable[i + startIndex.row - 1][startIndex.col - 1];
                        cells[i][numColsOfExpandedTable - 1] = initialTable[i + startIndex.row - 1][endIndex.col];
                    } else {
                        cells[i][0] = null;
                        cells[i][numColsOfInitTable + 1] = null;
                    }
                }
                //fill by cols
                for (int j = 0; j < numColsOfExpandedTable; j++) {
                    cells[numRowsOfExpandedTable-1][j] = false;
                    if (isInitial && j!=0) {
                        cells[0][j] = initialTable[startIndex.row - 1][j + startIndex.col - 1];
                    } else {
                        cells[0][j] = null;
                    }
                }
                break;
            case LEFT_EDGE:
                //fill by rows
                for (int i = 0; i < numRowsOfExpandedTable; i++) {
                    cells[i][0] = false; //no original border
                    if (isInitial && i!=0) {
                        cells[i][numColsOfExpandedTable-1] = initialTable[i + startIndex.row - 1][endIndex.col];
                    } else {
                        cells[i][numColsOfExpandedTable-1] = null;
                    }
                }
                //fill by cols
                for (int j = 1; j < numColsOfExpandedTable; j++) {
                    if (isInitial && j!=0) {
                        cells[0][j] = initialTable[startIndex.row - 1][j + startIndex.col - 1];
                        cells[numRowsOfExpandedTable - 1][j] = initialTable[endIndex.row][j + startIndex.col - 1];
                    } else {
                        cells[0][j] = null;
                        cells[numRowsOfExpandedTable - 1][j] = null;
                    }
                }
                break;
            case UP_EDGE:
                //fill by rows
                for (int i = 1; i < numRowsOfExpandedTable; i++) {
                    if (isInitial && i!=0) {
                        cells[i][0] = initialTable[i + startIndex.row - 1][startIndex.col - 1];
                        cells[i][numColsOfExpandedTable - 1] = initialTable[i + startIndex.row - 1][endIndex.col];
                    } else {
                        cells[i][0] = null;
                        cells[i][numColsOfInitTable + 1] = null;
                    }
                }
                //fill by cols
                for (int j = 0; j < numColsOfExpandedTable; j++) {
                    cells[0][j] = false;
                    if (isInitial && j!=0) {
                        cells[numRowsOfExpandedTable - 1][j] = initialTable[endIndex.row][j + startIndex.col - 1];
                    } else {
                        cells[numRowsOfExpandedTable - 1][j] = null;
                    }
                }
                break;
            default:
                break;
        }

        return cells;
    }

    // empty the current table for a new round
    private Boolean[][] createBlankTable() {
        Boolean[][] cells = new Boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = null;
            }
        }
        return cells;
    }

    // switch tables
    private void switchTables() {
        prevTable = createMiniTable(currTable, new Index(0, 0), new Index(rows, cols), false);
        this.currTable = createBlankTable();
        this.currGen++;
    }
}
