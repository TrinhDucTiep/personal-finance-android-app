package com.example.my_budget.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.my_budget.dataLocal.AlarmReceivier;
import com.example.my_budget.historyFragment.History;
import com.example.my_budget.system.LoginActivity;
import com.example.my_budget.R;
import com.example.my_budget.dataLocal.DataLocalManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AccountFragment extends Fragment{

    private static final String EXTRA_INSTANCE_ID = "1";
    private ImageView imgAccount;
    private TextView txtUserName;
    private TextView txtUserEmail;
    private TextView txtHistory;
    private TextView selectedTime;
    private static Date date;
    private Switch switchButton;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar = Calendar.getInstance();

    private Button logoutBtn;

    public static int tHour=0, tMinute=0;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_account, container, false);
        createNotificationChannel();

        imgAccount = mView.findViewById(R.id.img_account);
        txtUserName = mView.findViewById(R.id.user_name);
        txtUserEmail = mView.findViewById(R.id.user_email);
        txtHistory = mView.findViewById(R.id.history_txt);
        selectedTime = mView.findViewById(R.id.selected_time);
        switchButton = mView.findViewById(R.id.switch_button);

        SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");

        //init for alarm states
        String curSetTime = DataLocalManager.getHourAlarm() + ":" + DataLocalManager.getMinuteAlarm();
        try {
            date = f24Hours.parse(curSetTime);
            selectedTime.setText(f12Hours.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(DataLocalManager.getOnOffAlarm()){
            switchButton.setChecked(true);
        }

        logoutBtn = mView.findViewById(R.id.logout_btn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        txtUserName.setText(user.getDisplayName());
        txtUserEmail.setText(user.getEmail());
        Glide.with(getContext()).load(user.getPhotoUrl()).error(R.drawable.ic_default_avatar_account).into(imgAccount);

        txtHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mView.getContext(), History.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mView.getContext())
                        .setTitle("My budget app")
                        .setMessage("Bạn có muốn đăng xuất?")
                        .setCancelable(false)
                        .setPositiveButton("Có", (dialog, id) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(mView.getContext(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        })
                        .setNegativeButton("Không", null)
                        .show();
            }
        });



        if (date != null) selectedTime.setText(f12Hours.format(date));

        selectedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                tHour = hourOfDay;
                                tMinute = minute;

                                DataLocalManager.setHourAlarm(tHour);
                                DataLocalManager.setMinuteAlarm(tMinute);
                                switchButton.setChecked(false);
                                DataLocalManager.setOnOffAlarm(false);

                                calendar.set(Calendar.HOUR_OF_DAY, tHour);
                                calendar.set(Calendar.MINUTE, tMinute);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);

                                String time = tHour + ":" + tMinute;

                                try {
                                    date = f24Hours.parse(time);

                                    selectedTime.setText(f12Hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(tHour, tMinute);



                timePickerDialog.show();
            }
        });

//        if(switchButton.isChecked()){
//            setAlarm();
//        }
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchButton.isChecked()){
                    DataLocalManager.setOnOffAlarm(true);
                    setAlarm();
//                    Toast.makeText(getContext(), String.valueOf(tMinute), Toast.LENGTH_SHORT).show();
                } else {
                    DataLocalManager.setOnOffAlarm(false);
                    cancelAlarm();
                }
            }
        });

        return mView;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void setAlarm() {
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceivier.class);

        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()-240000,
                AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(getContext(), "Cài đặt nhắc nhở thành công", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(){
        Intent intent = new Intent(getContext(), AlarmReceivier.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        if(alarmManager == null)
            alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getContext(), "Đã hủy nhắc nhở", Toast.LENGTH_SHORT).show();
    }

    public void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Alarm channel";
            String description = "This is description of Alarm channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("id1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


    }

}