package ex1;

import java.util.ArrayList;
import java.util.Arrays;

public class SingleThreadGameOfLife implements Runnable{
    Boolean[][] currTable, prevTable;
    int currGen, generations;
    Index startIndex, endIndex;
    int threadRow, threadCol;
    int originRows, originCols;
    ThreadsCommunicator communicator;

    public SingleThreadGameOfLife(boolean[][] initialTable,Index startIndex, Index endIndex,
                                  int threadRow, int threadCol, int originRows, int originCols, int generations,ThreadsCommunicator communicator){
       this.prevTable = createMiniTable(initialTable,startIndex,endIndex);
       this.currTable = createBlankTable();
       this.generations = generations; this.currGen = 0;
       this.startIndex = startIndex; this.endIndex = endIndex;
       this.threadRow = threadRow; this.threadCol = threadCol;
       this.originRows = originRows; this.originCols = originCols;
       this.communicator = communicator;
    }

    // this method will "play the game"
    @Override
    public void run() {
        // as long as we have more generation to compute
        while(this.currGen < this.generations){
            // calc the cells we can do independently
            updateCells();

            // check for cells from neighbors
            // todo: for each message we get, update the prev table. after we took all the messages, update the curr table
            updateTableFromNeighbors();

            // update neighbors on cells in edges
            updateNeighbors();

            // switch tables (prev <= current , current should be blank)
            switchTables();
        }
    }

    // this method will update the current board form data from other neighbors
    private void updateTableFromNeighbors() {
        // calc how many neighbors you have
        // todo: can we have it from the communicator?
        int numOfNeighbors=10000;
        // ask for data from communicator about other neighbors
        // keep getting data, until you got all the data needed
        int currStatus = 0;
        while(currStatus < numOfNeighbors){
            Message message = communicator.getMessageFromBank(threadRow,threadCol,currGen-1);
            // update prev table with this data
        }

        // update current board
    }

    private void updateNeighbors() {
        if(startIndex.row != 0){
            //send the first row in prev table
            ArrayList<Boolean> row = new ArrayList<>(Arrays.asList(currTable[0]));
            Message message = new Message(row, Message.Direction.UP, currGen);
            communicator.insertToBank(threadRow,threadCol,message);
        }
        if(startIndex.col != 0){
            //send the first col in prev table
            ArrayList<Boolean> col = new ArrayList<>();
            for(int row = 0; row < originRows; row++)
            {
                col.add(currTable[row][0]);
            }
            Message message = new Message(col, Message.Direction.LEFT, currGen);
            communicator.insertToBank(threadRow,threadCol,message);
        }
        if(endIndex.row != originRows){
            //send the last row in prev table
            ArrayList<Boolean> col = new ArrayList<>();
            for(int row = 0; row < originRows; row++)
            {
                col.add(currTable[row][originCols-1]);
            }
            Message message = new Message(col, Message.Direction.RIGHT, currGen);
            communicator.insertToBank(threadRow,threadCol,message);
        }
        if(endIndex.col != originCols){
            //send the last col in prev table
            ArrayList<Boolean> row = new ArrayList<>(Arrays.asList(currTable[originRows-1]));
            Message message = new Message(row, Message.Direction.DOWN, currGen);
            communicator.insertToBank(threadRow,threadCol,message);
        }
    }

    // update the cells we already know
    private void updateCells(){
        for(int i=this.startIndex.row;i<this.endIndex.col;i++){
            for(int j=this.startIndex.col;j<this.endIndex.col;j++){
                updateCell(i,j);
            }
        }
    }

    // update a single cell
    private void updateCell(int row, int col){
        int liveNeighbors = 0;
        int neiCol, neiRow;

        for(int i=-1; i<2; i++){
            for(int j=-1; j<2; j++){
                if(i==0 && j==0){
                    // we are int the original cell that we're testing
                    continue;
                }
                neiRow = row-i; neiCol = col-j;
                liveNeighbors += calcCell(neiRow,neiCol);

                //check if this cell will 'rebirth' or stay alive
                if(liveNeighbors == 3 || (this.prevTable[row][col] && liveNeighbors==2) ){
                    this.currTable[row][col] = true;
                } else {
                    this.currTable[row][col] = false;
                }
            }
        }
    }

    // this will return 1 if the current neighbor is alive
    private int calcCell(int neiRow, int neiCol){
        // if the current cell is in the first row, it doesn't have a neighbor from above
        if(!inBound(neiRow,neiCol)){
            // do nothing, since all the non-exiting neighbors are considered dead.
            // or - we dont know the status of this cell since it is not in our part of the table
            return 0;
        } else {
            // the neighbor is in our part of the table so we can calc it
            if (prevTable[neiRow][neiCol] == true) {
                return 1;
            }
        }
        return 0;
    }
    // this method will check if we're in the big table and if we are in the current table
    private boolean inBound(int row, int col){
        if(row<0 || row<this.startIndex.row || row>=this.endIndex.row ||
                col<0 || col<this.startIndex.col || col>=this.endIndex.col){
            return false;
        }
        return true;
    }

    // this will return the numbers of neighbors
//    private int neighbors(int row, int col){
//        int num=0;
//        for(int i=-1; i<2; i++) {
//            for (int j = -1; j < 2; j++) {
//                if(i==0 && j==0) continue; // this is the current cell
//                int neiRow = row+i, neiCol = col+j;
//                boolean outsideSelfTable = neiRow<this.startIndex.row || neiRow>=this.endIndex.row ||
//                        neiCol<this.startIndex.col || neiCol>=this.endIndex.col;
//                // the cell is outside the curren table but inside the big board!
//                if(outsideSelfTable && neiRow>=0 && neiRow<originRows
//                        && neiCol>=0 && neiCol<originCols){
//                    num++;
//                }
//            }
//        }
//        return num;
//    }

    // fill the prev table with values
    private Boolean[][] createMiniTable(boolean[][] initialTable, Index startIndex, Index endIndex) {
        Boolean[][] cells = new Boolean[endIndex.row-startIndex.row][endIndex.col-startIndex.col];
        for(int i=this.startIndex.row, i1=0 ;i<this.endIndex.col;i++, i1++){
            for(int j=this.startIndex.col, j1=0 ;j<this.endIndex.col;j++, j1++){
                cells[i1][j1] = initialTable[i][j];
            }
        }
        return cells;
    }

    // empty the current table for a new round
    private Boolean[][] createBlankTable(){
        int rows = endIndex.row-startIndex.row, cols = endIndex.col-startIndex.col;
        Boolean[][] cells = new Boolean[rows][cols];
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                cells[i][j] = null;
            }
        }
        return cells;
    }

    // switch tables
    private void switchTables(){
        for(int i=startIndex.row;i<endIndex.col;i++) {
            for (int j = startIndex.col; j < endIndex.col; j++) {
                this.prevTable[i][j] = this.currTable[i][j];
            }
        }
        this.currTable = createBlankTable();
        this.currGen++;
    }
}
