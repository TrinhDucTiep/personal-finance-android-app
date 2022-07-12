package com.example.my_budget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.my_budget.customSpinner.CustomAdapter;
import com.example.my_budget.customSpinner.CustomItem;
import com.example.my_budget.database.DataSchedule;
import com.example.my_budget.database.DataSpend;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int FRAGMENT_SPEND = 1;
    private static final int FRAGMENT_ANALYZE = 2;
    private static final int FRAGMENT_SCHEDULE = 3;
    private static final int FRAGMENT_ACCOUNT = 4;

    private int currentFragment = FRAGMENT_SPEND;

    private FloatingActionButton fab;

    private DatabaseReference budgetRef;
    private DatabaseReference scheduleRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private EditText amount;

    // for spinner
    ArrayList<CustomItem> customList;
    private String budgetItem = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        fab = findViewById(R.id.fab);

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        scheduleRef = FirebaseDatabase.getInstance().getReference().child("schedule").child(mAuth.getCurrentUser().getUid());
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

    private ArrayList<CustomItem> getCustomList(){
        customList = new ArrayList<>();
        customList.add(new CustomItem("CHỌN NHÓM", R.drawable.ic_question_mark));
        customList.add(new CustomItem("---CHI TIÊU---", R.drawable.ic_question_mark));
        customList.add(new CustomItem("Ăn uống", R.drawable.ic_food));
        customList.add(new CustomItem("Di chuyển", R.drawable.ic_transport));
        customList.add(new CustomItem("Giải trí", R.drawable.ic_entertainment));
        customList.add(new CustomItem("Giáo dục", R.drawable.ic_education));
        customList.add(new CustomItem("Hóa đơn điện", R.drawable.ic_electric));
        customList.add(new CustomItem("Hóa đơn internet", R.drawable.ic_internet));
        customList.add(new CustomItem("Hóa đơn nước", R.drawable.ic_water));
        customList.add(new CustomItem("Làm đẹp", R.drawable.ic_beauty));
        customList.add(new CustomItem("Mua sắm", R.drawable.ic_shopping));
        customList.add(new CustomItem("Sức khỏe", R.drawable.ic_health));
        customList.add(new CustomItem("Thuê nhà", R.drawable.ic_house));
        customList.add(new CustomItem("Vật nuôi", R.drawable.ic_animal));
        customList.add(new CustomItem("Chi tiêu khác", R.drawable.ic_other));
        customList.add(new CustomItem("---THU NHẬP---", R.drawable.ic_question_mark));
        customList.add(new CustomItem("Lương", R.drawable.ic_salary));
        customList.add(new CustomItem("Tiền thưởng", R.drawable.ic_bonus));
        customList.add(new CustomItem("Trợ cấp", R.drawable.ic_parents));
        customList.add(new CustomItem("Thu nhập khác", R.drawable.ic_other_money_in));
        customList.add(new CustomItem("---VAY/NỢ---", R.drawable.ic_question_mark));
        customList.add(new CustomItem("Cho vay", R.drawable.ic_give));
        customList.add(new CustomItem("Nợ", R.drawable.ic_get));

        return customList;
    }

    private void addItem(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.item_spinner);
        amount = myView.findViewById(R.id.amount);
        final EditText notes = myView.findViewById(R.id.note);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        //for editText's format
        amount.addTextChangedListener(onTextChangedListener());

        //custom for spinner
        customList = getCustomList();
        CustomAdapter adapter = new CustomAdapter(this, customList);
        if (itemSpinner != null) {
            itemSpinner.setAdapter(adapter);
            itemSpinner.setOnItemSelectedListener(this);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String budgetAmount = amount.getText().toString();
                if (budgetAmount.contains(",")) {
                    budgetAmount = budgetAmount.replaceAll(",", "");
                }
                String budgetNotes = notes.getText().toString();
//                String budgetItem = itemSpinner.getSelectedItem().toString();
//                String budgetItem = itemSpinner.getSelectedItem().

                if(TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Bạn chưa nhập số tiền");
                    return;
                }
                if(budgetItem.equals("CHỌN NHÓM") || budgetItem.equals("---CHI TIÊU---") || budgetItem.equals("---THU NHẬP---") || budgetItem.equals("---VAY/NỢ---")){
                    Toast.makeText(MainActivity.this, "Hãy chọn một nhóm", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if((!budgetItem.equals("Cho vay")) && (!(budgetItem.equals("Nợ")))) {
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
                    Weeks weeks = Weeks.weeksBetween(epouch, now.minusDays(4));
                    Months months = Months.monthsBetween(epouch, now);

                    DataSpend data = new DataSpend(budgetItem, date, id, budgetNotes, Long.parseLong(budgetAmount), weeks.getWeeks(),months.getMonths());
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
                } else {
                    loader.setMessage("đang lưu");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = scheduleRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar calendar = Calendar.getInstance();
                    String date = dateFormat.format(calendar.getTime());

                    DataSchedule dataSchedule = new DataSchedule(budgetItem, date, id, budgetNotes, Long.parseLong(budgetAmount));
                    scheduleRef.child(id).setValue(dataSchedule).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CustomItem item = (CustomItem) parent.getSelectedItem();
//        Toast.makeText(this, item.getSpinnerItemName(), Toast.LENGTH_SHORT).show();
        budgetItem = item.getSpinnerItemName();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //text wachter for editText's format
    private TextWatcher onTextChangedListener(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                amount.removeTextChangedListener(this);
                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                   // DecimalFormat formatter = new DecimalFormat("###,###,###");
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("###,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    amount.setText(formattedString);
                    amount.setSelection(amount.getText().length());
                } catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }

                amount.addTextChangedListener(this);
            }
        };
    }
}