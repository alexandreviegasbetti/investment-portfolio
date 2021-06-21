package br.com.alexandre.investmentportfolio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import br.com.alexandre.investmentportfolio.R;
import br.com.alexandre.investmentportfolio.entity.Investment;

import static br.com.alexandre.investmentportfolio.config.FireBaseConfig.getFirebaseAuth;
import static br.com.alexandre.investmentportfolio.config.FireBaseConfig.getReferenceInvestment;
import static java.lang.Double.valueOf;
import static java.lang.String.valueOf;

public class FormEditItemActivity extends AppCompatActivity {

    private final DatabaseReference myRefInvest = getReferenceInvestment();

    private TextView code;
    private EditText quantity;
    private EditText value;
    private EditText date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit_form);

        code = findViewById(R.id.code);
        quantity = findViewById(R.id.quantity);
        value = findViewById(R.id.value);
        date = findViewById(R.id.date);
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> edit());

        uploadForm();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getResources().getString(R.string.logout));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.toString().equals(getResources().getString(R.string.logout))) {
            getFirebaseAuth().signOut();
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void edit() {
        if (ifFilled()) {
            Toast.makeText(FormEditItemActivity.this,
                    getResources().getString(R.string.fields_validation), Toast.LENGTH_LONG).show();
        } else {
            Integer newQuantity = Integer.valueOf(this.quantity.getText().toString());
            Double newValue = valueOf(this.value.getText().toString());
            DatabaseReference key = myRefInvest.child(getIntent().getStringExtra("id"));
                    key.child("purchaseDate").setValue(date.getText().toString());
                    key.child("quantity").setValue(newQuantity);
                    key.child("value").setValue(newValue);
                    key.child("totalValue").setValue(getTotalValue(newQuantity, newValue));
                    finish();
        }
    }

    private Double getTotalValue(Integer quantity, Double value) {
        return BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(value))
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private boolean ifFilled() {
        return quantity.getText().toString().isEmpty() || code.getText().toString().isEmpty() ||
                date.getText().toString().isEmpty() || value.getText().toString().isEmpty();
    }

    private void uploadForm() {
        Investment investment = getInvestment();
        if (Objects.nonNull(investment)) {
            code.setText(investment.getCode());
            date.setText(investment.getPurchaseDate());
            quantity.setText(valueOf(investment.getQuantity()));
            value.setText(valueOf(investment.getValue()));
        }
    }

    private Investment getInvestment() {
        return Investment.builder()
                .id(getIntent().getStringExtra("id"))
                .code(getIntent().getStringExtra("code"))
                .name(getIntent().getStringExtra("name"))
                .value(valueOf(getIntent().getStringExtra("value")))
                .totalValue(valueOf(getIntent().getStringExtra("totalValue")))
                .quantity(Integer.valueOf(getIntent().getStringExtra("quantity")))
                .purchaseDate(getIntent().getStringExtra("purchaseDate"))
                .build();
    }

}