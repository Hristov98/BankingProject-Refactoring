package cardManipulation.cardTables;

import java.util.Map;

public class TableSortedByEncryptedNumber extends CardTable {
    public TableSortedByEncryptedNumber() {
        super("tableSortedByEncryptedNumber.txt");
    }

    @Override
    public void addCardToTable(String cardNumber, String encryptedNumber) {
        table.put(encryptedNumber, cardNumber);
    }

    @Override
    public String tableToString() {
        StringBuilder cardNumberPairs = new StringBuilder();
        for (Map.Entry<String, String> pair : table.entrySet()) {
            cardNumberPairs.append(String.format("%s %s\n", pair.getValue(), pair.getKey()));
        }

        return cardNumberPairs.toString();
    }
}
