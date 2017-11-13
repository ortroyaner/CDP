package ex1;

public class SingleThreadGameOfLife implements Runnable{
    Boolean[][] currTable;
    Boolean[][] prevTable;
    int currGen;
    int generations;
    Index startIndex;
    Index endIndex;
    int threadRow, threadCol;

    public SingleThreadGameOfLife(boolean[][] initialTable,Index startIndex, Index endIndex, int threadRow, int threadCol, int generations){
       this.prevTable = createMiniTable(initialTable,startIndex,endIndex);
       this.currTable = createBlankTable();
       this.generations = generations;
       this.currGen = 0;
       this.startIndex = startIndex;
       this.endIndex = endIndex;
       this.threadRow = threadRow;
       this.threadCol = threadCol;
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

            // update neighbors on cells
            updateNeighbors();

            // switch tables (prev <= current , current should be blank)
            switchTables();
        }
    }

    private void updateNeighbors() {
        //
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
