package com.example.my_budget.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.my_budget.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    private ImageView imgAccount;
    private TextView txtUserName;
    private TextView txtUserEmail;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        txtUserName.setText(user.getDisplayName());
        txtUserEmail.setText(user.getEmail());
        Glide.with(getContext()).load(user.getPhotoUrl()).error(R.drawable.ic_default_avatar_account).into(imgAccount);

        return mView;
    }
}