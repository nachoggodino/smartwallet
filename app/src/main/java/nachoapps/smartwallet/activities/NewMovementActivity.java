package nachoapps.smartwallet.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import com.nmaltais.calcdialog.CalcDialog;
import com.ramotion.directselect.DSListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import nachoapps.smartwallet.R;
import nachoapps.smartwallet.adapters.AccountsSelectorAdapter;
import nachoapps.smartwallet.classes.Account;
import nachoapps.smartwallet.classes.Money;
import nachoapps.smartwallet.classes.Movement;
import nachoapps.smartwallet.classes.SmartWallet;
import nachoapps.smartwallet.classes.SnackbarHelper;
import nachoapps.smartwallet.layout.DatePickerFragment;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NewMovementActivity extends AppCompatActivity implements CalcDialog.CalcDialogCallback {

    final String FILENAME = "SAVED_SMART_WALLET";
    final int CALC_REQUEST_CODE = 0;

    private Money selectedAmount;
    private Movement.Kind selectedKind;
    private Calendar selectedDate;
    private SmartWallet smartWallet;

    private ToggleSwitch toggleSwitch;
    private EditText offsetEdit, dateEdit, descriptionEdit;
    private TextView originTextView, destinationTextView;

    private AccountsSelectorAdapter originAdapter, destinationAdapter;
    private DSListView<Account> originPickerView, destinationPickerView;

    private int ifIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_movement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Nuevo registro");

        //LAYOUT
        toggleSwitch = findViewById(R.id.kind_switch);
        offsetEdit = findViewById(R.id.offset_edit);
        dateEdit = findViewById(R.id.date_edit);
        descriptionEdit = findViewById(R.id.description_edit);
        originPickerView = findViewById(R.id.ds_origin_listview);
        destinationPickerView = findViewById(R.id.ds_destination_listview);
        originTextView = findViewById(R.id.origin_account_tv);
        destinationTextView = findViewById(R.id.destination_account_tv);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Inicializacion de objetos
        smartWallet = getStoredSmartWallet();
        final List<Account> accountsList = smartWallet.getAccounts();
        final List<Account> incomeList = smartWallet.getIncomeCategories();
        incomeList.add(SmartWallet.getNoCategoryAccount());
        final List<Account> expenseList = smartWallet.getExpenseCategories();
        expenseList.add(SmartWallet.getNoCategoryAccount());

        List<Account> originList = new ArrayList<>(accountsList);
        List<Account> destinationList = new ArrayList<>(accountsList);

        final CalcDialog calcDialog = CalcDialog.newInstance(CALC_REQUEST_CODE);


        //Adapter creation
        originAdapter = new AccountsSelectorAdapter(
                this, R.layout.accounts_selector_list_item, originList);
        destinationAdapter = new AccountsSelectorAdapter(
                this, R.layout.accounts_selector_list_item, destinationList);

        // Adapter set
        originPickerView.setAdapter(originAdapter);
        destinationPickerView.setAdapter(destinationAdapter);

        //Inicializacion de variables
        selectedAmount = new Money();
        selectedKind = Movement.Kind.TRANSFER;
        selectedDate = Calendar.getInstance();
        ifIndex = getIntent().getIntExtra("ACCOUNT_INDEX", -1);

        // Layout basic setup
        toggleSwitch.setCheckedPosition(1);
        originPickerView.bringToFront();
        dateEdit.setText(SmartWallet.getStringFromDate(selectedDate));
        offsetEdit.setText(new Money().toFormattedString(smartWallet.getCurrency()));
        calcDialog.setFormatSymbols('.', ',');
        calcDialog.setSignCanBeChanged(false, 1);
        calcDialog.setShowSignButton(false);
        if(ifIndex != -1){
            originPickerView.setSelectedIndex(getIntent().getIntExtra("ACCOUNT_INDEX", 0));
            destinationPickerView.setSelectedIndex(getIntent().getIntExtra("ACCOUNT_INDEX", 0));
        }

        // Listeners
        offsetEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcDialog.setValue(selectedAmount.getAmount());
                calcDialog.show(getSupportFragmentManager(), "calc_dialog");

            }
        });

        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(Calendar.getInstance());

            }
        });

        toggleSwitch.setOnChangeListener(new ToggleSwitch.OnChangeListener() {
            @Override
            public void onToggleSwitchChanged(int i) {
                originAdapter.clear();
                destinationAdapter.clear();
                switch (i) {
                    case 0:
                        changeOnToggle(accountsList, expenseList, "Desde la cuenta", "Categoría de gastos");
                        selectedKind = Movement.Kind.EXPENSE;
                        break;
                    case 1:
                        changeOnToggle(accountsList, accountsList, "Desde la cuenta", "A la cuenta");
                        selectedKind = Movement.Kind.TRANSFER;
                        break;
                    case 2:
                        changeOnToggle(incomeList, accountsList, "Categoría de ingresos", "A la cuenta");
                        selectedKind = Movement.Kind.INCOME;
                        break;
                    default:
                        changeOnToggle(accountsList, accountsList, "Desde la cuenta", "A la cuenta");
                        selectedKind = Movement.Kind.TRANSFER;
                }
            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             CALLBACKS                                      //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        if(ifIndex != -1){
            Intent i = new Intent(NewMovementActivity.this, AccountsActivity.class);
            i.putExtra("CURRENT_INDEX", ifIndex);
            startActivity(i);
        }
        finish();
    }

    @Override
    public void onValueEntered(int requestCode, BigDecimal value) {
        selectedAmount = new Money(value);
        offsetEdit.setText(selectedAmount.toFormattedString(smartWallet.getCurrency()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_account_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.check_btn) {
            if(checkInputs()){
                String description = descriptionEdit.getText().toString();
                int originAccount = smartWallet.getIDs(selectedKind, true).get(originPickerView.getSelectedIndex());
                int destinationAccount = smartWallet.getIDs(selectedKind, false).get(destinationPickerView.getSelectedIndex());
                smartWallet.executeMovement(new Movement(selectedDate, description, selectedAmount, originAccount, destinationAccount, selectedKind));
                storeSmartWallet(smartWallet);
                finish();
            }
            return true;
        } else if(id == android.R.id.home){
            if(ifIndex != -1){
                Intent i = new Intent(NewMovementActivity.this, AccountsActivity.class);
                i.putExtra("CURRENT_INDEX", ifIndex);
                startActivity(i);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             STORAGE                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void storeSmartWallet(SmartWallet smartWallet) {

        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            outputStream.write(SmartWallet.serialize(smartWallet).getBytes(Charset.forName("UTF8")));
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SmartWallet getStoredSmartWallet() {

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
                while ((character = bufferedReader.read()) != -1) {
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

    private void changeOnToggle(List<Account> originItems, List<Account> destinationItems, String originText, String destinationText){
        originAdapter.addAll(originItems);
        destinationAdapter.addAll(destinationItems);
        originTextView.setText(originText);
        destinationTextView.setText(destinationText);
        originAdapter.notifyDataSetChanged();
        destinationAdapter.notifyDataSetChanged();
        originPickerView.setSelectedIndex(new Random().nextInt(originItems.size()));
        destinationPickerView.setSelectedIndex(new Random().nextInt(destinationItems.size()));
    }

    private boolean checkInputs(){
        if(selectedAmount.getAmount().equals(new Money().getAmount())){
            Snackbar snack = Snackbar.make(offsetEdit, "La cantidad no puede ser 0", Snackbar.LENGTH_LONG);
            SnackbarHelper.configSnackbar(this, snack);
            snack.show();
            return false;
        } else if(selectedKind == Movement.Kind.TRANSFER && originPickerView.getSelectedIndex().equals(destinationPickerView.getSelectedIndex())){
            Snackbar snack = Snackbar.make(originPickerView, "Las cuentas deben ser distintas.", Snackbar.LENGTH_LONG);
            SnackbarHelper.configSnackbar(this, snack);
            snack.show();
            return false;
        }
        return true;
    }

    private void showDatePickerDialog(Calendar defaultDate) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                selectedDate.set(year, month, day);
                dateEdit.setText(SmartWallet.getStringFromDate(selectedDate));

            }
        }, defaultDate);
        newFragment.show(getSupportFragmentManager(),  "datePicker");
    }


}
