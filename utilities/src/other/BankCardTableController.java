package other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Formatter;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class BankCardTableController {
    private TreeMap<String, String> cardTableSortedByCardNumber;
    private TreeMap<String, String> cardTableSortedByEncryptedNumber;

    public BankCardTableController() {
        cardTableSortedByCardNumber = new TreeMap<>();
        cardTableSortedByEncryptedNumber = new TreeMap<>();
    }

    public BankCardTableController(TreeMap<String, String> cards) {
        setCardTableSortedByCardNumber(cards);
        setCardTableSortedByEncryptedNumber(cards);
    }

    public void setCardTableSortedByCardNumber(TreeMap<String, String> cards) {
        cardTableSortedByCardNumber = new TreeMap<>(cards);
    }

    public void setCardTableSortedByEncryptedNumber(TreeMap<String, String> cards) {
        cardTableSortedByEncryptedNumber = new TreeMap<>(cards);
    }

    public TreeMap<String, String> getCardTableSortedByCardNumber() {
        return new TreeMap<>(cardTableSortedByCardNumber);
    }

    public TreeMap<String, String> getCardTableSortedByEncryptedNumber() {
        return new TreeMap<>(cardTableSortedByEncryptedNumber);
    }

    public void addCard(String cardNumber, String encryptedNumber) {
        cardTableSortedByCardNumber.put(cardNumber, encryptedNumber);
        cardTableSortedByEncryptedNumber.put(encryptedNumber, cardNumber);
    }

    public String toStringSortedByCard() {
        StringBuilder cardNumbersAsText = new StringBuilder();
        for (Map.Entry<String, String> pair : cardTableSortedByCardNumber.entrySet()) {
            cardNumbersAsText.append(String.format("%s %s\n", pair.getKey(), pair.getValue()));
        }

        return cardNumbersAsText.toString();
    }

    public String toStringSortedByEncryption() {
        StringBuilder cardNumbersAsText = new StringBuilder();

        for (Map.Entry<String, String> pair : cardTableSortedByEncryptedNumber.entrySet()) {
            cardNumbersAsText.append(String.format("%s %s\n", pair.getValue(), pair.getKey()));
        }

        return cardNumbersAsText.toString();
    }

    public void saveSortByCardToFile() {
        File tableFile = new File("tableSortedByCard.txt");
        Formatter formatter = null;

        if (!tableFile.exists()) {
            try {
                tableFile.createNewFile();
            } catch (IOException ioException) {
                System.err.println("Error: Could not create new file tableSortedByCard.txt");
                ioException.printStackTrace();
            }
        }

        try {
            formatter = new Formatter("tableSortedByCard.txt");
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: File tableSortedByCard.txt could not be found.");
        }

        formatter.format("%s%n", toStringSortedByCard());

        if (formatter != null) {
            formatter.close();
        }
    }

    public TreeMap<String, String> readSortByCardFromFile() {
        File tableFile = new File("tableSortedByCard.txt");
        Scanner scanner = null;
        TreeMap<String, String> cardsFromFile = new TreeMap<>();

        try {
            scanner = new Scanner(tableFile);

            while (scanner.hasNext()) {
                cardsFromFile.put(scanner.next(), scanner.next());
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: Could not find file tableSortedByCard.txt");
        }

        if (scanner != null) {
            scanner.close();
        }

        return cardsFromFile;
    }

    public void saveSortByEncryptionToFile() {
        File tableFile = new File("tableSortedByEncryption.txt");
        Formatter formatter = null;

        if (!tableFile.exists()) {
            try {
                tableFile.createNewFile();
            } catch (IOException ioException) {
                System.err.println("Error: Could not create new file tableSortedByEncryption.txt");
                ioException.printStackTrace();
            }
        }

        try {
            formatter = new Formatter("tableSortedByEncryption.txt");
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: File tableSortedByEncryption.txt could not be found.");
        }

        formatter.format("%s%n", toStringSortedByCard());

        if (formatter != null) {
            formatter.close();
        }
    }

    public TreeMap<String, String> readSortByEncryptionFromFile() {
        File tableFile = new File("tableSortedByEncryption.txt");
        Scanner scanner = null;
        TreeMap<String, String> cardsFromFile = new TreeMap<>();

        try {
            scanner = new Scanner(tableFile);

            while (scanner.hasNext()) {
                String cardNumber = scanner.next();
                String encryption = scanner.next();
                cardsFromFile.put(encryption, cardNumber);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: Could not find file tableSortedByEncryption.txt");
        }

        if (scanner != null) {
            scanner.close();
        }

        return cardsFromFile;
    }
}
