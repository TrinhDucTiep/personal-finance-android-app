package com.example.my_budget.analyzeFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.my_budget.Data;
import com.example.my_budget.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;

public class WeekAnalyze extends Fragment {

    private PieChart pieChart;

    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef;

    private int totalFood, totalTransport, totalHouse, totalWater, totalElectric, totalInternet;
    private int totalAnimal, totalShopping, totalEdu, totalHealth, totalBeauty, totalEnt, totalOther, totalSalary, totalBonus, totalParents, totalOtherMoneyIn;
    private int sum, res, res1;

    public WeekAnalyze() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_week_analyze, container, false);

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());

        MutableDateTime epouch = new MutableDateTime();
        epouch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epouch, now.minusDays(4));
        Query query = budgetRef.orderByChild("week").equalTo(weeks.getWeeks());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalFood=0; totalTransport=0; totalHouse=0; totalWater=0; totalElectric=0; totalInternet=0;
                totalAnimal=0; totalShopping=0; totalEdu=0; totalHealth=0; totalBeauty=0; totalEnt=0; totalOther=0; totalSalary=0; totalBonus=0; totalParents=0; totalOtherMoneyIn=0;
                sum = 0; res = 0; res1 = 0;
                Data data;
                for(DataSnapshot snap : snapshot.getChildren()){
                    data = snap.getValue(Data.class);
                    sum += data.getAmount();
                    if(data.getItem().equals("Ăn uống")){
                        totalFood += data.getAmount();
                    } else if(data.getItem().equals("Di chuyển")){
                        totalTransport += data.getAmount();
                    } else if(data.getItem().equals("Thuê nhà")){
                        totalHouse += data.getAmount();
                    } else if(data.getItem().equals("Hóa đơn nước")){
                        totalWater += data.getAmount();
                    } else if(data.getItem().equals("Hóa đơn điện")){
                        totalElectric += data.getAmount();
                    } else if(data.getItem().equals("Hóa đơn internet")){
                        totalInternet += data.getAmount();
                    } else if(data.getItem().equals("Vật nuôi")){
                        totalAnimal += data.getAmount();
                    } else if(data.getItem().equals("Mua sắm")){
                        totalShopping += data.getAmount();
                    } else if(data.getItem().equals("Giáo dục")){
                        totalEdu += data.getAmount();
                    } else if(data.getItem().equals("Sức khỏe")){
                        totalHealth += data.getAmount();
                    } else if(data.getItem().equals("Làm đẹp")){
                        totalBeauty += data.getAmount();
                    } else if(data.getItem().equals("Giải trí")){
                        totalEnt += data.getAmount();
                    } else if(data.getItem().equals("Chi tiêu khác")){
                        totalOther += data.getAmount();
                    } else if(data.getItem().equals("Lương")){
                        totalSalary += data.getAmount();
                        sum -= data.getAmount();
                    } else if(data.getItem().equals("Tiền thưởng")){
                        totalBonus += data.getAmount();
                        sum -= data.getAmount();
                    } else if(data.getItem().equals("Trợ cấp")){
                        totalParents += data.getAmount();
                        sum -= data.getAmount();
                    } else if(data.getItem().equals("Thu nhập khác")){
                        totalOtherMoneyIn += data.getAmount();
                        sum -= data.getAmount();
                    }
                }
                //pie chart for money out
                PieChart pieChart = mView.findViewById(R.id.w_piechart);
                ArrayList<PieEntry> numbers = new ArrayList<>();
                if((float)totalFood/sum >= 0.05) numbers.add(new PieEntry(totalFood, "Ăn uống"));
                else { res+=totalFood; }
                if((float)totalTransport/sum >= 0.05) numbers.add(new PieEntry(totalTransport, "Di chuyển"));
                else { res+=totalTransport; }
                if((float)totalHouse/sum >= 0.05) numbers.add(new PieEntry(totalHouse, "Thuê nhà"));
                else { res+=totalHouse; }
                if((float)totalWater/sum >= 0.05) numbers.add(new PieEntry(totalWater, "Hóa đơn nước"));
                else { res+=totalWater; }
                if((float)totalElectric/sum >= 0.05) numbers.add(new PieEntry(totalElectric, "Hóa đơn điện"));
                else { res+=totalElectric; }
                if((float)totalInternet/sum >= 0.05) numbers.add(new PieEntry(totalInternet, "Hóa đơn internet"));
                else { res+=totalInternet; }
                if((float)totalAnimal/sum >= 0.05) numbers.add(new PieEntry(totalAnimal, "Vật nuôi"));
                else { res+=totalAnimal; }
                if((float)totalShopping/sum >= 0.05) numbers.add(new PieEntry(totalShopping, "Mua sắm"));
                else { res+=totalShopping; }
                if((float)totalEdu/sum >= 0.05) numbers.add(new PieEntry(totalEdu, "Giáo dục"));
                else { res+=totalEdu; }
                if((float)totalHealth/sum >= 0.05) numbers.add(new PieEntry(totalHealth, "Sức khỏe"));
                else { res+=totalHealth; }
                if((float)totalBeauty/sum >= 0.05) numbers.add(new PieEntry(totalBeauty, "Làm đẹp"));
                else { res+=totalBeauty; }
                if((float)totalEnt/sum >= 0.05) numbers.add(new PieEntry(totalEnt, "Giải trí"));
                else { res+=totalEnt; }
                if((float)totalOther/sum >= 0.05) numbers.add(new PieEntry(totalOther, "Chi tiêu khác"));
                else { res+=totalOther; }
                if((float)res/sum >= 0.04) numbers.add(new PieEntry(res, "...     "));

                PieDataSet pieDataSet = new PieDataSet(numbers, "");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieDataSet.setValueTextColor(Color.BLACK);
                pieDataSet.setValueTextSize(16f);
                pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                PieData pieData = new PieData(pieDataSet);

                pieChart.setData(pieData);
                pieChart.getDescription().setEnabled(false);
                pieChart.setEntryLabelColor(Color.BLACK);
                pieChart.setCenterText("Chi tiêu");
                //pieChart.showContextMenu();
                //pieChart.animate();
                pieChart.setExtraBottomOffset(20f);
                pieChart.setExtraLeftOffset(40f);
                pieChart.setExtraRightOffset(40f);
                //pieChart.setHoleRadius(30f);
                pieChart.animateY(1000);

                //piechart for money in

                PieChart pieChart1 = mView.findViewById(R.id.w_piechart1);
                ArrayList<PieEntry> numbers1 = new ArrayList<>();
                if((float)totalSalary/sum >= 0.05) numbers1.add(new PieEntry(totalSalary, "Lương"));
                else { res1+=totalSalary; }
                if((float)totalBonus/sum >= 0.05) numbers1.add(new PieEntry(totalBonus, "Tiền thưởng"));
                else { res1+=totalBonus; }
                if((float)totalParents/sum >= 0.05) numbers1.add(new PieEntry(totalParents, "Trợ cấp"));
                else { res1+=totalParents; }
                if((float)totalOtherMoneyIn/sum >= 0.05) numbers1.add(new PieEntry(totalOtherMoneyIn, "Thu nhập khác"));
                else { res1+=totalOtherMoneyIn; }
                if((float)res1/sum >= 0.04) numbers1.add(new PieEntry(res1, "...     "));

                PieDataSet pieDataSet1 = new PieDataSet(numbers1, "");
                pieDataSet1.setColors(ColorTemplate.LIBERTY_COLORS);
                pieDataSet1.setValueTextColor(Color.BLACK);
                pieDataSet1.setValueTextSize(16f);
                pieDataSet1.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                pieDataSet1.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                PieData pieData1 = new PieData(pieDataSet1);

                pieChart1.setData(pieData1);
                pieChart1.getDescription().setEnabled(false);
                pieChart1.setEntryLabelColor(Color.BLACK);
                pieChart1.setExtraBottomOffset(20f);
                pieChart1.setExtraLeftOffset(40f);
                pieChart1.setCenterText("Thu nhập");
                pieChart1.setExtraRightOffset(40f);
                pieChart1.animateY(1000);

                pieChart.invalidate();
                pieChart1.invalidate();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return mView;
    }
}