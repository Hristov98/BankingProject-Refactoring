package other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Formatter;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

//a class that handles saving the card information to a file as a table and reading from the file
public class BankCardFileControl
{
    private TreeMap<String,String> cardTableSortedByCardNumber;
    private TreeMap<String,String> cardTableSortedByEncryptedNumber;

    public BankCardFileControl()
    {
        cardTableSortedByCardNumber=new TreeMap<>();
        cardTableSortedByEncryptedNumber=new TreeMap<>();
    }

    public BankCardFileControl(TreeMap<String,String> cards)
    {
        setCardTableSortedByCardNumber(cards);
        setCardTableSortedByEncryptedNumber(cards);
    }

    public void setCardTableSortedByCardNumber(TreeMap<String, String> cards) {
        cardTableSortedByCardNumber=new TreeMap<>(cards);
    }

    public void setCardTableSortedByEncryptedNumber(TreeMap<String, String> cards) {
        cardTableSortedByEncryptedNumber=new TreeMap<>(cards);
    }

    public TreeMap<String, String> getCardTableSortedByCardNumber() {
        return new TreeMap<>(cardTableSortedByCardNumber);
    }

    public TreeMap<String, String> getCardTableSortedByEncryptedNumber() {
        return new TreeMap<>(cardTableSortedByEncryptedNumber);
    }

    public void addCard(String cardNumber,String encryptedNumber)
    {
        cardTableSortedByCardNumber.put(cardNumber,encryptedNumber);
        cardTableSortedByEncryptedNumber.put(encryptedNumber,cardNumber);
    }

    public String toStringSortedByCard() {
        StringBuilder table= new StringBuilder();
        for (Map.Entry<String, String> pair : cardTableSortedByCardNumber.entrySet())
        {
            table.append(String.format("%s %s\n", pair.getKey(), pair.getValue()));
        }

        return table.toString();
    }

    public String toStringSortedByEncryption() {
        StringBuilder table= new StringBuilder();

        for (Map.Entry<String, String> pair : cardTableSortedByEncryptedNumber.entrySet())
        {
            table.append(String.format("%s %s\n", pair.getValue(),pair.getKey()));
        }

        return table.toString();
    }

    public void saveSortByCardToFile()
    {
        File table=new File("tableSortedByCard.txt");
        Formatter formatter = null;

        //creating a new file if it doesn't exist
        if (!table.exists())
        {
            try
            {
                table.createNewFile();
            }
            catch (IOException e) {
                System.err.println("Error: Could not create new file tableSortedByCard.txt");
                e.printStackTrace();
            }
        }

        //opening formatter
        try
        {
            formatter = new Formatter("tableSortedByCard.txt");
        }
        catch(FileNotFoundException foundNotFound)
        {
            System.err.println("Error: File tableSortedByCard.txt could not be found.");
        }

        //saving table to file
        formatter.format("%s%n",toStringSortedByCard());

        //closing formatter
        if(formatter!=null){
            formatter.close();
        }
    }

    public TreeMap<String, String> readSortByCardFromFile()
    {
        File table = new File("tableSortedByCard.txt");
        Scanner scanner=null;
        TreeMap<String,String> cardsFromFile=new TreeMap<>();

        try{
            //opening scanner
            scanner = new Scanner(table);

            //adding card information to container
            while(scanner.hasNext()){
                cardsFromFile.put(scanner.next(),scanner.next());
            }
        }
        catch (FileNotFoundException fileNotFoundException){
            System.err.println("Error: Could not find file tableSortedByCard.txt");
        }

        //closing scanner
        if ( scanner != null )
        {
            scanner.close();
        }

       return cardsFromFile;
    }

    public void saveSortByEncryptionToFile()
    {
        File table=new File("tableSortedByEncryption.txt");
        Formatter formatter = null;

        //creating a new file if it doesn't exist
        if (!table.exists())
        {
            try
            {
                table.createNewFile();
            }
            catch (IOException e) {
                System.err.println("Error: Could not create new file tableSortedByEncryption.txt");
                e.printStackTrace();
            }
        }

        //opening formatter
        try
        {
            formatter = new Formatter("tableSortedByEncryption.txt");
        }
        catch(FileNotFoundException foundNotFound)
        {
            System.err.println("Error: File tableSortedByEncryption.txt could not be found.");
        }

        //saving table to file
        formatter.format("%s%n",toStringSortedByCard());

        //closing formatter
        if(formatter!=null){
            formatter.close();
        }
    }

    public TreeMap<String, String> readSortByEncryptionFromFile()
    {
        File table = new File("tableSortedByEncryption.txt");
        Scanner scanner=null;
        TreeMap<String,String> cardsFromFile=new TreeMap<>();


        try{
            //opening scanner
            scanner = new Scanner(table);

            //adding card information to container
            while(scanner.hasNext()){
                String cardNumber=scanner.next();
                String encryption=scanner.next();
                cardsFromFile.put(encryption,cardNumber);
            }
        }
        catch (FileNotFoundException fileNotFoundException){
            System.err.println("Error: Could not find file tableSortedByEncryption.txt");
        }

        //closing scanner
        if ( scanner != null )
        {
            scanner.close();
        }

        return cardsFromFile;
    }
}
