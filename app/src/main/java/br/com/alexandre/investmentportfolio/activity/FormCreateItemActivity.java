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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import br.com.alexandre.investmentportfolio.R;
import br.com.alexandre.investmentportfolio.config.FireBaseConfig;
import br.com.alexandre.investmentportfolio.entity.Company;
import br.com.alexandre.investmentportfolio.entity.Investment;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;
import static br.com.alexandre.investmentportfolio.config.FireBaseConfig.getReferenceCompany;
import static br.com.alexandre.investmentportfolio.config.FireBaseConfig.getReferenceInvestment;

public class FormCreateItemActivity extends AppCompatActivity {

    private final DatabaseReference myRefCompany = getReferenceCompany();
    private final DatabaseReference myRefInvest = getReferenceInvestment();
    private final List<Company> companyList = new ArrayList<>();
    private final List<String> codeList = new ArrayList<>();

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
                finish();
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
            Double val = Double.valueOf(this.value.getText().toString());
            String code = spinner.getSelectedItem().toString();

            Investment investment = Investment
                    .builder()
                    .code(code)
                    .purchaseDate(date.getText().toString())
                    .quantity(qtd)
                    .value(val)
                    .totalValue(getTotalValue(qtd, val))
                    .name(getCompanyName(code))
                    .build();

            myRefInvest.push().setValue(investment);
            Intent intent = new Intent(this, ListItemsActivity.class);
            startActivity(intent);
        }
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
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public void getSpinnerValues() {
        myRefCompany.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(co -> {
                    codeList.add(co.child("code").getValue(String.class));
                    Company company = co.getValue(Company.class);
                    companyList.add(company);
                });
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

}