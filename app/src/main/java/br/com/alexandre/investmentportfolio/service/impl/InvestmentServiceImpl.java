package br.com.alexandre.investmentportfolio.service.impl;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import br.com.alexandre.investmentportfolio.entity.Investment;
import br.com.alexandre.investmentportfolio.service.InvestmentService;

import static br.com.alexandre.investmentportfolio.enums.TableName.INVESTMENTS;

public class InvestmentServiceImpl implements InvestmentService {

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    public List<Investment> getAllInvestments() {
        List<Investment> investmentList = new ArrayList<>();
        reference.child(INVESTMENTS.name()).orderByKey()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(x -> {
                            Investment investment = x.getValue(Investment.class);
                            investment.setId(x.getKey());
                            investmentList.add(investment);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
        return investmentList;
    }

}
