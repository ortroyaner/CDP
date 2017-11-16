package ex1;

class Results {
    private static boolean[][] last;
    private static boolean[][] prev;

    static boolean[][] getLast() {
        return last;
    }

    static boolean[][] getPrev() {
        return prev;
    }

    enum TableKind{
        PREV,LAST
    }

    static void initTables(int numOfRows, int numOfCols) {
        last = new boolean[numOfRows][numOfCols];
        prev = new boolean[numOfRows][numOfCols];
    }

    synchronized static void updateTable(TableKind kind, Boolean[][] mini, Index start, Index end){
        int rows = end.row-start.row, cols=end.col-start.col;
        for(int remoteI=0, localI=start.row; localI<start.row+rows; remoteI++, localI++){
            for(int remoteJ=0, localJ=start.col; localJ<start.col+cols; remoteJ++, localJ++){
                if(kind==TableKind.PREV){
                    prev[localI][localJ] = mini[remoteI][remoteJ];
                }else{
                    last[localI][localJ] = mini[remoteI][remoteJ];
                }
            }
        }
    }
}
