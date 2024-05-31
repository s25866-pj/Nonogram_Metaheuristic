import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    //sposób podawania liczb:
    //napierw pion, od góry, do dołu, od lewej do prawej
    //gwaizdka (*)
    // poziom od lewej do prawej, od góry do dołu
    static int columsNumber = 0;
    static int rowNumber = 0;
    static List<List<Integer>> rowHints = new ArrayList<>();
    static List<List<Integer>> colHints = new ArrayList<>();

    public static void main(String[] args) {
        File inputfile = new File("src/nonogram.txt");
        if (inputfile.exists()) {
            readFromFile(inputfile);
            int[][] finalResult = generateEmptyMap();

            if (solveNonogram(finalResult, 0, 0)) {
                showResult(finalResult);
            } else {
                System.out.println("No solution found.");
            }
        } else {
            System.out.println("plik nie istnieje");
        }
    }

    private static void showResult(int[][] finalResult) {
        for (int i = 0; i < finalResult.length; i++) {
            for (int j = 0; j < finalResult[i].length; j++) {
                System.out.print((finalResult[i][j] == 1 ? "X" : ".") + " ");
            }
            System.out.println();
        }
    }

    private static int[][] generateEmptyMap() {
        int[][] emptyArray = new int[rowNumber][columsNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < columsNumber; j++) {
                emptyArray[i][j] = 0;
            }
        }
        return emptyArray;
    }

    private static void readFromFile(File inputfile) {
        boolean column = false;
        try {
            Scanner scanner = new Scanner(inputfile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("*")) {
                    column = true;
                } else {
                    List<Integer> hints = new ArrayList<>();
                    for (String num : line.split(" ")) {
                        hints.add(Integer.parseInt(num));
                    }
                    if (column) {
                        colHints.add(hints);
                        columsNumber++;
                    } else {
                        rowHints.add(hints);
                        rowNumber++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean solveNonogram(int[][] board, int row, int col) {
        if (row == rowNumber) {
            return isValidBoard(board);
        }
        if (col == columsNumber) {
            return solveNonogram(board, row + 1, 0);
        }
        board[row][col] = 1;
        if (solveNonogram(board, row, col + 1)) {
            return true;
        }
        board[row][col] = 0;
        return solveNonogram(board, row, col + 1);
    }

    private static boolean isValidBoard(int[][] board) {
        for (int i = 0; i < rowNumber; i++) {
            if (!isValidLine(board[i], rowHints.get(i))) {
                return false;
            }
        }
        for (int j = 0; j < columsNumber; j++) {
            int[] col = new int[rowNumber];
            for (int i = 0; i < rowNumber; i++) {
                col[i] = board[i][j];
            }
            if (!isValidLine(col, colHints.get(j))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidLine(int[] line, List<Integer> hints) {
        List<Integer> groups = new ArrayList<>();
        int count = 0;
        for (int cell : line) {
            if (cell == 1) {
                count++;
            } else {
                if (count > 0) {
                    groups.add(count);
                    count = 0;
                }
            }
        }
        if (count > 0) {
            groups.add(count);
        }
        return groups.equals(hints);
    }
}
