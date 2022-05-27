package com.example.my_budget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.my_budget.fragment.AccountFragment;
import com.example.my_budget.fragment.AnalyzeFragment;
import com.example.my_budget.fragment.ScheduleFragment;
import com.example.my_budget.fragment.SpendFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int FRAGMENT_SPEND = 1;
    private static final int FRAGMENT_ANALYZE = 2;
    private static final int FRAGMENT_SCHEDULE = 3;
    private static final int FRAGMENT_ACCOUNT = 4;

    private int currentFragment = FRAGMENT_SPEND;

    private FloatingActionButton fab;

    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        fab = findViewById(R.id.fab);

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        // bottom navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_spend) {
                    openSpendFragment();
                } else if(id == R.id.action_analyze) {
                    openAnalyzeFragment();
                } else if(id == R.id.action_schedule) {
                    openScheduleFragment();
                } else if(id == R.id.action_account){
                    openAccountFragment();
                }
                return true;
            }
        });

        replaceFragment(new SpendFragment());

        // floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

    }


    private void addItem(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.item_spinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final EditText notes = myView.findViewById(R.id.note);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String budgetAmount = amount.getText().toString();
                String budgetNotes = notes.getText().toString();
                String budgetItem = itemSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Bạn chưa nhập số tiền");
                    return;
                }
                if(budgetItem.equals("Chọn nhóm")){
                    Toast.makeText(MainActivity.this, "Hãy chọn một nhóm", Toast.LENGTH_SHORT).show();
                }
                else{
                    loader.setMessage("đang lưu");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = budgetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar calendar = Calendar.getInstance();
                    String date = dateFormat.format(calendar.getTime());

                    MutableDateTime epouch = new MutableDateTime();
                    epouch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epouch, now);
                    Months months = Months.monthsBetween(epouch, now);

                    Data data = new Data(budgetItem, date, id, budgetNotes, Integer.parseInt(budgetAmount), weeks.getWeeks(),months.getMonths());
                    budgetRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void openSpendFragment(){
        if(currentFragment != FRAGMENT_SPEND){
            replaceFragment(new SpendFragment());
            currentFragment = FRAGMENT_SPEND;
        }
    }

    private void openAnalyzeFragment(){
        if(currentFragment != FRAGMENT_ANALYZE){
            replaceFragment(new AnalyzeFragment());
            currentFragment = FRAGMENT_ANALYZE;
        }
    }

    private void openScheduleFragment(){
        if(currentFragment != FRAGMENT_SCHEDULE){
            replaceFragment(new ScheduleFragment());
            currentFragment = FRAGMENT_SCHEDULE;
        }
    }

    private void openAccountFragment(){
        if(currentFragment != FRAGMENT_ACCOUNT){
            replaceFragment(new AccountFragment());
            currentFragment = FRAGMENT_ACCOUNT;
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main, fragment);
        transaction.commit();
    }

}