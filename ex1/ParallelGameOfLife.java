package ex1;

import java.util.ArrayList;

public class ParallelGameOfLife implements GameOfLife {

	public boolean[][][] invoke(boolean[][] initalField, int hSplit, int vSplit,
			int generations) {
        // tables to return
        boolean[][] beforeLastTable;
        boolean[][] lastTable;

        // create singleThreaded games
		int numOfRows = initalField.length;
		int numOfCols = initalField[0].length;
		int rowJump = numOfRows/vSplit; int colJump = numOfCols/hSplit;
		int countC = 0; int countR = 0;

        Thread[][] threads = new Thread[vSplit][hSplit];

        for(int i=0; i<numOfRows && countR < vSplit; i+=rowJump){
			for(int j=0; j<numOfCols && countC < hSplit; j+=colJump){
				// [i][j] is the mini section to solve by one thread
				// create for every [i][j] a SingleThreadGoL
                threads[i][j] = new Thread(new SingleThreadGameOfLife(initalField, new Index(i,j),
                        new Index(i+rowJump,j+colJump),generations));

				// give data to ThreadsCommunicator , hSplit + vSplit
                //TODO: create communicator
				countC++;
			}
			countC=0;
			countR++;
		}

		return null;
	}

	//TODO: method that will start all threads, and will join them afterwards
    //TODO: fill out tables to return
}
