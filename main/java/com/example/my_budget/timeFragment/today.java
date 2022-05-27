package com.example.my_budget.timeFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_budget.Data;
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

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class today extends Fragment {

    private TextView budgetAmountTextView;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef;

    //argument for update and delete
    private String postKey = "";
    private String item = "";
    private int amount = 0;
    private String note = "";
    private String dateToday = "";

    public today() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        budgetAmountTextView = view.findViewById(R.id.td_budgetAmountTextView);
        recyclerView = view.findViewById(R.id.td_recyclerview);
        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        dateToday = dateFormat.format(calendar.getTime());
        Query query = budgetRef.orderByChild("date").equalTo(dateToday);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;

                for(DataSnapshot snap : snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalAmount -= data.getAmount();
                    String strTotal = String.valueOf("Số dư: " + totalAmount + " đ");
                    budgetAmountTextView.setText(strTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        dateToday = dateFormat.format(calendar.getTime());
        Query query = budgetRef.orderByChild("date").equalTo(dateToday);

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setItemAmount(model.getAmount() + " đ");
                holder.setDate(model.getDate());
                holder.setItemName(model.getItem());
                holder.setNotes(model.getNotes());


                switch (model.getItem()){
                    case "Ăn uống":
                        holder.imageView.setImageResource(R.drawable.ic_food);
                        break;
                    case "Di chuyển":
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                        break;
                    case "Hóa đơn điện":
                        holder.imageView.setImageResource(R.drawable.ic_electric);
                        break;
                    case "Hóa đơn internet":
                        holder.imageView.setImageResource(R.drawable.ic_internet);
                        break;
                    case "Hóa đơn nước":
                        holder.imageView.setImageResource(R.drawable.ic_water);
                        break;
                    case "Thuê nhà":
                        holder.imageView.setImageResource(R.drawable.ic_house);
                        break;
                }

                // update and delete
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postKey = getRef(holder.getBindingAdapterPosition()).getKey();
                        item = model.getItem();
                        amount = model.getAmount();
                        note = model.getNotes();

                        update();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new MyViewHolder(view);
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
        final EditText mAmount = mView.findViewById(R.id.udAmount);
        final EditText mNote = mView.findViewById(R.id.udNote);

//        mNote.setVisibility(View.GONE);

        mNote.setText(note);
//        mNote.setSelection(note.length()); bị null
        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button updateBtn = mView.findViewById(R.id.updateBtn);
        Button deleteBtn = mView.findViewById(R.id.deleteBtn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = Integer.parseInt(mAmount.getText().toString());
                note = mNote.getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendar = Calendar.getInstance();
                String date = dateFormat.format(calendar.getTime());

                MutableDateTime epouch = new MutableDateTime();
                epouch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epouch, now);
                Months months = Months.monthsBetween(epouch, now);

                Data data = new Data(item, date, postKey, note, amount, weeks.getWeeks(), months.getMonths());
                budgetRef.child(postKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                budgetRef.child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

}


