package nachoapps.smartwallet.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maltaisn.icondialog.IconHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nachoapps.smartwallet.R;
import nachoapps.smartwallet.classes.Account;
import nachoapps.smartwallet.classes.Movement;
import nachoapps.smartwallet.classes.SmartWallet;

public class LastMovesAdapter extends ArrayAdapter<Movement> {
    private List<Movement> items;
    private SmartWallet smartWallet;
    private Context context;

    public LastMovesAdapter(@NonNull Context context, int resource, @NonNull List<Movement> objects, SmartWallet smartWallet) {
        super(context, resource, objects);
        this.smartWallet = smartWallet;
        this.items = objects;
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert vi != null;
            convertView = vi.inflate(R.layout.last_moves_list_item, parent, false);
        }
        final Movement movement = items.get(position);
        final Account originAccount = smartWallet.getAccountFromId(movement.getOriginAccountID());
        final Account destinationAccount = smartWallet.getAccountFromId(movement.getDestinationAccountID());
        final Movement.Kind kind = movement.getKind();

        ((TextView)convertView.findViewById(R.id.titles_textview)).setText("De " + originAccount.getName() + " a " + destinationAccount.getName());
        ((TextView)convertView.findViewById(R.id.description_textview)).setText(movement.getDescription());
        ((TextView)convertView.findViewById(R.id.date_textview)).setText(SmartWallet.getStringFromDate(movement.getDate()));
        TextView amountTV = convertView.findViewById(R.id.money_textview);
        amountTV.setText(movement.getAmount().toFormattedString(smartWallet.getCurrency()));
        if(kind == Movement.Kind.EXPENSE){amountTV.setTextColor(context.getResources().getColor(R.color.negativeRed));}
        else if(kind == Movement.Kind.INCOME){amountTV.setTextColor(context.getResources().getColor(R.color.positiveGreen));}
        else if(kind == Movement.Kind.TRANSFER){amountTV.setTextColor(context.getResources().getColor(R.color.textDarkGrey));}

        final IconHelper iconHelper = IconHelper.getInstance(context);
        final ImageView fromImageView = convertView.findViewById(R.id.from_image_view);
        final ImageView toImageView = convertView.findViewById(R.id.to_image_view);

        if(iconHelper.isDataLoaded()){
            originAccount.setUpImageView(context, iconHelper, fromImageView);
            destinationAccount.setUpImageView(context, iconHelper, toImageView);
        } else{
            iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                @Override
                public void onDataLoaded() {
                    originAccount.setUpImageView(context, iconHelper, fromImageView);
                    destinationAccount.setUpImageView(context, iconHelper, toImageView);
                }
            });
        }
        return convertView;
    }
}