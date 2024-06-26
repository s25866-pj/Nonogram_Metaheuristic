import java.io.*;
import java.util.*;

public class Main {
    static int columnsNumber = 0;
    static int rowNumber = 0;
    static List<List<Integer>> rowHints = new ArrayList<>();
    static List<List<Integer>> colHints = new ArrayList<>();
    static Random random = new Random();

    static final int POPULATION_SIZE = 100;
    static final int ELITE_SIZE = 2;
    static final int NUM_GENERATIONS = 1000;
    static final double MUTATION_RATE = 0.01;
    static final double CROSSOVER_RATE = 0.7;

    public static void main(String[] args) {
        if(args.length==0){
            System.out.println("nie podano argumentów");
            return;
        }else if(args[0].equals("h")){
            System.out.println("rozwiązanie, plik np <1 nonogram.txt>\n" +
                    "0-fullsearch\n 1-HillClimbing \n 2-TabuSearch\n3-GeneticAlgoritm\n 4-allAlgoroitms \n5-raport <1 nonogram.txt howManyTimeTest> ");
        }
        else{

                File inputfile = new File(args[1]);
                int[][] finalResult;
                if (inputfile.exists()) {
                    readFromFile(inputfile);
//                    finalResult = generateEmptyMap();
                } else {
                    System.out.println("File not found.");
                }
                switch(args[0]){
                    case "0":
                        finalResult = generateEmptyMap();
                        nonogramAlgoritm(inputfile,finalResult);
                        break;
                    case "1":
                        finalResult = generateEmptyMap();
                        HillClimbing(inputfile,finalResult);
                        break;
                    case "2":
                        finalResult = generateEmptyMap();
                        TabuSearch(inputfile,finalResult);
                        break;
                    case "3":
                        finalResult = generateEmptyMap();
                        GeneticAlgorithm(inputfile,finalResult);
                        break;
                    case "4":
                        finalResult = generateEmptyMap();
                        nonogramAlgoritm(inputfile,finalResult);
                        finalResult = generateEmptyMap();
                        HillClimbing(inputfile,finalResult);
                        finalResult = generateEmptyMap();
                        TabuSearch(inputfile,finalResult);
                        finalResult = generateEmptyMap();
                        GeneticAlgorithm(inputfile,finalResult);
                        break;
                    case "5":
                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter("testResult.txt"));
                            int testNumber =Integer.parseInt(args[2]);
                            writer.write("Brutforce: ");
                            for (int i = 0; i < testNumber; i++) {
                                finalResult = generateEmptyMap();
                                long startTime=System.nanoTime();
                                nonogramAlgoritmNoS(inputfile,finalResult);
                                long endTime = System.nanoTime();
                                long duration = endTime-startTime;
                                writer.write(duration/100+" ");
                            }
                            writer.write("\n HillClimbing: ");
                            for (int i = 0; i < testNumber; i++) {
                                finalResult = generateEmptyMap();
                                long startTime=System.nanoTime();
                                HillClimbingNoS(inputfile,finalResult);
                                long endTime = System.nanoTime();
                                long duration = endTime-startTime;
                                writer.write(duration/100+" ");
                            }
                            writer.write("\n TabuSearch: ");
                            for (int i = 0; i < testNumber; i++) {
                                finalResult = generateEmptyMap();
                                long startTime=System.nanoTime();
                                TabuSearchNoS(inputfile,finalResult);
                                long endTime = System.nanoTime();
                                long duration = endTime-startTime;
                                writer.write(duration/100+" ");
                            }
                            writer.write("\n GeneticAlgoritm: ");
                            for (int i = 0; i < testNumber; i++) {
                                finalResult = generateEmptyMap();
                                long startTime=System.nanoTime();
                                GeneticAlgorithmNoS(inputfile,finalResult);
                                long endTime = System.nanoTime();
                                long duration = endTime-startTime;
                                writer.write(duration/100+" ");
                            }
                            writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
//                        generateRaport(args[2]);
                        break;

                }

        }
    }

    private static void generateRaport(String arg) {
        ArrayList<String> algorithms = new ArrayList<>();
        ArrayList<Integer> times = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("testResult.txt"));
            String line;
            while((line=br.readLine())!=null){
                String[] patrs = line.split(" ");
                algorithms.add(patrs[0]);
                for (int i = 1; i <Integer.parseInt(arg) ; i++) {
                    times.add(Integer.parseInt(patrs[i]));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void GeneticAlgorithm(File inputfile, int[][] finalResult) {
        System.out.println("Using Genetic Algorithm:");
        if (geneticAlgorithm(finalResult)) {
            showResult(finalResult);
        } else {
            System.out.println("No solution found.");
        }
    }
    private static void TabuSearch(File inputfile, int[][] finalResult) {
        System.out.println("Using Tabu Search:");
        if (solveNonogramTabuSearch(finalResult)) {
            showResult(finalResult);
        } else {
            System.out.println("No solution found.");
        }
    }
    private static void HillClimbing(File inputfile, int[][] finalResult) {
        System.out.println("Using Hill Climbing:");
        if (rowNumber == 0 || columnsNumber == 0) {
            System.out.println("Board dimensions not initialized properly.");
            return;
        }
        if (solveNonogramHillClimbing(finalResult)) {
            showResult(finalResult);
        } else {
            System.out.println("No solution found.");
        }
    }
    private static void nonogramAlgoritm(File inputfile, int[][] finalResult) {
        System.out.println("Using Full Search:");
        if (solveNonogram(finalResult, 0, 0)) {
            showResult(finalResult);
        } else {
            System.out.println("No solution found.");
        }
    }

    private static void GeneticAlgorithmNoS(File inputfile, int[][] finalResult) {
        geneticAlgorithm(finalResult);
    }
    private static void TabuSearchNoS(File inputfile, int[][] finalResult) {
        solveNonogramTabuSearch(finalResult);
    }
    private static void HillClimbingNoS(File inputfile, int[][] finalResult) {
        if (rowNumber == 0 || columnsNumber == 0) {
            return;
        }
       solveNonogramHillClimbing(finalResult);
    }
    private static void nonogramAlgoritmNoS(File inputfile, int[][] finalResult) {
        solveNonogram(finalResult, 0, 0);
    }

    private static void showResult(int[][] finalResult) {
        for (int[] row : finalResult) {
            for (int cell : row) {
                System.out.print((cell == 1 ? "■" : "□") + " ");
            }
            System.out.println();
        }
    }

    private static int[][] generateEmptyMap() {
        int[][] emptyArray = new int[rowNumber][columnsNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < columnsNumber; j++) {
                emptyArray[i][j] = 0;
            }
        }
        return emptyArray;
    }

    private static void readFromFile(File inputfile) {
        rowHints.clear();
        colHints.clear();
        columnsNumber = 0;
        rowNumber = 0;
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
                        columnsNumber++;
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
        if (col == columnsNumber) {
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
        for (int j = 0; j < columnsNumber; j++) {
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
///////////////////////////////////////////////////////////////////////////////////////
    private static boolean solveNonogramHillClimbing(int[][] board) {
        initializeRandomBoard(board);
        int currentScore = calculateScore(board);
        int steps = 100000;

        for (int step = 0; step < steps; step++) {
            int[][] neighbor = generateNeighbor(board);
            int neighborScore = calculateScore(neighbor);

            if (neighborScore > currentScore) {
                for (int i = 0; i < rowNumber; i++) {
                    System.arraycopy(neighbor[i], 0, board[i], 0, columnsNumber);
                }
                currentScore = neighborScore;

                if (currentScore == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void initializeRandomBoard(int[][] board) {
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < columnsNumber; j++) {
                board[i][j] = random.nextInt(2);
            }
        }
    }

    private static int calculateScore(int[][] board) {
        int score = 0;
        for (int i = 0; i < rowNumber; i++) {
            score += calculateLineScore(board[i], rowHints.get(i));
        }
        for (int j = 0; j < columnsNumber; j++) {
            int[] col = new int[rowNumber];
            for (int i = 0; i < rowNumber; i++) {
                col[i] = board[i][j];
            }
            score += calculateLineScore(col, colHints.get(j));
        }
        return score;
    }

    private static int calculateLineScore(int[] line, List<Integer> hints) {
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

        if (groups.equals(hints)) {
            return 0;
        } else {
            return Math.abs(groups.size() - hints.size());
        }
    }

    private static int[][] generateNeighbor(int[][] board) {
        int[][] neighbor = new int[rowNumber][columnsNumber];
        for (int i = 0; i < rowNumber; i++) {
            System.arraycopy(board[i], 0, neighbor[i], 0, columnsNumber);
        }

        int row = random.nextInt(rowNumber);
        int col = random.nextInt(columnsNumber);
        neighbor[row][col] = 1 - neighbor[row][col];

        return neighbor;
    }
//////////////////////////////////////////////////////////////////////
    private static boolean solveNonogramTabuSearch(int[][] board) {
        initializeRandomBoard(board);
        int currentScore = calculateScore(board);
        List<int[][]> tabuList = new ArrayList<>();
        int tabuTenure = 100;

        int steps = 100000;
        for (int step = 0; step < steps; step++) {
            int[][] bestNeighbor = null;
            int bestNeighborScore = Integer.MAX_VALUE;

            for (int i = 0; i < rowNumber; i++) {
                for (int j = 0; j < columnsNumber; j++) {
                    int[][] neighbor = generateNeighbor(board, i, j);
                    if (tabuList.contains(neighbor)) {
                        continue;
                    }

                    int neighborScore = calculateScore(neighbor);
                    if (neighborScore < bestNeighborScore) {
                        bestNeighbor = neighbor;
                        bestNeighborScore = neighborScore;
                    }
                }
            }

            if (bestNeighbor == null) {
                break;
            }

            board = bestNeighbor;
            currentScore = bestNeighborScore;

            tabuList.add(board);
            if (tabuList.size() > tabuTenure) {
                tabuList.remove(0);
            }

            if (currentScore == 0) {
                return true;
            }
        }
        return false;
    }

    private static int[][] generateNeighbor(int[][] board, int row, int col) {
        int[][] neighbor = new int[rowNumber][columnsNumber];
        for (int i = 0; i < rowNumber; i++) {
            System.arraycopy(board[i], 0, neighbor[i], 0, columnsNumber);
        }

        neighbor[row][col] = 1 - neighbor[row][col];
        return neighbor;
    }
///////////////////////////////////////////////////////////////////////////////////
    private static boolean geneticAlgorithm(int[][] board) {
        List<int[][]> population = initializePopulation();
        int[] fitnessScores = new int[POPULATION_SIZE];

        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            for (int i = 0; i < POPULATION_SIZE; i++) {
                fitnessScores[i] = calculateScore(population.get(i));
                if (fitnessScores[i] == 0) {
                    System.arraycopy(population.get(i), 0, board, 0, rowNumber);
                    return true;
                }
            }

            List<int[][]> newPopulation = new ArrayList<>();
            for (int i = 0; i < ELITE_SIZE; i++) {
                newPopulation.add(population.get(selectBestIndividual(fitnessScores)));
            }

            while (newPopulation.size() < POPULATION_SIZE) {
                int[][] parent1 = population.get(tournamentSelection(fitnessScores));
                int[][] parent2 = population.get(tournamentSelection(fitnessScores));
                int[][] child;

                if (random.nextDouble() < CROSSOVER_RATE) {
                    child = crossover(parent1, parent2);
                } else {
                    child = parent1;
                }

                if (random.nextDouble() < MUTATION_RATE) {
                    mutate(child);
                }

                newPopulation.add(child);
            }

            population = newPopulation;
        }

        return false;
    }

    private static List<int[][]> initializePopulation() {
        List<int[][]> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            int[][] individual = new int[rowNumber][columnsNumber];
            initializeRandomBoard(individual);
            population.add(individual);
        }
        return population;
    }

    private static int selectBestIndividual(int[] fitnessScores) {
        int bestIndex = 0;
        for (int i = 1; i < fitnessScores.length; i++) {
            if (fitnessScores[i] < fitnessScores[bestIndex]) {
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private static int tournamentSelection(int[] fitnessScores) {
        int bestIndex = random.nextInt(POPULATION_SIZE);
        for (int i = 1; i < 3; i++) {
            int index = random.nextInt(POPULATION_SIZE);
            if (fitnessScores[index] < fitnessScores[bestIndex]) {
                bestIndex = index;
            }
        }
        return bestIndex;
    }

    private static int[][] crossover(int[][] parent1, int[][] parent2) {
        int[][] child = new int[rowNumber][columnsNumber];
        int crossoverPoint = random.nextInt(rowNumber * columnsNumber);

        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < columnsNumber; j++) {
                int index = i * columnsNumber + j;
                if (index < crossoverPoint) {
                    child[i][j] = parent1[i][j];
                } else {
                    child[i][j] = parent2[i][j];
                }
            }
        }

        return child;
    }

    private static void mutate(int[][] individual) {
        int row = random.nextInt(rowNumber);
        int col = random.nextInt(columnsNumber);
        individual[row][col] = 1 - individual[row][col];
    }
}
