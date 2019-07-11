package nachoapps.smartwallet.layout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import nachoapps.smartwallet.R;

public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private Calendar defaultDate;

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener, Calendar date) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setListener(listener, date);
        return fragment;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener, Calendar defaultDate) {
        this.listener = listener;
        this.defaultDate = defaultDate;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year = defaultDate.get(Calendar.YEAR);
        int month = defaultDate.get(Calendar.MONTH);
        int day = defaultDate.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), R.style.AppTheme_DatePicker, listener, year, month, day);
    }

}
