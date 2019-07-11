package nachoapps.smartwallet.classes;


import android.graphics.Color;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SmartWallet implements Serializable {

    public enum Currency {EURO, DOLLAR, POUND}

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             FIELDS                                         //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<Account> accounts;
    private List<Account> incomeCategories;
    private List<Account> expenseCategories;

    private Currency currency;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             CONSTRUCTORS                                   //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SmartWallet(){

        accounts = new ArrayList<>();
        incomeCategories = new ArrayList<>();
        expenseCategories = new ArrayList<>();

        currency = Currency.EURO;

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             GETTERS                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int getAccountsSize(){
        return accounts.size();
    }

    //LIST GETTERS

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Account> getExpenseCategories() {
        return expenseCategories;
    }

    public List<Account> getIncomeCategories() {
        return incomeCategories;
    }

    //CURRENCY

    public Currency getCurrency() {
        return currency;
    }

    public String getCurrencySymbol(){
        switch (this.currency){
            case EURO:
                return "€";
            case POUND:
                return "£";
            case DOLLAR:
                return "$";
        }
        return "NO_SYMBOL";
    }

    //ID

    public int getNextID(){
        List<Account> list = new ArrayList<>();
        list.addAll(accounts);
        list.addAll(incomeCategories);
        list.addAll(expenseCategories);

        if(list.isEmpty()){return 0;}

        int i = 0;
        while(true){
            boolean found = false;
            for(Account account: list){
                if(account.getAccountID() == i){
                    i++;
                    found = true;
                    break;
                }
            }
            if(!found){
                return i;
            }
        }
    }

    public Account getAccountFromId(int id){
        if(id == -10){
            return getNoCategoryAccount();
        } else if(id == -20){
            return getRemovedAccount();
        }else if(id == -30){
            return getAddAccount();
        }

        List<Account> list = new ArrayList<>();
        list.addAll(accounts);
        list.addAll(incomeCategories);
        list.addAll(expenseCategories);

        for(Account account: list){
            if(account.getAccountID() == id){
                return account;
            }
        }
        return null;
    }

    public List<Integer> getIDs(Movement.Kind kind, boolean isOrigin){
        List<Integer> list = new ArrayList<>();
        if(kind == Movement.Kind.TRANSFER || (kind == Movement.Kind.EXPENSE && isOrigin) || (kind == Movement.Kind.INCOME && !isOrigin)){
            for (Account account : accounts){
                list.add(account.getAccountID());
            }
        } else if(kind == Movement.Kind.EXPENSE){
            for (Account account : expenseCategories){
                list.add(account.getAccountID());
            }
        } else if(kind == Movement.Kind.INCOME){
            for (Account account : incomeCategories){
                list.add(account.getAccountID());
            }
        }
        return list;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             STATIC                                         //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getCurrencySymbol(Currency currency){
        switch (currency){
            case EURO:
                return "€";
            case POUND:
                return "£";
            case DOLLAR:
                return "$";
        }
        return "NO_SYMBOL";
    }

    public static String getStringFromDate(Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("EE dd 'de' MMM yyyy", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             SETTERS                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             METHODS                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addAccount(Account account){
        switch (account.getKind()){
            case ACCOUNT:
                accounts.add(account);
                break;
            case EXPENSE_CATEGORY:
                expenseCategories.add(account);
                break;
            case INCOME_CATEGORY:
                incomeCategories.add(account);
                break;
        }
    }

    public void removeAccount(int id){
        Account account = getAccountFromId(id);
        switch (account.getKind()){
            case ACCOUNT:
                accounts.remove(account);
                break;
            case EXPENSE_CATEGORY:
                expenseCategories.remove(account);
                break;
            case INCOME_CATEGORY:
                incomeCategories.remove(account);
                break;
        }

        for(Account possibleAccount : accounts){
            for(Movement movement : possibleAccount.getMovements()){
                if(movement.getOriginAccountID() == id){
                    movement.setOriginAccountID(-20);
                } else if(movement.getDestinationAccountID() == id){
                    movement.setDestinationAccountID(-20);
                }
            }
        }

        for(Account possibleAccount : incomeCategories){
            for(Movement movement : possibleAccount.getMovements()){
                if(movement.getOriginAccountID() == id){
                    movement.setOriginAccountID(-10);
                } else if(movement.getDestinationAccountID() == id){
                    movement.setDestinationAccountID(-10);
                }
            }
        }

        for(Account possibleAccount : incomeCategories){
            for(Movement movement : possibleAccount.getMovements()){
                if(movement.getOriginAccountID() == id){
                    movement.setOriginAccountID(-10);
                } else if(movement.getDestinationAccountID() == id){
                    movement.setDestinationAccountID(-10);
                }
            }
        }
    }

    //MOVEMENT

    public void executeMovement(Movement movement){
        Account originAccount = getAccountFromId(movement.getOriginAccountID());
        Account destinationAccount = getAccountFromId(movement.getDestinationAccountID());
        switch (movement.getKind()){
            case INCOME:
            case TRANSFER:
            default:
                originAccount.addMoney(movement.getAmount());
                destinationAccount.addMoney(movement.getAmount());
                break;
            case EXPENSE:
                originAccount.substractMoney(movement.getAmount());
                destinationAccount.addMoney(movement.getAmount());
                break;
        }
        originAccount.addMovement(movement);
        destinationAccount.addMovement(movement);
    }

    //GET BALANCES

    public Money getTotalBalance(){
        Money accountsResult = new Money();
        for(Account account : this.accounts){
            accountsResult.add(account.getCurrentMoney());
        }
        return accountsResult;
    }

    public Money getIncomeOfCurrentMonth(){
        Money result = new Money();
        for (Account category : incomeCategories){
            Log.i("NACHOAPPS_SW", category.getName());
            result.add(category.getResultOfCurrentMonth());
        }
        return result;
    }

    public Money getExpenseOfCurrentMonth(){
        Money result = new Money();
        for (Account category : expenseCategories){
            Log.i("NACHOAPPS_SW", category.getName());
            result.add(category.getResultOfCurrentMonth());
        }
        return result;
    }

    //CHECKERS

    public boolean checkIfNameExists(String name){
        for (Account account : accounts){
            if(account.getName().equals(name)){
                return false;
            }
        }
        for (Account account : expenseCategories){
            if(account.getName().equals(name)){
                return false;
            }
        }
        for (Account account : incomeCategories){
            if(account.getName().equals(name)){
                return false;
            }
        }
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             PRIVATE                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////




    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             SERIALIZATION                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static String serialize(SmartWallet smartWallet){
        Gson listJson = new Gson();
        return listJson.toJson(smartWallet);
    }

    public static SmartWallet deSerialize(String json){
        return new Gson().fromJson(json,
                new TypeToken<SmartWallet>(){}.getType());
    }

    public static Account getNoCategoryAccount(){
        return new Account("Cualquiera", Account.Kind.EXPENSE_CATEGORY, new Money(), Color.GRAY, 37, -10);
    }

    public static Account getRemovedAccount(){
        return new Account("Eliminada", Account.Kind.ACCOUNT, new Money(), Color.GRAY, 46, -20);
    }

    public static Account getAddAccount(){
        return new Account("Crear nueva", Account.Kind.ACCOUNT, new Money(), Color.GRAY, 100
                , -20);
    }
}
