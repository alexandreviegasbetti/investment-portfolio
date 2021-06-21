package br.com.alexandre.investmentportfolio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.alexandre.investmentportfolio.R;
import br.com.alexandre.investmentportfolio.adapter.AdapterInvestmentPortfolio;
import br.com.alexandre.investmentportfolio.config.FireBaseConfig;
import br.com.alexandre.investmentportfolio.entity.Investment;

import static br.com.alexandre.investmentportfolio.config.FireBaseConfig.getReferenceInvestment;
import static java.util.Objects.requireNonNull;

public class ListItemsActivity extends AppCompatActivity {

    private final DatabaseReference myRefInvest = getReferenceInvestment();
    private final List<Investment> investmentList = new ArrayList<>();

    private AdapterInvestmentPortfolio adapter;
    private ValueEventListener valueEventListener;
    private ListView lvInvestment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> saveNewOnItemClick());

        lvInvestment = findViewById(R.id.lvInvestment);
        adapter = new AdapterInvestmentPortfolio(this, investmentList);
        lvInvestment.setAdapter(adapter);

        editOnItemClick();
        deleteOnItemClick();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getResources().getString(R.string.logout));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.toString().equals(getResources().getString(R.string.logout))) {
            FireBaseConfig.getFirebaseAuth().signOut();
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNewOnItemClick() {
        startActivity(new Intent(this, FormCreateItemActivity.class));
    }

    private void editOnItemClick() {
        lvInvestment.setOnItemClickListener((parent, view, position, id) -> {
            Investment selectedInvestment = investmentList.get(position);
            Intent intent = new Intent(ListItemsActivity.this, FormEditItemActivity.class);
            intent.putExtra("id", selectedInvestment.getId());
            intent.putExtra("value", selectedInvestment.getValue().toString());
            intent.putExtra("totalValue", selectedInvestment.getTotalValue().toString());
            intent.putExtra("code", selectedInvestment.getCode());
            intent.putExtra("name", selectedInvestment.getName());
            intent.putExtra("purchaseDate", selectedInvestment.getPurchaseDate());
            intent.putExtra("quantity", selectedInvestment.getQuantity().toString());
            startActivity(intent);
        });
    }

    private void deleteOnItemClick() {
        lvInvestment.setOnItemLongClickListener((parent, view, position, id) -> {
            final Investment selectedInvestment = investmentList.get(position);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setIcon(android.R.drawable.ic_input_delete);
            alert.setTitle(getResources().getString(R.string.attention));
            alert.setMessage(getResources().getString(R.string.exclusion_of_the_investment)
                    + selectedInvestment.getCode()
                    + getResources().getString(R.string.question_mark));
            alert.setNeutralButton(R.string.cancel, null);
            alert.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                myRefInvest.child(selectedInvestment.getId()).removeValue();
                investmentList.remove(position);
                adapter.notifyDataSetChanged();
                myRefInvest.removeEventListener(valueEventListener);
            });
            alert.show();
            return true;
        });
    }

    private void listAllSelectedInvestments() {
        valueEventListener = myRefInvest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Investment investment = s.getValue(Investment.class);
                    requireNonNull(investment).setId(s.getKey());
                    investmentList.add(investment);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListItemsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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



