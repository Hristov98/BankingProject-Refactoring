package other;

//a class that contains validating functions for the card numbers
public class Validation
{
    //checks if a card number starts with 3,4,5 or 6 and has exactly 16 numbers
    public boolean validationByRegexDecrypted(String cardNumber)
    {
        return cardNumber.matches("^[3,4,5,6]\\d{15}$" );
    }

    //checks if a card number has exactly 16 numbers
    public boolean validationByRegexEncrypted(String cardNumber)
    {
        return cardNumber.matches("^\\d{16}$" );
    }

    //checks if a card number is valid according to the Luhn algorithm
    public boolean validationByLuhn(String cardNumber)
    {
        char[] charArr=cardNumber.toCharArray();
        int length=cardNumber.length();
        int parity=length%2;
        int digitSum=0;
        int currentDigit;

        for (int i=length-1;i>=0;i--)
        {
            currentDigit=(int)charArr[i]-'0';

            if (i%2==parity)
            {
                currentDigit*=2;
                currentDigit=currentDigit/10+currentDigit%10;
            }

            digitSum+=currentDigit;
        }

        return (digitSum%10)==0;
    }
}
