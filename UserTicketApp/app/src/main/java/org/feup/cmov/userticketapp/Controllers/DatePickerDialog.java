package org.feup.cmov.userticketapp.Controllers;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.R;

import java.util.Calendar;
import java.util.Date;

public class DatePickerDialog extends AppCompatDialogFragment {

    public interface DatePickerDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    DatePickerDialogListener mListener;
    SharedDataSingleton sharedData = SharedDataSingleton.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.date_picker_layout, null);

        final DialogFragment fragment = this;

        builder.setView(view)
                .setPositiveButton("Pick Date", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker_date_picker);
                        TimePicker timePicker = (TimePicker) view.findViewById(R.id.date_picker_time_picker);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, datePicker.getYear());
                        cal.set(Calendar.MONTH, datePicker.getMonth());
                        cal.set(Calendar.DAY_OF_MONTH,  datePicker.getDayOfMonth());
                        cal.set(Calendar.HOUR,  timePicker.getCurrentHour());
                        cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                        cal.set(Calendar.AM_PM, timePicker.getCurrentHour() < 12 ? Calendar.AM : Calendar.PM);
                        Date dateRepresentation = cal.getTime();

                        sharedData.setSelectedDate(dateRepresentation);

                        mListener.onDialogPositiveClick(fragment);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DatePickerDialog.this.getDialog().cancel();
                        mListener.onDialogNegativeClick(fragment);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DatePickerDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}