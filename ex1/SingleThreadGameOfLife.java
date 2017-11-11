package ex1;

public class SingleThreadGameOfLife implements Runnable{
    Boolean[][] currTable;
    Boolean[][] prevTable;
    int currGen;
    int generations;
    Index startIndex;
    Index endIndex;

    public SingleThreadGameOfLife(boolean[][] initialTable,Index startIndex, Index endIndex, int generations){
       this.prevTable = createMiniTable(initialTable,startIndex,endIndex);
       this.currTable = createBlankTable();
       this.generations = generations;
       this.currGen = 0;
       this.startIndex = startIndex;
       this.endIndex = endIndex;
    }

    // this method will "play the game"
    @Override
    public void run() {
        // as long as we have more generation to compute
        while(this.currGen < this.generations){
            // calc the cells we can do independently
            updateCells();
            // check for cells from neighbors

            // update neighbors on cells

            // switch tables (prev <= current , current should be blank)
            switchTables();
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

    }

    // fill the prev table with values
    private Boolean[][] createMiniTable(boolean[][] initialTable, Index startIndex, Index endIndex) {
        Boolean[][] cells = new Boolean[endIndex.row-startIndex.row][endIndex.col-startIndex.col];
        for(int i=this.startIndex.row;i<this.endIndex.col;i++){
            for(int j=this.startIndex.col;j<this.endIndex.col;j++){
                cells[i][j] = initialTable[i][j];
            }
        }
        return cells;
    }

    // empty the current table for a new round
    private Boolean[][] createBlankTable(){
        Boolean[][] cells = new Boolean[endIndex.row-startIndex.row][endIndex.col-startIndex.col];
        for(int i=startIndex.row;i<endIndex.col;i++){
            for(int j=startIndex.col;j<endIndex.col;j++){
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
    }
}
