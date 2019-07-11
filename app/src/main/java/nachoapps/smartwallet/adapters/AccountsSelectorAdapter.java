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

public class AccountsSelectorAdapter extends ArrayAdapter<Account> {
    private List<Account> items;
    private Context context;

    public AccountsSelectorAdapter(@NonNull Context context, int resource, @NonNull List<Account> objects) {
        super(context, resource, objects);
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
        final AccountsSelectorAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert vi != null;
            convertView = vi.inflate(R.layout.accounts_selector_list_item, parent, false);
            holder = new AccountsSelectorAdapter.ViewHolder();
            holder.text = convertView.findViewById(R.id.custom_cell_text);
            holder.icon = convertView.findViewById(R.id.custom_cell_image);
            convertView.setTag(holder);
        } else {
            holder = (AccountsSelectorAdapter.ViewHolder) convertView.getTag();
        }
        if (holder != null) {
            holder.text.setText(items.get(position).getName());
            Log.i("NACHOAPPS_SW", "Se añade " + items.get(position).getName());
            final IconHelper iconHelper = IconHelper.getInstance(context);
            if(iconHelper.isDataLoaded()){
                Drawable drawable = IconHelper.getInstance(context).getIcon(items.get(position).getIcon()).getDrawable(context);
                drawable.setColorFilter(new PorterDuffColorFilter(items.get(position).getColor(), PorterDuff.Mode.SRC_IN));
                holder.icon.setImageDrawable(drawable);
            } else{
                iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                    @Override
                    public void onDataLoaded() {
                        Drawable drawable = IconHelper.getInstance(context).getIcon(items.get(position).getIcon()).getDrawable(context);
                        drawable.setColorFilter(new PorterDuffColorFilter(items.get(position).getColor(), PorterDuff.Mode.SRC_IN));
                        holder.icon.setImageDrawable(drawable);
                    }
                });
            }
        }
        return convertView;
    }

    public void setItems(List<Account> items) {
        this.items = items;
    }

    private class ViewHolder {
        TextView text;
        ImageView icon;
    }
}