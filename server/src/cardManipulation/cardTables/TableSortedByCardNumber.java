package cardManipulation.cardTables;

import java.util.Map;

public class TableSortedByCardNumber extends CardTable {
    public TableSortedByCardNumber() {
        super("tableSortedByCardNumber.txt");
    }

    @Override
    public void addCardToTable(String cardNumber, String encryptedNumber) {
        table.put(cardNumber, encryptedNumber);
    }

    @Override
    public String tableToString() {
        StringBuilder cardNumberPairs = new StringBuilder();
        for (Map.Entry<String, String> pair : table.entrySet()) {
            cardNumberPairs.append(String.format("%s %s\n", pair.getKey(), pair.getValue()));
        }

        return cardNumberPairs.toString();
    }
}
