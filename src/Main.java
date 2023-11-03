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

        for(int[][] m : arrays) {
            if(m.length<8) {
                rref(m);
                printMatrix(m, bw);
                System.out.println("NEXT\n\n\n");
            }
        }


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
            //looks for the first row with a value in the lead column that isn't 0
            while (matrix[i][lead] == 0) {
                i++;
                //if no non-zero value is found, the program moves on to the next column
                if (i == rowCount) {
                    i = r;
                    lead++;
                    //will return if the last column is reached
                    if (colCount == lead)
                        return;
                }
            }
            //swaps the row that was found with the current row
            int[] temp = matrix[i];
            matrix[i] = matrix[r];
            matrix[r] = temp;

            //**********
            //sets lead value to the top row's lead value
            int lv = matrix[r][lead];

            //any number that is a factor of the numbers 1 above or below the lead value can be used to change it to 1
            int highDiff = lv + 1;
            int lowDiff = lv - 1;

            //pretest

            //looks through each row for a value that matches that
            for (i = 0; i < rowCount; i++) {
                //if the leading value is a factor that can get the original lead value to 1, then it will go into one of the if statements
                if (i > r && matrix[i][lead] != 0 && lowDiff % matrix[i][lead] == 0) {
                    int factor = lowDiff / matrix[i][lead];
                    System.out.println("Factor " + factor);
                    for (int j = 0; j < matrix[0].length; j++) {
                        matrix[r][j] -= matrix[i][j] * factor;
                    }
                    break;
                } else if (i > r && matrix[i][lead] != 0 && highDiff % matrix[i][lead] == 0) {
                    //the factor is how much each value needs to be multiplied by to get the lead value to 1
                    int factor = highDiff / matrix[i][lead];
                    for (int j = 0; j < matrix[0].length; j++) {
                        matrix[r][j] -= matrix[i][j] * factor;
                        //this is because if the number subtracted is the one above the original lead value, then it needs to be multiplied by -1, which is a
                        matrix[r][j] *= -1;
                    }
                    break;
                }
            }

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