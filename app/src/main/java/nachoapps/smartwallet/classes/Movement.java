package nachoapps.smartwallet.classes;

import java.io.Serializable;
import java.util.Calendar;

public class Movement implements Serializable {

    public enum Kind {INCOME, EXPENSE, TRANSFER}

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             FIELDS                                         //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Calendar date;
    private String description;
    private Money amount;
    private Money amountBefore;
    private Money amountAfter;
    private int originAccountID;
    private int destinationAccountID;
    private Kind kind;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             CONSTRUCTORS                                   //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Movement(Calendar date, String description, Money amount, int originAccountID, int destinationAccountID, Kind kind){
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.originAccountID = originAccountID;
        this.destinationAccountID = destinationAccountID;
        this.kind = kind;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             GETTERS                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Calendar getDate(){
         return this.date;
     }

    public Money getAmount() {
        return amount;
    }

    public Kind getKind() {
        return kind;
    }

    public int getOriginAccountID() {
        return originAccountID;
    }

    public int getDestinationAccountID() {
        return destinationAccountID;
    }

    public Money getAmountBefore() {
        return amountBefore;
    }

    public Money getAmountAfter() {
        return amountAfter;
    }

    public String getDescription() {
        return description;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             SETTERS                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setOriginAccountID(int originAccountID) {
        this.originAccountID = originAccountID;
    }

    public void setDestinationAccountID(int destinationAccountID) {
        this.destinationAccountID = destinationAccountID;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             METHODS                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             PRIVATE                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

}
