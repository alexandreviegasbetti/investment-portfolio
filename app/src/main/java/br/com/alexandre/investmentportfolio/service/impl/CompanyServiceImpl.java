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

import br.com.alexandre.investmentportfolio.entity.Company;
import br.com.alexandre.investmentportfolio.service.CompanyService;

import static br.com.alexandre.investmentportfolio.enums.TableName.COMPANIES;

public class CompanyServiceImpl implements CompanyService {

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    public List<Company> getAllCompanies() {
        List<Company> companyList = new ArrayList<>();
        reference.child(COMPANIES.name()).orderByKey()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(x -> {
                            Company company = x.getValue(Company.class);
                            company.setId(x.getKey());
                            companyList.add(company);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
        return companyList;
    }

}
