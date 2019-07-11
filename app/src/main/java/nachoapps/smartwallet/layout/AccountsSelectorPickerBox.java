package nachoapps.smartwallet.layout;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maltaisn.icondialog.IconHelper;
import com.ramotion.directselect.DSAbstractPickerBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nachoapps.smartwallet.R;
import nachoapps.smartwallet.classes.Account;

public class AccountsSelectorPickerBox extends DSAbstractPickerBox<Account> {
    private TextView text;
    private ImageView icon;
    private View cellRoot;

    public AccountsSelectorPickerBox(@NonNull Context context) {
        this(context, null);
    }

    public AccountsSelectorPickerBox(@NonNull Context context,
                                           @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountsSelectorPickerBox(@NonNull Context context,
                                           @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert mInflater != null;
        mInflater.inflate(R.layout.accounts_selector_picker_box, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.text = findViewById(R.id.custom_cell_text);
        this.icon = findViewById(R.id.custom_cell_image);
        this.cellRoot = findViewById(R.id.custom_cell_root);
    }

    @Override
    public void onSelect(final Account selectedItem, int selectedIndex) {
        Log.i("NACHOAPPS_SW", "La seleccioon marca " + selectedItem.getName());
        this.text.setText(selectedItem.getName());
        final IconHelper iconHelper = IconHelper.getInstance(getContext());

        if(iconHelper.isDataLoaded()){
            Drawable drawable = iconHelper.getIcon(selectedItem.getIcon()).getDrawable(getContext());
            drawable.setColorFilter(new PorterDuffColorFilter(selectedItem.getColor(), PorterDuff.Mode.SRC_IN));
            this.icon.setImageDrawable(drawable);
        } else{
            iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                @Override
                public void onDataLoaded() {
                    Drawable drawable = iconHelper.getIcon(selectedItem.getIcon()).getDrawable(getContext());
                    drawable.setColorFilter(new PorterDuffColorFilter(selectedItem.getColor(), PorterDuff.Mode.SRC_IN));
                    icon.setImageDrawable(drawable);
                }
            });
        }



    }

    @Override
    public View getCellRoot() {
        return this.cellRoot;
    }
}
