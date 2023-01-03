package com.example.yelpsearch.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.*;
import android.widget.*;
import androidx.annotation.LongDef;
import androidx.datastore.preferences.PreferencesProto;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.yelpsearch.R;
import com.example.yelpsearch.model.Reservation;
import com.google.gson.Gson;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

public class BusinessDetailFragment extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    String business_address_string = "";
    private Bundle bundle;
    ImageSlider imageSlider;
    ArrayList<SlideModel> slideModels = new ArrayList<SlideModel>();
    ArrayList<String> photos = new ArrayList<>();

    EditText res_email, res_date, res_time;
    Button res_submit, res_cancel;
    Calendar myCalendar = Calendar.getInstance();
    Calendar dateCalendar = Calendar.getInstance();
    int day = myCalendar.get(Calendar.DAY_OF_MONTH);
    int month = myCalendar.get(Calendar.MONTH);
    int year = myCalendar.get(Calendar.YEAR);
    Boolean timePickerBoolean = false;
    Boolean emailValidationBoolean = false;
    int selectedHourCheck;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_detail, container, false);

        TextView business_address = view.findViewById(R.id.business_address);
        TextView business_price = view.findViewById(R.id.business_price);
        TextView business_phone = view.findViewById(R.id.business_phone);
        TextView business_status = view.findViewById(R.id.business_status);
        TextView business_category = view.findViewById(R.id.business_category);
        TextView business_url = view.findViewById(R.id.business_url);
        imageSlider = view.findViewById(R.id.imageSlider);

        bundle = this.getArguments();
        Log.d("bundle check", bundle.toString());
        business_address.setText(bundle.getString("business_address"));
        business_price.setText(bundle.getString("business_price"));
        business_phone.setText(bundle.getString("business_phone"));
        business_status.setText(bundle.getString("business_status"));
        business_category.setText(bundle.getString("business_category"));
        photos = (ArrayList<String>) bundle.getSerializable("photo_list");

        if (business_status.getText().equals("Closed")) {
            business_status.setTextColor(this.getResources().getColor(R.color.purple_200));
        }

        setSlidePhotos(photos);

        business_url.setClickable(true);
        business_url.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href = \"" + bundle.getString("business_url") + "\">Business Link</a>";

        Log.d("bizUrl", text);
        business_url.setText(Html.fromHtml(text));

        //Set res dialog title to business name

        Button reserveButton = view.findViewById(R.id.reserve_button);
        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReservationDialog();
            }
        });

        sharedPreferences = getContext().getSharedPreferences("Reservations", 0);
        myEdit = sharedPreferences.edit();

        return view;
    }

    public void showReservationDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //user can cancel dialog by clicking anywhere outside dialog
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.reservation_dialog);

        TextView res_business_name = dialog.findViewById(R.id.res_business_name);
        res_business_name.setText(bundle.getString("business_name"));

        res_email = dialog.findViewById(R.id.res_email);
        res_date = dialog.findViewById(R.id.res_date);
        res_time = dialog.findViewById(R.id.res_time);
        res_submit = dialog.findViewById(R.id.res_submit);
        res_cancel = dialog.findViewById(R.id.res_cancel);
        
        dialog.show();

        res_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePicker;

                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        day = selectedDay;
                        month = selectedMonth;
                        year = selectedYear;

                        int displayMonth = selectedMonth + 1;
                        res_date.setText(displayMonth + "-" + selectedDay + "-" + selectedYear);
                    }
                }, year, month, day);
                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                mDatePicker.show();
            }
        });

        res_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        res_time.setText(selectedHour + ":" + selectedMinute);
                        selectedHourCheck = selectedHour;
/*                        if (selectedHour >= 10 && selectedHour <= 17) {
                            timePickerBoolean = true;
                        }*/
                    }
                }, hour,minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
                mTimePicker.show();
            }
        });


        res_submit.setOnClickListener((view -> {
            int size = sharedPreferences.getAll().size();
            Log.d("Current res size", String.valueOf(size));

            int index;
            if (size == 0){
                index = 1;
            } else {
                index = size + 1;
            }
            Log.d("res index", String.valueOf(index));

            String business_name = res_business_name.getText().toString();
            String date = res_date.getText().toString();
            String time = res_time.getText().toString();
            String email = res_email.getText().toString();

            emailValidator(res_email);

            if (selectedHourCheck >= 10 && selectedHourCheck <= 17) {
                timePickerBoolean = true;
            } else {
                timePickerBoolean = false;
            }

            if (emailValidationBoolean == false) {
                Toast.makeText(this.getContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            if (timePickerBoolean == false) {
                Toast.makeText(this.getContext(), "Time should be between 10AM and 5PM", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            if (emailValidationBoolean == true && timePickerBoolean == true){
                Reservation reservation = new Reservation(index,business_name, date, time, email);
                Log.d("NewRes", reservation.toString());

                Gson gson = new Gson();
                String json = gson.toJson(reservation);

                myEdit.putString(business_name, json);
                myEdit.apply();
                Toast.makeText(this.getContext(), "Reservation Booked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }));

        res_cancel.setOnClickListener((view) -> {
            dialog.dismiss();
        });
    }

    public void setSlidePhotos(ArrayList<String> photos) {
        for (int i = 0; i < photos.size(); i++) {
            slideModels.add(new SlideModel(photos.get(i), ScaleTypes.FIT));
        }
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

    public void emailValidator(EditText Email) {
        emailValidationBoolean = false;
        String emailString = Email.getText().toString();

        if (!emailString.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            emailValidationBoolean = true;
        }
    }
}

