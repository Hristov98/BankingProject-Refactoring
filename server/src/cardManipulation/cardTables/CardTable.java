package cardManipulation.cardTables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.TreeMap;

public abstract class CardTable {
    protected final String fileName;
    protected TreeMap<String, String> table;

    public CardTable(String fileName){
        this.fileName = fileName;
        table = new TreeMap<>();
    }

    public abstract void addCardToTable(String cardNumber, String encryptedNumber);

    public abstract String tableToString();

    public void loadCardTable() {
        File tableFile = new File(fileName);
        try {
            Scanner scanner = openFileForReading(tableFile);
            readCardPairsOntoContainer(scanner);
            scanner.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: No table file found when attempting to read.");
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            System.err.println("Error: IOException encountered while reading from file.");
            ioException.printStackTrace();
        }
    }

    private Scanner openFileForReading(File file) throws IOException {
        createFileIfNotFound(file);
        return new Scanner(file);
    }

    private void createFileIfNotFound(File file) throws IOException {
        if (!file.exists()) {
            System.err.println("Warning: Table for sorting by card number file doesn't exist. "
                    + "Creating new table file.");
            file.createNewFile();
        }
    }

    private void readCardPairsOntoContainer(Scanner scanner) {
        while (scanner.hasNext()) {
            table.put(scanner.next(), scanner.next());
        }
    }

    public void saveTableToFile() {
        File tableFile = new File(fileName);

        try {
            Formatter formatter = openFileForWriting(tableFile);
            formatter.format("%s%n", tableToString());
            formatter.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: No table file found when attempting to write.");
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            System.err.println("Error: IOException encountered while writing to file.");
            ioException.printStackTrace();
        }
    }

    private Formatter openFileForWriting(File file) throws IOException {
        createFileIfNotFound(file);
        return new Formatter(file);
    }
}
