package br.com.alexandre.investmentportfolio.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import br.com.alexandre.investmentportfolio.R;
import br.com.alexandre.investmentportfolio.config.FireBaseConfig;
import br.com.alexandre.investmentportfolio.entity.Company;
import br.com.alexandre.investmentportfolio.entity.Investment;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;
import static br.com.alexandre.investmentportfolio.config.FireBaseConfig.getReferenceCompany;
import static br.com.alexandre.investmentportfolio.config.FireBaseConfig.getReferenceInvestment;
import static java.lang.Double.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Objects.requireNonNull;

public class FormCreateItemActivity extends AppCompatActivity {

    private final DatabaseReference myRefCompany = getReferenceCompany();
    private final DatabaseReference myRefInvest = getReferenceInvestment();
    private final List<Company> companyList = new ArrayList<>();
    private final List<Investment> investmentList = new ArrayList<>();
    private List<String> codeList;
    private ValueEventListener valueEventListener;
    private Investment investmentByCode;

    private Spinner spinner;
    private EditText quantity;
    private EditText value;
    private EditText date;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_create_form);

        Button btnSave = findViewById(R.id.btnSave);
        quantity = findViewById(R.id.quantity);
        value = findViewById(R.id.value);
        date = findViewById(R.id.date);
        spinner = findViewById(R.id.code);
        getSpinnerValues();
        btnSave.setOnClickListener(v -> save());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getResources().getString(R.string.new_code));
        menu.add(getResources().getString(R.string.logout));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.toString().equals(getResources().getString(R.string.new_code))) {
            addNewCompany();
        }
        if (item.toString().equals(getResources().getString(R.string.logout))) {
            FireBaseConfig.getFirebaseAuth().signOut();
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private Investment findByCode(String code) {
        return investmentList.stream()
                .filter(x -> Objects.equals(x.getCode(), code))
                .findFirst()
                .orElse(null);
    }

    private void addNewCompany() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_create_company);

        EditText codeDialog = dialog.findViewById(R.id.codeDialog);
        EditText nameDialog = dialog.findViewById(R.id.nameDialog);
        Button btnSave = dialog.findViewById(R.id.btnSaveDialog);
        Button btnCancel = dialog.findViewById(R.id.btnCancelDialog);

        btnCancel.setOnClickListener(x -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String code = codeDialog.getText().toString();
            String name = nameDialog.getText().toString();
            Company company = Company
                    .builder()
                    .code(code.toUpperCase())
                    .name(name.toUpperCase())
                    .build();
            if (code.isEmpty()) {
                Toast.makeText(this,
                        getResources().getString(R.string.code_not_null), Toast.LENGTH_LONG).show();
            } else if (name.isEmpty()) {
                Toast.makeText(this,
                        getResources().getString(R.string.name_not_null), Toast.LENGTH_LONG).show();
            } else {
                myRefCompany.push().setValue(company);
                getSpinnerValues();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    private void save() {
        if (ifFilled()) {
            Toast.makeText(FormCreateItemActivity.this, getResources().getString(R.string.fields_validation),
                    Toast.LENGTH_LONG).show();
        } else {
            Integer qtd = Integer.valueOf(this.quantity.getText().toString());
            Double val = valueOf(this.value.getText().toString());
            String code = spinner.getSelectedItem().toString();
            Double totalValue = getTotalValue(qtd, val);

            investmentByCode = findByCode(code);
            if (Objects.nonNull(investmentByCode)) {
                Investment newInvestmentAveragePrice = newInvestmentAveragePrice(qtd, code, totalValue);
                myRefInvest.child(investmentByCode.getId()).setValue(newInvestmentAveragePrice);
            } else {
                Investment investment = investmentBuilder(qtd, val, code, totalValue);
                myRefInvest.push().setValue(investment);
            }

            Intent intent = new Intent(this, ListItemsActivity.class);
            startActivity(intent);
        }
    }

    private Investment investmentBuilder(Integer qtd, Double val, String code, Double totalValue) {
        return Investment.builder()
                .code(code)
                .purchaseDate(date.getText().toString())
                .quantity(qtd)
                .value(val)
                .totalValue(totalValue)
                .name(getCompanyName(code))
                .build();
    }

    private Investment newInvestmentAveragePrice(Integer qtd, String code, Double totalValue) {
        BigDecimal newQuantity = new BigDecimal(investmentByCode.getQuantity()).add(BigDecimal.valueOf(qtd));
        BigDecimal newTotalValue = BigDecimal.valueOf(investmentByCode.getTotalValue())
                .add(BigDecimal.valueOf(totalValue)).setScale(2, HALF_UP);
        BigDecimal newValue = newTotalValue.divide(newQuantity, 2, HALF_UP);
        return investmentBuilder(newQuantity.intValue(), newValue.doubleValue(), code, newTotalValue.doubleValue());
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSpinnerValues();
    }

    private boolean ifFilled() {
        return quantity.getText().toString().isEmpty() || spinner.getSelectedItem().toString().isEmpty() ||
                date.getText().toString().isEmpty() || value.getText().toString().isEmpty();
    }


    private Double getTotalValue(Integer quantity, Double value) {
        return BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(value))
                .setScale(2, HALF_UP).doubleValue();
    }

    public void getSpinnerValues() {
        codeList = new ArrayList<>();
        myRefCompany.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(co -> {
                    codeList.add(co.child("code").getValue(String.class));
                    Company company = co.getValue(Company.class);
                    companyList.add(company);
                });
                codeList.sort(Comparator.comparing(String::valueOf));
                arrayAdapter = new ArrayAdapter<>(FormCreateItemActivity.this, simple_spinner_item, codeList);
                arrayAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FormCreateItemActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getCompanyName(String code) {
        return companyList
                .stream()
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElse(new Company())
                .getName();
    }

    private void listAllSelectedInvestments() {
        valueEventListener = myRefInvest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Investment investment = s.getValue(Investment.class);
                    requireNonNull(investment).setId(s.getKey());
                    investmentList.add(investment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FormCreateItemActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        investmentList.clear();
        listAllSelectedInvestments();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myRefInvest.removeEventListener(valueEventListener);
    }

}