import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {


        BufferedReader br = new BufferedReader(new FileReader("in.txt"));
        ArrayList<int[][]> arrays = new ArrayList<>();
        int i = Integer.parseInt(br.readLine());
        while(i != 0) {
            arrays.add(new int[i][i]);
            int indx = arrays.size()-1;
            for(int j = 0; j < i; j++) {
                int[] nums = new int[i];
                String[] numStr = br.readLine().split(", ");
                for(int k = 0; k < i; k++) {
                    nums[k] = Integer.parseInt(numStr[k]);
                }
                arrays.get(indx)[j] = nums;
            }
            i = Integer.parseInt(br.readLine());
        }
        br.close();


        BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));

        System.out.println(Arrays.toString(findCoeffs(new int[]{5, 7, 12})));

        /*
        for(int[][] m : arrays) {
            if(m.length<8) {
                rref(m);
                printMatrix(m, bw);
                System.out.println("NEXT\n\n\n");
            }
        }
        */

        bw.flush();
        bw.close();

        /*
        int[][] fourTest = {{-1, -1, 2, 0}, {-1, 2, 0, -1}, {0, -1, -1, 2}, {2, 0, -1, -1}};
        rref(fourTest);
        */
    }

    public static void rref(int[][] matrix) {
        //current column being worked on
        int lead = 0;

        //num of rows and cols in matrix
        int rowCount = matrix.length;
        int colCount = matrix[0].length;

        //goes through each row
        for (int r = 0; r < rowCount; r++) {
            //if the lead is at the end of the row then the matrix is done
            if (lead >= colCount)
                return;

            //**********

            //sets increment value to start at the current row
            int i = r;


            int[] leadVals = new int[rowCount - r];
            for(i = r; i < rowCount; i++) {
                leadVals[i - rowCount] = matrix[i][lead];
            }
            int[] coeffs = findCoeffs(leadVals);

            for()


            //**********
            //printMatrix(matrix);
            //**********

            for (i = r + 1; i < rowCount; i++) {
                if (matrix[i][lead] != 0) {
                    int factor = matrix[i][lead];
                    for (int j = 0; j < matrix[0].length; j++) {
                        matrix[i][j] -= matrix[r][j] * factor;
                    }
                }
            }

            printMatrix(matrix);

            lead++;

        }
    }

    public static int[] findCoeffs(int[] vals) {
        boolean coeffsFound = false;

        //sign stored in a binary number - makes them easier to increment
        int signCounter = 0;

        //
        int coeffCounter = 0;
        int base = 2;
        int[] testCoeffs = new int[vals.length];


        for(;;) {
            String coeffStr = Integer.toString(coeffCounter, base);
            while(coeffStr.length() < vals.length) coeffStr = "0" + coeffStr;
            for(int i = 0; i < vals.length; i++) testCoeffs[i] = Integer.parseInt(String.valueOf(coeffStr.toCharArray()[i]));
            do {
                String binarySign = Integer.toBinaryString(signCounter);
                while(binarySign.length() < vals.length) binarySign = "0" + binarySign;
                String[] signStrArr = new String[vals.length];
                for(int i = 0; i < vals.length; i++) signStrArr[i] = String.valueOf(binarySign.toCharArray()[i]);
                int[] signs = new int[vals.length];
                for(int i = 0; i < vals.length; i++) signs[i] = signStrArr[i].equals("0") ? 1 : -1;
                if(testCoefficients(vals, testCoeffs, signs)) {
                    for(int i = 0; i < vals.length; i++) testCoeffs[i] *= signs[i];
                    coeffsFound = true;
                    return testCoeffs;
                }
                signCounter++;
            } while(signCounter < vals.length * 2);
            coeffCounter = (Math.pow(base, vals.length) - 1 == coeffCounter) ? 0 : coeffCounter + 1;
            if(coeffCounter == 0) base++;
        }
    }

    public static boolean testCoefficients(int[] vals, int[] coeffs, int[] signs) {
        int sum = 0;
        for(int i = 0; i < vals.length; i++) sum += vals[i] * coeffs[i] * signs[i];
        return sum == 1;
    }


    public static void printMatrix(int[][] matrix) {
        for (int[] i : matrix) {
            System.out.println(Arrays.toString(i));
        }
        System.out.println();
    }

    public static void printMatrix(int[][] matrix, BufferedWriter bw) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int[] i : matrix) {
            System.out.println(Arrays.toString(i));
            sb.append(Arrays.toString(i)).append("\n");
        }
        System.out.println();
        bw.write(sb + "\n");
    }
}