package com.example.my_budget.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_budget.database.DataSchedule;
import com.example.my_budget.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleFragment extends Fragment {

    private TextView giveTxt, getTxt;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference scheduleRef;

    private String postKey = "";
    private String item = "";
    private EditText mAmount;
    private Long amount = Long.parseLong("0");
    private String note = "";
    private String dateToday = "";
    private DecimalFormat formatter = new DecimalFormat("###,###,###");

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_schedule, container, false);

        giveTxt = mView.findViewById(R.id.give_txt);
        getTxt = mView.findViewById(R.id.get_txt);
        recyclerView = mView.findViewById(R.id.recyclerview_schedule);
        mAuth = FirebaseAuth.getInstance();
        scheduleRef = FirebaseDatabase.getInstance().getReference().child("schedule").child(mAuth.getCurrentUser().getUid());

        Query query = scheduleRef;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long giveMoney=0, getMoney=0;
                for(DataSnapshot snap : snapshot.getChildren()){
                    DataSchedule dataSchedule = snap.getValue(DataSchedule.class);
                    if(dataSchedule.getItem().equals("Cho vay")){
                        giveMoney += dataSchedule.getAmount();
                        String strGiveMoney = String.valueOf(formatter.format(giveMoney) + " đ");
                        giveTxt.setText(strGiveMoney);
                    }else {
                        getMoney += dataSchedule.getAmount();
                        String strGetMoney = String.valueOf(formatter.format(getMoney) + " đ");
                        getTxt.setText(strGetMoney);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        dateToday = dateFormat.format(calendar.getTime());
        Query query = scheduleRef;

        FirebaseRecyclerOptions<DataSchedule> options = new FirebaseRecyclerOptions.Builder<DataSchedule>()
                .setQuery(query, DataSchedule.class)
                .build();

        FirebaseRecyclerAdapter<DataSchedule, ScheduleFragment.MyViewHolder> adapter = new FirebaseRecyclerAdapter<DataSchedule, ScheduleFragment.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ScheduleFragment.MyViewHolder holder, int position, @NonNull DataSchedule model) {
                holder.setItemAmount(formatter.format(model.getAmount()) + " đ");
                holder.setDate(model.getDate());
                holder.setItemName(model.getItem());
                holder.setNotes(model.getNote());


                switch (model.getItem()){
                    case "Cho vay":
                        holder.imageView.setImageResource(R.drawable.ic_give);
                        break;
                    case "Nợ":
                        holder.imageView.setImageResource(R.drawable.ic_get);
                        break;
                }

                // update and delete
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postKey = getRef(holder.getBindingAdapterPosition()).getKey();
                        item = model.getItem();
                        amount = model.getAmount();
                        note = model.getNote();

                        update();
                    }
                });
            }

            @NonNull
            @Override
            public ScheduleFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new ScheduleFragment.MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ImageView imageView;
        public TextView notes;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            notes = itemView.findViewById(R.id.note);
        }

        public void setItemName(String itemName){
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount(String itemAmount){
            TextView amount = mView.findViewById(R.id.amount);
            amount.setText(itemAmount);
        }

        public void setDate(String itemDate){
            TextView date = mView.findViewById(R.id.date);
            date.setText(itemDate);
        }

        public void setNotes(String itemNote){
            TextView note = mView.findViewById(R.id.note);
            note.setText(itemNote);
        }
    }

    private void update(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View mView = inflater.inflate(R.layout.update_layout, null);

        myDialog.setView(mView);

        final AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.udItemName);
        mAmount = mView.findViewById(R.id.udAmount);
        final EditText mNote = mView.findViewById(R.id.udNote);

        mAmount.addTextChangedListener(onTextChangedListener());

        mNote.setText(note);

        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button updateBtn = mView.findViewById(R.id.updateBtn);
        Button deleteBtn = mView.findViewById(R.id.deleteBtn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String budgetAmount = mAmount.getText().toString();
                if (budgetAmount.contains(",")) {
                    budgetAmount = budgetAmount.replaceAll(",", "");
                }
                if(TextUtils.isEmpty(budgetAmount)){
                    mAmount.setError("Bạn chưa nhập số tiền");
                    return;
                }
                amount = Long.parseLong(budgetAmount);
                note = mNote.getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendar = Calendar.getInstance();
                String date = dateFormat.format(calendar.getTime());

                DataSchedule dataSchedule = new DataSchedule(item, date, postKey, note, amount);
                scheduleRef.child(postKey).setValue(dataSchedule).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleRef.child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getContext(),task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
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
                mAmount.removeTextChangedListener(this);
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
                    mAmount.setText(formattedString);
                    mAmount.setSelection(mAmount.getText().length());
                } catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }

                mAmount.addTextChangedListener(this);
            }
        };
    }

}