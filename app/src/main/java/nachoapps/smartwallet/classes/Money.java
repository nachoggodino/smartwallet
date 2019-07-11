package nachoapps.smartwallet.classes;


import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

public class Money implements Serializable {

    private BigDecimal amount;

    public Money(){
        this.amount = new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public Money(BigDecimal amount){
        this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public Money(String amount){
        this.amount = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @NotNull
    @Override
    public String toString(){
        return amount.toString();
    }

    public String toFormattedString(SmartWallet.Currency currency){
        return amount.toString() + " " + SmartWallet.getCurrencySymbol(currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void add(Money money){
        this.amount = this.amount.add(money.getAmount()).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public void substract(Money money){
        this.amount = this.amount.subtract(money.getAmount()).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }



}
