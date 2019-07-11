package nachoapps.smartwallet.classes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.maltaisn.icondialog.IconHelper;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nachoapps.smartwallet.R;

public class Account implements Serializable {

    public enum Kind {ACCOUNT, INCOME_CATEGORY, EXPENSE_CATEGORY}


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             FIELDS                                         //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String name;
    private Money currentMoney;
    private Money offsetMoney;
    private List<Movement> movements;
    private Kind kind;
    private int color;
    private int icon;
    private int accountID;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             CONSTRUCTORS                                   //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Account (String name, Kind kind, Money offsetMoney, int color, int icon, int accountID){
        this.name = name;
        this.kind = kind;
        this.offsetMoney = offsetMoney;
        this.currentMoney = offsetMoney;
        this.color = color;
        this.icon = icon;
        this.accountID = accountID;
        movements = new ArrayList<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             GETTERS                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Money getCurrentMoney() {
        return currentMoney;
    }

    public Kind getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getIcon() {
        return icon;
    }

    public Money getOffsetMoney() {
        return offsetMoney;
    }

    public List<Movement> getMovements() {
        return movements;
    }

    public int getAccountID() {
        return accountID;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             SETTERS                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public void setColor(int color) {
        this.color = color;
    }

    public void setCurrentMoney(Money currentMoney) {
        this.currentMoney = currentMoney;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOffsetMoney(Money offsetMoney) {
        this.offsetMoney = offsetMoney;
    }

    public void addMoney(Money amount){
        this.currentMoney.add(amount);
    }

    public void substractMoney(Money amount){
        this.currentMoney.substract(amount);
    }

    public void addMovement(Movement movement){
        //Probablemente harán falta en orden cronólogico.
        this.movements.add(movement);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             METHODS                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setUpImageView(Context context, IconHelper iconHelper, ImageView imageView){
        Drawable drawable = iconHelper.getIcon(this.icon).getDrawable(context);
        drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        Drawable background = context.getResources().getDrawable(R.drawable.circle_shape);
        background.setColorFilter(new PorterDuffColorFilter(this.color, PorterDuff.Mode.SRC_IN));
        imageView.setImageDrawable(drawable);
        imageView.setBackground(background);
    }

    public Money getResultBetweenTwoDates(Calendar startDate, Calendar endDate){
        Money result = new Money();
        for (Movement movement : movements){
            if (movement.getDate().after(startDate) && movement.getDate().before(endDate)){
                result.add(movement.getAmount());
            }
        }
        return result;
    }

    public Money getResultOfCurrentMonth() {
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, lastDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        return getResultBetweenTwoDates(firstDayOfMonth, lastDayOfMonth);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             PRIVATE                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////



}
