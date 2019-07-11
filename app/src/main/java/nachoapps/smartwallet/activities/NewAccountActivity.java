package nachoapps.smartwallet.activities;

import android.content.Context;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import androidx.cardview.widget.CardView;
import nachoapps.smartwallet.R;
import nachoapps.smartwallet.classes.Account;
import nachoapps.smartwallet.classes.Money;
import nachoapps.smartwallet.classes.SmartWallet;
import nachoapps.smartwallet.classes.SnackbarHelper;
import petrov.kristiyan.colorpicker.ColorPicker;

public class NewAccountActivity extends AppCompatActivity implements IconDialog.Callback{

    final String FILENAME = "SAVED_SMART_WALLET";

    private Spinner currencySpinner;
    private EditText nameEdit, offsetEdit;
    private ImageButton colorDropdownBtn, iconButton;
    private CardView colorShower;

    private IconDialog iconDialog;

    private int selectedColor;
    private int selectedDrawable;

    private int ifIndex = -1;

    private SmartWallet smartWallet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        nameEdit = findViewById(R.id.name_edit);
        offsetEdit = findViewById(R.id.offset_edit);
        colorDropdownBtn = findViewById(R.id.color_picker_button);
        colorShower = findViewById(R.id.color_picker);
        iconButton = findViewById(R.id.icon_btn);


        setSupportActionBar(myChildToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        smartWallet = getStoredSmartWallet();

        selectedDrawable = 252;
        selectedColor = getResources().getColor(R.color.colorPrimary);

        if(getIntent().getIntExtra("ACCOUNT_INDEX", -1) != -1){
            ifIndex = getIntent().getIntExtra("ACCOUNT_INDEX", -1);
            setAccountLayout(smartWallet.getAccounts().get(ifIndex));
        }

        colorDropdownBtn.setOnClickListener(dialogShowerListener);
        colorShower.setOnClickListener(dialogShowerListener);

        iconButton.setOnClickListener(iconPickerListener);
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedDrawable = icons[0].getId();
        Drawable drawable = IconHelper.getInstance(this).getIcon(selectedDrawable).getDrawable(this);
        drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.textDarkGrey), PorterDuff.Mode.SRC_IN));
        iconButton.setImageDrawable(drawable);
    }


    @Override
    public void onBackPressed() {
        if(ifIndex != -1){
            Intent i = new Intent(NewAccountActivity.this, AccountsActivity.class);
            i.putExtra("CURRENT_INDEX", ifIndex);
            startActivity(i);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_account_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.check_btn) {
            createOrOverwriteAccount(ifIndex);
            return true;
        }else if (id == android.R.id.home){
            if(ifIndex != -1){
                Intent i = new Intent(NewAccountActivity.this, AccountsActivity.class);
                i.putExtra("CURRENT_INDEX", ifIndex);
                startActivity(i);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private View.OnClickListener dialogShowerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ColorPicker colorPicker = new ColorPicker(NewAccountActivity.this);
            colorPicker.setRoundColorButton(true);
            colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                @Override
                public void setOnFastChooseColorListener(int position,int color) {
                    colorShower.setCardBackgroundColor(color);
                    selectedColor = color;
                }

                @Override
                public void onCancel(){
                    // put code
                }
            });
            colorPicker.show();
        }
    };

    private View.OnClickListener iconPickerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            iconDialog = new IconDialog();
            iconDialog.setShowHeaders(false, false);
            iconDialog.setShowSelectButton(false);
            iconDialog.setTitle(IconDialog.VISIBILITY_ALWAYS, "Seleccionar icono");
            iconDialog.show(getSupportFragmentManager(), "icon_dialog");
        }
    };



    private void setAccountLayout(final Account account){
        nameEdit.setText(account.getName());
        offsetEdit.setText(account.getOffsetMoney().toString());
        colorShower.setCardBackgroundColor(account.getColor());
        selectedColor = account.getColor();

        final IconHelper iconHelper = IconHelper.getInstance(this);
        if(iconHelper.isDataLoaded()){
            Drawable drawable = iconHelper.getIcon(account.getIcon()).getDrawable(this);
            drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.textDarkGrey), PorterDuff.Mode.SRC_IN));
            iconButton.setImageDrawable(drawable);
            selectedDrawable = account.getIcon();
        } else{
            iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                @Override
                public void onDataLoaded() {
                    Drawable drawable = iconHelper.getIcon(account.getIcon()).getDrawable(NewAccountActivity.this);
                    drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.textDarkGrey), PorterDuff.Mode.SRC_IN));
                    iconButton.setImageDrawable(drawable);
                    selectedDrawable = account.getIcon();
                }
            });
        }
    }

    private boolean checkInputs(){
        if(nameEdit.getText().toString().equals("")){
            Snackbar snack = Snackbar.make(nameEdit, "No olvides escribir un nombre.", Snackbar.LENGTH_LONG);
            SnackbarHelper.configSnackbar(this, snack);
            snack.show();
            return false;
        } else if(!smartWallet.checkIfNameExists(nameEdit.getText().toString().trim())){
            Snackbar snack = Snackbar.make(nameEdit, "Ya existe una cuenta con ese nombre.", Snackbar.LENGTH_LONG);
            SnackbarHelper.configSnackbar(this, snack);
            snack.show();
            return false;
        }
        return true;
    }

    private void createOrOverwriteAccount(int ifIndex){
        if(checkInputs()){
            Money amount = new Money();
            if(!offsetEdit.getText().toString().equals("")) {
                amount = new Money(offsetEdit.getText().toString());
            }
            Account account = new Account(
                    nameEdit.getText().toString().trim(), Account.Kind.ACCOUNT, amount,
                    selectedColor, selectedDrawable, smartWallet.getNextID());

            if(ifIndex > -1){
                smartWallet.getAccounts().set(ifIndex, account);
                storeSmartWallet(smartWallet);
                Intent i = new Intent(NewAccountActivity.this, AccountsActivity.class);
                i.putExtra("CURRENT_INDEX", ifIndex);
                startActivity(i);
            }else{
                smartWallet.addAccount(account);
                storeSmartWallet(smartWallet);
            }
            finish();
        }
    }



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
}
