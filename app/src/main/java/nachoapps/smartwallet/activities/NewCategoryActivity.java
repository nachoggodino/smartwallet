package nachoapps.smartwallet.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconHelper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import nachoapps.smartwallet.R;
import nachoapps.smartwallet.classes.Account;
import nachoapps.smartwallet.classes.Money;
import nachoapps.smartwallet.classes.SmartWallet;
import nachoapps.smartwallet.classes.SnackbarHelper;
import petrov.kristiyan.colorpicker.ColorPicker;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class NewCategoryActivity extends AppCompatActivity implements IconDialog.Callback{

    final String FILENAME = "SAVED_SMART_WALLET";

    private EditText nameEdit;
    private ToggleSwitch toggleSwitch;
    private ImageButton iconBtn, colorDropDownBtn;
    CardView colorCard;

    private IconDialog iconDialog;

    private int ifIndex;

    private int selectedColor;
    private int selectedDrawable;
    private Account.Kind kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("Nueva categoría");

        nameEdit = findViewById(R.id.name_edit);
        toggleSwitch = (ToggleSwitch)findViewById(R.id.kind_switch);
        iconBtn = findViewById(R.id.icon_btn);
        colorDropDownBtn = findViewById(R.id.color_picker_button);
        colorCard = findViewById(R.id.color_picker);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ifIndex = getIntent().getIntExtra("ACCOUNT_INDEX", -1);

        selectedDrawable = 269;
        selectedColor = getResources().getColor(R.color.colorPrimary);

        colorDropDownBtn.setOnClickListener(dialogShowerListener);
        colorCard.setOnClickListener(dialogShowerListener);

        iconBtn.setOnClickListener(iconPickerListener);

        toggleSwitch.setOnChangeListener(new ToggleSwitch.OnChangeListener(){
            @Override
            public void onToggleSwitchChanged(int position) {
                if(position == 0){
                    kind = Account.Kind.EXPENSE_CATEGORY;
                } else{
                    kind = Account.Kind.INCOME_CATEGORY;
                }
            }
        });

        toggleSwitch.setCheckedPosition(0);
        kind = Account.Kind.EXPENSE_CATEGORY;

    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedDrawable = icons[0].getId();
        Drawable drawable = IconHelper.getInstance(this).getIcon(selectedDrawable).getDrawable(this);
        drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.textDarkGrey), PorterDuff.Mode.SRC_IN));
        iconBtn.setImageDrawable(drawable);
    }

    @Override
    public void onBackPressed() {
        if(ifIndex != -1){
            //Habra que...
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
            if(checkInputs()){
                SmartWallet smartWallet = getStoredSmartWallet();
                Account account = new Account(nameEdit.getText().toString().trim(), kind, new Money(), selectedColor, selectedDrawable, smartWallet.getNextID());

                smartWallet.addAccount(account);
                storeSmartWallet(smartWallet);
                if(ifIndex != -1){
                    //HAbrá que hacer algo.
                }
                finish();
            }
            return true;
        } else if(id == android.R.id.home){
            if(ifIndex != -1){
                //Habra que...
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener dialogShowerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ColorPicker colorPicker = new ColorPicker(NewCategoryActivity.this);
            colorPicker.setRoundColorButton(true);
            colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                @Override
                public void setOnFastChooseColorListener(int position,int color) {
                    colorCard.setCardBackgroundColor(color);
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



    private boolean checkInputs(){
        if(nameEdit.getText().toString().equals("")){
            Snackbar snack = Snackbar.make(nameEdit, "No olvides escribir un nombre!", Snackbar.LENGTH_SHORT);
            SnackbarHelper.configSnackbar(this, snack);
            snack.show();
            return false;
        }
        return true;
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
