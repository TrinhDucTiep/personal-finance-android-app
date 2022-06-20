package com.example.my_budget.spendFragment;

import android.app.AlertDialog;
import android.os.Bundle;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Week extends Fragment {

    private TextView budgetAmountTextView, moneyInTextView, moneyOutTextView;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef;

    //argument for update and delete
    private String postKey = "";
    private String item = "";
    private int amount = 0;
    private String note = "";
    private Weeks weeks;
    private DecimalFormat formatter = new DecimalFormat("###,###,###");

    public Week() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        budgetAmountTextView = view.findViewById(R.id.w_budgetAmountTextView);
        moneyInTextView = view.findViewById(R.id.w_moneyIn);
        moneyOutTextView = view.findViewById(R.id.w_moneyOut);
        recyclerView = view.findViewById(R.id.w_recyclerview);
        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());

        MutableDateTime epouch = new MutableDateTime();
        epouch.setDate(0);
        DateTime now = new DateTime();
        weeks = Weeks.weeksBetween(epouch, now.minusDays(4));
        Query query = budgetRef.orderByChild("week").equalTo(weeks.getWeeks());


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int moneyIn=0, moneyOut=0;
                int totalAmount = 0;


                for(DataSnapshot snap : snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    if(data.getItem().equals("Lương") || data.getItem().equals("Tiền thưởng") || data.getItem().equals("Trợ cấp") || data.getItem().equals("Thu nhập khác")){
                        moneyIn+=data.getAmount();
                        String strMoneyIn = String.valueOf(formatter.format(moneyIn )+ " đ");
                        moneyInTextView.setText(strMoneyIn);
                    }else {
                        moneyOut+=data.getAmount();
                        String strMoneyOut = String.valueOf(formatter.format(moneyOut) + " đ");
                        moneyOutTextView.setText(strMoneyOut);
                    }

                    totalAmount = moneyIn - moneyOut;
                    String strTotal = String.valueOf(formatter.format(totalAmount) + " đ");
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

        MutableDateTime epouch = new MutableDateTime();
        epouch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epouch, now.minusDays(4));
        Query query = budgetRef.orderByChild("week").equalTo(weeks.getWeeks());

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, Week.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, Week.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Week.MyViewHolder holder, int position, @NonNull Data model) {
                holder.setItemAmount(formatter.format(model.getAmount()) + " đ");
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
                    case "Vật nuôi":
                        holder.imageView.setImageResource(R.drawable.ic_animal);
                        break;
                    case "Mua sắm":
                        holder.imageView.setImageResource(R.drawable.ic_shopping);
                        break;
                    case "Giáo dục":
                        holder.imageView.setImageResource(R.drawable.ic_education);
                        break;
                    case "Sức khỏe":
                        holder.imageView.setImageResource(R.drawable.ic_health);
                        break;
                    case "Làm đẹp":
                        holder.imageView.setImageResource(R.drawable.ic_beauty);
                        break;
                    case "Giải trí":
                        holder.imageView.setImageResource(R.drawable.ic_entertainment);
                        break;
                    case "Chi tiêu khác":
                        holder.imageView.setImageResource(R.drawable.ic_other);
                        break;
                    case "Lương":
                        holder.imageView.setImageResource(R.drawable.ic_salary);
                        break;
                    case "Tiền thưởng":
                        holder.imageView.setImageResource(R.drawable.ic_bonus);
                        break;
                    case "Trợ cấp":
                        holder.imageView.setImageResource(R.drawable.ic_parents);
                        break;
                    case "Thu nhập khác":
                        holder.imageView.setImageResource(R.drawable.ic_other_money_in);
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
            public Week.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new Week.MyViewHolder(view);
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
                weeks = Weeks.weeksBetween(epouch, now.minusDays(4));
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