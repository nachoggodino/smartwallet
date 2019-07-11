package nachoapps.smartwallet.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.navigation.NavigationView;

import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import nachoapps.smartwallet.R;
import nachoapps.smartwallet.classes.Account;
import nachoapps.smartwallet.classes.Money;
import nachoapps.smartwallet.classes.SmartWallet;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final String FILENAME = "SAVED_SMART_WALLET";

    private SmartWallet smartWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        SpeedDialView speedDialView = findViewById(R.id.fab);

        Button dropdownMain = findViewById(R.id.dropdown_main);
        Button dropdownIncomeVsExpense = findViewById(R.id.dropdown_income_vs_expense);

        setSupportActionBar(toolbar);


        //FAB
        addFabItem(speedDialView, R.id.new_account, R.drawable.ic_bank_white_24dp, "Nueva cuenta");
        addFabItem(speedDialView, R.id.new_transfer, R.drawable.ic_bank_transfer_white_24dp, "Nuevo registro", getResources().getColor(R.color.colorAccent));
        addFabItem(speedDialView, R.id.new_category, R.drawable.ic_category_white_24dp, "Nueva Categoría");

        speedDialView.setOnActionSelectedListener(fabListener);

        //NAVIGATION DRAWER
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //LAYOUT LISTENERS
        dropdownIncomeVsExpense.setOnClickListener(dropdownIncomeVsExpensesListener);

        if(getStoredSmartWallet() == null){
            smartWallet = new SmartWallet();
            storeSmartWallet(smartWallet);
        } else{
            //storeSmartWallet(new SmartWallet());
            smartWallet = getStoredSmartWallet();
        }

        //Rellenar LAYOUT aquí
        fillMainCard(smartWallet);
        fillAccountCard(smartWallet);
        fillIncomeExpenseCard(smartWallet);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        smartWallet = getStoredSmartWallet();
        updateLayout(smartWallet);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             CALLBACKS                                      //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_accounts) {
            Intent i = new Intent(MainActivity.this, AccountsActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_expenses) {

        } else if (id == R.id.nav_income) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             LISTENERS                                      //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    View.OnClickListener dropdownIncomeVsExpensesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.seven_days:
                            return true;
                        case R.id.thirty_days:
                            return true;
                        case R.id.year_days:
                            return true;
                        case R.id.custom_days:
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.inflate(R.menu.compared_to_popup_menu);
            popup.show();
        }
    };

    //FAB
    SpeedDialView.OnActionSelectedListener fabListener = new SpeedDialView.OnActionSelectedListener() {
        @Override
        public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
            Intent i;
            switch (speedDialActionItem.getId()) {
                case R.id.new_account:
                    i = new Intent(MainActivity.this, NewAccountActivity.class);
                    startActivity(i);
                    return false; // true to keep the Speed Dial open
                case R.id.new_category:
                    i = new Intent(MainActivity.this, NewCategoryActivity.class);
                    startActivity(i);
                    return false;
                case R.id.new_transfer:
                    i = new Intent(MainActivity.this, NewMovementActivity.class);
                    startActivity(i);
                    return false;
                default:
                    return false;
            }
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             STORAGE                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void storeSmartWallet(SmartWallet smartWallet){

        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            outputStream.write(SmartWallet.serialize(smartWallet).getBytes(Charset.forName("UTF8")));
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SmartWallet getStoredSmartWallet(){

        FileInputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            inputStream = openFileInput(FILENAME);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int character;
        StringBuilder serializedSWRecovered = new StringBuilder();

        try {
            if (bufferedReader != null) {
                while( (character = bufferedReader.read()) != -1){
                    serializedSWRecovered.append(Character.toString((char) character));
                }
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return SmartWallet.deSerialize(serializedSWRecovered.toString());
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             PRIVATE                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void addFabItem(SpeedDialView speedDialView, int id, int icon, String label){
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(id, icon)
                        .setLabel(label)
                        .create()
        );
    }

    private void addFabItem(SpeedDialView speedDialView, int id, int icon, String label, int iconBackgroundColor){
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(id, icon)
                        .setLabel(label)
                        .setFabBackgroundColor(iconBackgroundColor)
                        .create()
        );
    }

    private void updateLayout(SmartWallet smartWallet){
        fillAccountCard(smartWallet);
        fillMainCard(smartWallet);
        fillIncomeExpenseCard(smartWallet);
    }

    private void fillAccountCard(final SmartWallet smartWallet){
        int size = smartWallet.getAccountsSize();
        int[] idS = {R.id.first_account, R.id.second_account, R.id.third_account,
                R.id.fourth_account, R.id.fifth_account, R.id.sixth_account,
                R.id.seventh_account, R.id.eigth_account, R.id.nineth_account};
        for (int i = 0; i < 9; i++){
            final FrameLayout cardView = findViewById(idS[i]);
            final CardView innerCard = cardView.findViewById(R.id.general_card);
            final TextView textView = cardView.findViewById(R.id.card_textview);
            if (i < size && i < 8){
                cardView.setVisibility(View.VISIBLE);
                final Account account = smartWallet.getAccounts().get(i);
                textView.setText(account.getName());
                textView.setTextColor(Color.WHITE);
                innerCard.setCardBackgroundColor(account.getColor());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(textView.getText().toString().equals(account.getName())){
                            animateCardRoll(cardView, textView, account.getCurrentMoney().toFormattedString(smartWallet.getCurrency()));
                        } else{
                            animateCardRoll(cardView, textView, account.getName());
                        }
                    }
                });
            } else if (i == size || size > i){
                cardView.setVisibility(View.VISIBLE);
                textView.setText("Añadir cuenta");
                textView.setTextColor(getResources().getColor(R.color.textDarkGrey));
                innerCard.setCardBackgroundColor(getResources().getColor(R.color.backgroundGrey));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, NewAccountActivity.class);
                        startActivity(i);
                    }
                });
            } else{
                cardView.setVisibility(View.GONE);
            }
        }
    }

    private void fillMainCard(SmartWallet smartWallet){
        ((TextView)findViewById(R.id.total_money_tv)).setText(smartWallet.getTotalBalance().toFormattedString(smartWallet.getCurrency()));
    }

    private void fillIncomeExpenseCard(SmartWallet smartWallet){
        Money incomeExpenseResult = new Money();
        Money incomeResult = smartWallet.getIncomeOfCurrentMonth();
        Money expenseResult = smartWallet.getExpenseOfCurrentMonth();
        incomeExpenseResult.add(incomeResult);
        incomeExpenseResult.substract(expenseResult);

        ((TextView)findViewById(R.id.income_expense_result_tv)).setText(incomeExpenseResult.toFormattedString(smartWallet.getCurrency()));
        ((TextView)findViewById(R.id.income_result_tv)).setText(incomeResult.toFormattedString(smartWallet.getCurrency()));
        ((TextView)findViewById(R.id.expense_result_tv)).setText(expenseResult.toFormattedString(smartWallet.getCurrency()));
    }

    private void animateCardRoll(final View animatedView, final TextView switcherTextView, final String outText){
        ObjectAnimator inAnimator = ObjectAnimator.ofFloat(
                animatedView, "rotationX", 0.0f, 90f);
        inAnimator.setDuration(100);
        inAnimator.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator outAnimator = ObjectAnimator.ofFloat(
                animatedView, "rotationX", -90f, 0.0f);
        outAnimator.setDuration(100);
        outAnimator.setInterpolator(new DecelerateInterpolator());

        inAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                switcherTextView.setText(outText);
                outAnimator.start();

            }
        });
        animatedView.setCameraDistance(20*animatedView.getWidth());
        inAnimator.start();
    }

}
