package nachoapps.smartwallet.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import nachoapps.smartwallet.R;
import nachoapps.smartwallet.adapters.LastMovesAdapter;
import nachoapps.smartwallet.classes.Account;
import nachoapps.smartwallet.classes.Money;
import nachoapps.smartwallet.classes.SmartWallet;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maltaisn.icondialog.IconHelper;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

public class AccountsActivity extends AppCompatActivity {

    /**
     * The {@link androidx.viewpager.widget.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * androidx.fragment.app.FragmentStatePagerAdapter.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ListAccountsCustomAdapter listAccountsCustomAdapter;
    private SmartWallet smartWallet;
    private Toolbar toolbar;
    private ListView listView;

    final String FILENAME = "SAVED_SMART_WALLET";
    private boolean accountsHidden;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        smartWallet = getStoredSmartWallet();

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        listView = findViewById(R.id.accounts_listview);

        listAccountsCustomAdapter = new ListAccountsCustomAdapter(this, smartWallet.getAccounts());
        listView.setAdapter(listAccountsCustomAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mViewPager.setCurrentItem(getIntent().getIntExtra("CURRENT_INDEX", 0));

        toolbar.setTitle("Cuentas");
        accountsHidden = true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.accounts_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_showAll) {
            animateAllAccountsView(accountsHidden);
            if(accountsHidden){
                accountsHidden = false;
            } else{
                accountsHidden = true;
            }
            return true;
        } else if (id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             FRAGMENT                                       //
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_SMARTWALLET = "section_SMARTWALLET";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, SmartWallet smartWallet) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable(ARG_SECTION_SMARTWALLET, smartWallet);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            //RECUPERAMOS LOS ARGUMENTOS
            SmartWallet smartWallet = (SmartWallet)getArguments().getSerializable(ARG_SECTION_SMARTWALLET);
            final int position = getArguments().getInt(ARG_SECTION_NUMBER);

            //CUENTA BASE Y LAYOUT BASE
            final Account account = smartWallet.getAccounts().get(position);
            View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);
            final ViewPager vp = getActivity().findViewById(R.id.view_pager);

            //VARIABLES CLAVE
            int color = account.getColor();

            //SETUP FACIL DE LAYOUT
            ((TextView)rootView.findViewById(R.id.current_money)).setText(account.getCurrentMoney() + " " + smartWallet.getCurrencySymbol());
            //((CardView)rootView.findViewById(R.id.money_card)).setCardBackgroundColor(color);
            ((TextView)rootView.findViewById(R.id.title_tv)).setText(account.getName());

            //TÍTULO DE LA CUENTA E ICONO
            final IconHelper iconHelper = IconHelper.getInstance(getActivity());
            final ImageView titleIcon = rootView.findViewById(R.id.title_image_view);
            if(iconHelper.isDataLoaded()){
                account.setUpImageView(getActivity(), iconHelper, titleIcon);
            } else{
                iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                    @Override
                    public void onDataLoaded() {
                        account.setUpImageView(getActivity(), iconHelper, titleIcon);
                    }
                });
            }

            //PREV AND NEXT BUTTONS
            ImageButton prevBtn = rootView.findViewById(R.id.prev_btn);
            ImageButton nextBtn = rootView.findViewById(R.id.next_btn);
            if(position == 0){prevBtn.setVisibility(View.GONE);}
            else{
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vp.setCurrentItem(position-1, true);
                    }
                });
            }
            if(position == smartWallet.getAccountsSize()-1){nextBtn.setVisibility(View.GONE);}
            else{
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vp.setCurrentItem(position+1, true);
                    }
                });
            }

            //EDIT BUTTON
            Button editButton = rootView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), NewAccountActivity.class);
                    i.putExtra("ACCOUNT_INDEX", position);
                    startActivity(i);
                    getActivity().finish();
                }
            });

            //LAST MOVES BUTTON
            Button newMoveButton = rootView.findViewById(R.id.movement_button);
            newMoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), NewMovementActivity.class);
                    i.putExtra("ACCOUNT_INDEX", position);
                    startActivity(i);
                    getActivity().finish();
                }
            });

            //LASTMOVES LISTVIEW
            ListView listView = rootView.findViewById(R.id.last_moves_listview);
            LastMovesAdapter adapter = new LastMovesAdapter(getActivity(), R.layout.last_moves_list_item, account.getMovements(), smartWallet);
            listView.setAdapter(adapter);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position, smartWallet);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return smartWallet.getAccountsSize();
        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             LIST                                           //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public class ListAccountsCustomAdapter extends ArrayAdapter<Account>
    {

        private Context context;
        private List<Account> items;

        ListAccountsCustomAdapter(Context context, List<Account> items) {
            super(context, R.layout.accounts_list_item, items);
            this.context = context;
            this.items = items;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = convertView;

            if (convertView == null) {
                LayoutInflater inf = LayoutInflater.from(getContext());
                view = inf.inflate(R.layout.accounts_list_item, parent, false);
            }

            final Account account = items.get(position);

            String name = account.getName();
            int color = account.getColor();
            final int iconID = account.getIcon();
            Money amount = account.getCurrentMoney();

            ((TextView)view.findViewById(R.id.item_name_tv)).setText(name);
            ((TextView)view.findViewById(R.id.item_amount_tv)).setText(amount.toFormattedString(smartWallet.getCurrency()));
            final ImageView circularImageView = view.findViewById(R.id.circular_image_view);

            final IconHelper iconHelper = IconHelper.getInstance(this.context);
            if(iconHelper.isDataLoaded()){
                account.setUpImageView(context, iconHelper, circularImageView);
            } else{
                iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                    @Override
                    public void onDataLoaded() {
                        account.setUpImageView(context, iconHelper, circularImageView);
                    }
                });
            }

            view.findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(position, true);
                    animateAllAccountsView(accountsHidden);
                    if(accountsHidden){
                        accountsHidden = false;
                    } else{
                        accountsHidden = true;
                    }
                }
            });

            view.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    smartWallet.removeAccount(position);
                    storeSmartWallet(smartWallet);
                    finish();
                }
            });

            return view;
        }
    }

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

    private void animateAllAccountsView(boolean boo){

        final CardView allAccountsView = findViewById(R.id.all_accounts_view);
        final RelativeLayout mainRelative = findViewById(R.id.main_relative);
        TranslateAnimation animate;
        if(boo){
            animate = new TranslateAnimation(0, 0, allAccountsView.getHeight(),0);
        } else{
            animate = new TranslateAnimation(0, 0, 0,allAccountsView.getHeight());
        }
        animate.setDuration(500);
        animate.setInterpolator(new AccelerateDecelerateInterpolator());
        if(boo){
            allAccountsView.setVisibility(View.VISIBLE);
        } else{
            animate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    allAccountsView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        allAccountsView.startAnimation(animate);


    }

}
