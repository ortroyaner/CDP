package ex1;

public class Results {
    private static boolean[][] last;
    private static boolean[][] prev;

    public static boolean[][] getLast() {
        return last;
    }

    public static boolean[][] getPrev() {
        return prev;
    }

    public enum TableKind{
        PREV,LAST
    }

    public synchronized static void updateTable(TableKind kind, Boolean[][] mini, Index start, Index end){
        int rows = end.row-start.row, cols=end.col-start.col;
        for(int remoteI=0, localI=start.row; remoteI<rows; remoteI++, localI++){
            for(int remoteJ=0, localJ=start.row; remoteJ<cols; remoteJ++, localJ++){
                if(kind==TableKind.PREV){
                    prev[localI][localJ] = mini[remoteI][remoteJ];
                }else{
                    last[localI][localJ] = mini[remoteI][remoteJ];
                }
            }
        }
    }
}
