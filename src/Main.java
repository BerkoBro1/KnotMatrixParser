import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {


        BufferedReader br = new BufferedReader(new FileReader("in.txt"));
        ArrayList<int[][]> arrays = new ArrayList<>();
        ArrayList<int[]> arrayNum = new ArrayList<>();
        int i = Integer.parseInt(br.readLine());
        int counter = 0;
        while(i != 0) {
            arrays.add(new int[i][i]);
            arrayNum.add(new int[]{i, ++counter});
            int indx = arrays.size()-1;
            for(int j = 0; j < i; j++) {
                int[] nums = new int[i];
                String[] numStr = br.readLine().split(", ");
                for(int k = 0; k < i; k++) {
                    nums[k] = Integer.parseInt(numStr[k]);
                }
                arrays.get(indx)[j] = nums;
            }
            int temp = i;
            i = Integer.parseInt(br.readLine());
            if(temp!=i) counter = 0;
        }
        br.close();


        BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));


        for(int[][] m : arrays) {
            if(m.length<9) {
                rref(m);
                printMatrix(m, arrayNum.get(i), bw);
                System.out.println("Knot: " + Arrays.toString(arrayNum.get(i)));
                System.out.println("NEXT\n\n\n");
            }
            i++;
        }


        bw.flush();
        bw.close();
    }

    public static void rref(int[][] matrix) {

        //*************
        System.out.println("Original matrix");
        printMatrix(matrix);
        //*************

        int[][] originalMatrix = new int[matrix.length][];
        for(int i = 0; i < matrix.length; i++)
            originalMatrix[i] = matrix[i].clone();

        ArrayList<Integer[]> chains = new ArrayList<>();

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


            int[] leadVals = new int[rowCount - r];
            for(i = r; i < rowCount; i++) {
                leadVals[i - r] = matrix[i][lead];
            }
            if(leadVals.length == 2) {
                return;
            }

            for(i = 1; i < leadVals.length; i++) {
                if(leadVals[i]!=0) break;
                if(i == leadVals.length - 1) return;
            }
            int[] coeffs = findCoeffs(leadVals, matrix, r);

            if(Arrays.equals(coeffs, new int[]{0})) return;

            for(i = r; i < rowCount; i++) matrix[r][i] *= coeffs[0];
            for(i = r + 1; i < rowCount; i++) {
                if(matrix[i][r] == 0) continue;
                for(int j = 0; j < rowCount; j++) {
                    matrix[r][j] += matrix[i][j] * coeffs[i-r];
                }
            }



            //**********
            System.out.println("Top row now set to 1");
            printMatrix(matrix);
            //**********

            for (i = r + 1; i < rowCount; i++) {
                if (matrix[i][lead] != 0) {
                    int factor = matrix[i][lead];
                    for (int j = 0; j < matrix[0].length; j++) {
                        matrix[i][j] -= matrix[r][j] * factor;
                    }
                }
            }

            System.out.println("Row now added down");
            printMatrix(matrix);

            lead++;

        }
    }

    public static int[] findCoeffs(int[] vals, int[][] matrix, int r) {

        //sign stored in a binary number - makes them easier to increment
        int signCounter = 0;

        //
        int base = 2;
        int coeffCounter = (int)Math.pow(base, vals.length);
        int[] testCoeffs = new int[vals.length];


        for(;;) {
            String coeffStr = Integer.toString(coeffCounter, base);
            while(coeffStr.length() < vals.length) coeffStr = "0" + coeffStr;
            for(int i = 0; i < vals.length; i++) {
                try {
                    testCoeffs[i] = Integer.parseInt(String.valueOf(coeffStr.toCharArray()[i]));
                } catch(Exception e) {
                    testCoeffs[i] = 10 + (int)coeffStr.toCharArray()[i] - 96;
                }
            }
            signCounter = 0;
            do {
                String binarySign = Integer.toBinaryString(signCounter);
                while(binarySign.length() < vals.length) binarySign = "0" + binarySign;
                String[] signStrArr = new String[vals.length];
                for(int i = 0; i < vals.length; i++) signStrArr[i] = String.valueOf(binarySign.toCharArray()[i]);
                int[] signs = new int[vals.length];
                for(int i = 0; i < vals.length; i++) signs[i] = signStrArr[i].equals("0") ? 1 : -1;
                if(testCoefficients(vals, testCoeffs, signs) && testNextCol(testCoeffs, signs, matrix, r)) {
                    for(int i = 0; i < vals.length; i++) testCoeffs[i] *= signs[i];
                    return testCoeffs;
                }
                signCounter++;
            } while(signCounter < vals.length * 2);
            coeffCounter = (Math.pow(base+1, vals.length) - 1 == coeffCounter) ? (int)Math.pow(++base, vals.length) : coeffCounter + 1;
            //System.out.println("Coeff counter: " + coeffCounter);
            if(coeffCounter>10000) {
                System.out.println("no working found");
                return new int[]{0};
            }
        }
    }

    public static boolean testCoefficients(int[] vals, int[] coeffs, int[] signs) {
        int sum = 0;
        for(int i = 0; i < vals.length; i++) sum += vals[i] * coeffs[i] * signs[i];
        return sum == 1;
    }

    public static boolean testNextCol(int[] testCoeffs, int[] signs, int[][] matrix, int r) {
        int[] nextCol = new int[matrix.length-r-1];
        ArrayList<Integer[]> factors = new ArrayList<>();
        int topNext = matrix[r][r+1];
        int scaledTopNext = topNext * testCoeffs[0] * signs[0];
        for(int i = r + 1; i < matrix.length; i++) {
            scaledTopNext += matrix[i][r+1] * testCoeffs[i-r] * signs[i-r];
        }
        for(int i = 0; i < matrix.length-r-1; i++) {
            nextCol[i] = matrix[i + r + 1][r+1] - scaledTopNext * matrix[i+r+1][r];
            if(nextCol[i] == 1 || nextCol[i] == -1) return true;
            if(nextCol[i]!=0) factors.add(Arrays.stream( primeFactors(nextCol[i]) ).boxed().toArray( Integer[]::new ));
        }

        if(factors.size()<=2) return true;

        Integer[] f0 = factors.get(0);
        boolean notFound = true;
        for(int f : f0) {
            for(int i = 1; i < factors.size(); i++) {
                if(!contains(factors.get(i), f)) {
                    break;
                }
                if(i+1 == factors.size()) return false;
            }
        }
        return true;
    }

    public static boolean contains(final Integer[] array, final int key) {
        for (final int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }


    public static void printMatrix(int[][] matrix) {
        for (int[] i : matrix) {
            System.out.println(Arrays.toString(i));
        }
        System.out.println();
    }

    public static void printMatrix(int[][] matrix, int[] knot, BufferedWriter bw) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int[] i : matrix) {
            System.out.println(Arrays.toString(i));
            sb.append(Arrays.toString(i)).append("\n");
        }
        System.out.println();
        bw.write(knot[0] + ", " + knot[1] + ": ");
        bw.write(Arrays.toString(primeFactors(matrix[matrix.length - 2][matrix.length - 2])) + "\n");
        //bw.write(sb + "\n");
    }

    public static int[] primeFactors(int n)
    {
        n = Math.abs(n);
        ArrayList<Integer> factors = new ArrayList<>();
        // Print the number of 2s that divide n
        if(n==0) return new int[]{0};
        while (n % 2 == 0) {
            factors.add(2);
            n /= 2;
        }

        // n must be odd at this point.  So we can
        // skip one element (Note i = i +2)
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            // While i divides n, print i and divide n
            while (n % i == 0) {
                factors.add(i);
                n /= i;
            }
        }

        // This condition is to handle the case when
        // n is a prime number greater than 2
        if (n > 2)
            factors.add(n);
        return Arrays.stream(factors.toArray()).mapToInt(o -> (int)o).toArray();
    }

}