import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    static int columsNumber=0;
    static int rowNumber=0;
    public static void main(String[] args) {
        File inputfile=new File("src/nonogram.txt");

        if(inputfile.exists()){
            readFromFile(inputfile);
            int finalResult[][]=generateEmptymap();
        }else{
            System.out.println("plik nie istnieje");
        }
    }

    private static int[][] generateEmptymap() {
        for (int i = 0;)
        System.out.println("col:"+columsNumber+" | row"+rowNumber);
        return null;
    }

    private static void readFromFile(File inputfile) {
        boolean column=false;
        try {
            Scanner scanner=new Scanner(inputfile);

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(column){
                    columsNumber++;
                }else{
                    if(line.equals("*")){
                        column=true;
                    }else{
                        rowNumber++;
                        System.out.println(line);
                    }
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}