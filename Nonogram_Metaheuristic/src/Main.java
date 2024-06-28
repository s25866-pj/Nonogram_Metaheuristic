import java.io.File;
import java.io.FileNotFoundException;
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
        File inputfile = new File("src/nonogram.txt");
        if (inputfile.exists()) {
            readFromFile(inputfile);
            int[][] finalResult = generateEmptyMap();

            System.out.println("Using Full Search:");
            if (solveNonogram(finalResult, 0, 0)) {
                showResult(finalResult);
            } else {
                System.out.println("No solution found.");
            }

            finalResult = generateEmptyMap();

            System.out.println("Using Hill Climbing:");
            if (solveNonogramHillClimbing(finalResult)) {
                showResult(finalResult);
            } else {
                System.out.println("No solution found.");
            }

            finalResult = generateEmptyMap();

            System.out.println("Using Tabu Search:");
            if (solveNonogramTabuSearch(finalResult)) {
                showResult(finalResult);
            } else {
                System.out.println("No solution found.");
            }

            finalResult = generateEmptyMap();

            System.out.println("Using Genetic Algorithm:");
            if (geneticAlgorithm(finalResult)) {
                showResult(finalResult);
            } else {
                System.out.println("No solution found.");
            }
        } else {
            System.out.println("File not found.");
        }
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

    private static boolean solveNonogramHillClimbing(int[][] board) {
        initializeRandomBoard(board);
        int currentScore = calculateScore(board);
        int steps = 100000;  // Increased maximum number of steps to perform

        for (int step = 0; step < steps; step++) {
            int[][] neighbor = generateNeighbor(board);
            int neighborScore = calculateScore(neighbor);

            if (neighborScore > currentScore || random.nextDouble() < 0.1) {  // Add randomness
                board = neighbor;
                currentScore = neighborScore;
            }

            if (currentScore == rowNumber + columnsNumber) {  // Found a solution
                return true;
            }
        }
        return false;  // No solution found within the step limit
    }

    private static void initializeRandomBoard(int[][] board) {
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < columnsNumber; j++) {
                board[i][j] = random.nextInt(2);
            }
        }
    }

    private static int[][] generateNeighbor(int[][] board) {
        int[][] neighbor = copyBoard(board);
        int row = random.nextInt(rowNumber);
        int col = random.nextInt(columnsNumber);
        neighbor[row][col] = 1 - neighbor[row][col];  // Flip the cell
        return neighbor;
    }

    private static int[][] copyBoard(int[][] board) {
        int[][] copy = new int[rowNumber][columnsNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < columnsNumber; j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }

    private static int calculateScore(int[][] board) {
        int score = 0;
        for (int i = 0; i < rowNumber; i++) {
            if (isValidLine(board[i], rowHints.get(i))) {
                score++;
            }
        }
        for (int j = 0; j < columnsNumber; j++) {
            int[] col = new int[rowNumber];
            for (int i = 0; i < rowNumber; i++) {
                col[i] = board[i][j];
            }
            if (isValidLine(col, colHints.get(j))) {
                score++;
            }
        }
        return score;
    }

    private static boolean solveNonogramTabuSearch(int[][] board) {
        initializeRandomBoard(board);
        int currentScore = calculateScore(board);
        int bestScore = currentScore;
        int[][] bestSolution = copyBoard(board);

        int tabuListSize = 50;
        List<int[][]> tabuList = new ArrayList<>();
        tabuList.add(copyBoard(board));

        int maxIterations = 100000;

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<int[][]> neighbors = generateNeighbors(board, 10); // Generate multiple neighbors
            int[][] bestNeighbor = null;
            int bestNeighborScore = Integer.MIN_VALUE;

            for (int[][] neighbor : neighbors) {
                int neighborScore = calculateScore(neighbor);

                if (neighborScore > bestNeighborScore && !isInTabuList(tabuList, neighbor)) {
                    bestNeighbor = neighbor;
                    bestNeighborScore = neighborScore;
                }
            }

            if (bestNeighbor == null) {
                continue;
            }

            board = bestNeighbor;
            currentScore = bestNeighborScore;

            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestSolution = copyBoard(board);
            }

            tabuList.add(copyBoard(board));
            if (tabuList.size() > tabuListSize) {
                tabuList.remove(0);
            }

            if (bestScore == rowNumber + columnsNumber) {
                for (int i = 0; i < rowNumber; i++) {
                    System.arraycopy(bestSolution[i], 0, board[i], 0, columnsNumber);
                }
                return true;
            }
        }

        for (int i = 0; i < rowNumber; i++) {
            System.arraycopy(bestSolution[i], 0, board[i], 0, columnsNumber);
        }
        return false;
    }

    private static List<int[][]> generateNeighbors(int[][] board, int numberOfNeighbors) {
        List<int[][]> neighbors = new ArrayList<>();
        for (int i = 0; i < numberOfNeighbors; i++) {
            neighbors.add(generateNeighbor(board));
        }
        return neighbors;
    }

    private static boolean isInTabuList(List<int[][]> tabuList, int[][] board) {
        for (int[][] tabu : tabuList) {
            if (Arrays.deepEquals(tabu, board)) {
                return true;
            }
        }
        return false;
    }

    // Genetic Algorithm components

    private static boolean geneticAlgorithm(int[][] finalResult) {
        List<int[][]> population = initializePopulation();
        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            List<int[][]> newPopulation = new ArrayList<>();

            // Elitism: carry over the best solutions directly to the next generation
            List<int[][]> elites = getElites(population);
            newPopulation.addAll(elites);

            // Generate the rest of the new population
            while (newPopulation.size() < POPULATION_SIZE) {
                int[][] parent1 = selectParent(population);
                int[][] parent2 = selectParent(population);

                int[][] offspring1, offspring2;
                if (random.nextDouble() < CROSSOVER_RATE) {
                    int[][][] offspring = crossover(parent1, parent2);
                    offspring1 = offspring[0];
                    offspring2 = offspring[1];
                } else {
                    offspring1 = copyBoard(parent1);
                    offspring2 = copyBoard(parent2);
                }

                mutate(offspring1);
                mutate(offspring2);

                newPopulation.add(offspring1);
                newPopulation.add(offspring2);
            }

            population = newPopulation;

            int[][] bestSolution = getBestSolution(population);
            if (calculateScore(bestSolution) == rowNumber + columnsNumber) {
                copyBoardToResult(bestSolution, finalResult);
                return true;
            }
        }

        int[][] bestSolution = getBestSolution(population);
        copyBoardToResult(bestSolution, finalResult);
        return calculateScore(bestSolution) == rowNumber + columnsNumber;
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

    private static List<int[][]> getElites(List<int[][]> population) {
        List<int[][]> sortedPopulation = new ArrayList<>(population);
        sortedPopulation.sort((a, b) -> Integer.compare(calculateScore(b), calculateScore(a)));
        return new ArrayList<>(sortedPopulation.subList(0, ELITE_SIZE));
    }

    private static int[][] selectParent(List<int[][]> population) {
        int tournamentSize = 5;
        List<int[][]> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(random.nextInt(POPULATION_SIZE)));
        }
        tournament.sort((a, b) -> Integer.compare(calculateScore(b), calculateScore(a)));
        return tournament.get(0);
    }

    private static int[][][] crossover(int[][] parent1, int[][] parent2) {
        int[][] offspring1 = new int[rowNumber][columnsNumber];
        int[][] offspring2 = new int[rowNumber][columnsNumber];

        int crossoverPoint1 = random.nextInt(rowNumber * columnsNumber);
        int crossoverPoint2 = random.nextInt(rowNumber * columnsNumber);
        if (crossoverPoint1 > crossoverPoint2) {
            int temp = crossoverPoint1;
            crossoverPoint1 = crossoverPoint2;
            crossoverPoint2 = temp;
        }

        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < columnsNumber; j++) {
                int index = i * columnsNumber + j;
                if (index < crossoverPoint1 || index > crossoverPoint2) {
                    offspring1[i][j] = parent1[i][j];
                    offspring2[i][j] = parent2[i][j];
                } else {
                    offspring1[i][j] = parent2[i][j];
                    offspring2[i][j] = parent1[i][j];
                }
            }
        }

        return new int[][][]{offspring1, offspring2};
    }

    private static void mutate(int[][] individual) {
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < columnsNumber; j++) {
                if (random.nextDouble() < MUTATION_RATE) {
                    individual[i][j] = 1 - individual[i][j];
                }
            }
        }

        if (random.nextDouble() < MUTATION_RATE) {
            int row = random.nextInt(rowNumber);
            for (int j = 0; j < columnsNumber; j++) {
                individual[row][j] = 1 - individual[row][j];
            }
        }
    }

    private static int[][] getBestSolution(List<int[][]> population) {
        return population.stream().max(Comparator.comparingInt(Main::calculateScore)).orElse(null);
    }

    private static void copyBoardToResult(int[][] bestSolution, int[][] finalResult) {
        for (int i = 0; i < rowNumber; i++) {
            System.arraycopy(bestSolution[i], 0, finalResult[i], 0, columnsNumber);
        }
    }
}
