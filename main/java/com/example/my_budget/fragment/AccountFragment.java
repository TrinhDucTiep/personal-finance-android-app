package com.example.my_budget.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.my_budget.History;
import com.example.my_budget.LoginActivity;
import com.example.my_budget.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class AccountFragment extends Fragment{

    private ImageView imgAccount;
    private TextView txtUserName;
    private TextView txtUserEmail;
    private TextView txtHistory;

    private Button logoutBtn;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_account, container, false);

        imgAccount = mView.findViewById(R.id.img_account);
        txtUserName = mView.findViewById(R.id.user_name);
        txtUserEmail = mView.findViewById(R.id.user_email);
        txtHistory = mView.findViewById(R.id.history_txt);

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

        return mView;
    }
}