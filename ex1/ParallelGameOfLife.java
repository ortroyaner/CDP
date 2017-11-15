package ex1;

import java.util.ArrayList;

public class ParallelGameOfLife implements GameOfLife {

    public boolean[][][] invoke(boolean[][] initialField, int hSplit, int vSplit,
                                int generations) {
        // tables to return
        boolean[][] beforeLastTable;
        boolean[][] lastTable;

        // create singleThreaded games
        int numOfRows = initialField.length, numOfCols = initialField[0].length;
        int rowJump = numOfRows / vSplit, colJump = numOfCols / hSplit;

        Thread[][] threads = new Thread[vSplit][hSplit];

        // give data to ThreadsCommunicator , hSplit + vSplit
        ThreadsCommunicator communicator = new ThreadsCommunicator(vSplit, hSplit);

        for (int i = 0, countR=0; i < numOfRows && countR < vSplit; i += rowJump, countR++) {
            for (int j = 0, countC=0; j < numOfCols && countC < hSplit; j += colJump, countC++) {
                // [i][j] is the mini section to solve by one thread
                // create for every [i][j] a SingleThreadGoL
                Index start = new Index(i, j);
                int tmpi, tmpj;
                if(countR==vSplit-1){
                    tmpi = numOfRows;
                } else{
                    tmpi = i+rowJump;
                }
                if(countC==hSplit-1){
                    tmpj=numOfCols;
                } else{
                    tmpj = j+colJump;
                }
                Index end = new Index(tmpi, tmpj);
                threads[countR][countC] = new Thread(new SingleThreadGameOfLife(initialField, start,
                        end, countR, countC, numOfRows, numOfCols, generations, communicator));
            }
        }

        startThreadsMatrixAndThenJoin(vSplit, hSplit, threads);

		//return tables
		boolean[][][] res = new boolean[2][][];
		res[0] = Results.getPrev();
		res[1] = Results.getLast();

        return res;
    }

    private void startThreadsMatrixAndThenJoin(int numOfRows, int numOfCols, Thread[][] threads) {
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                threads[i][j].start();
            }
        }
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                try {
                    threads[i][j].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
