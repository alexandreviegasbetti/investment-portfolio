package br.com.alexandre.investmentportfolio.entity;

import android.widget.LinearLayout;
import android.widget.TextView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SupportItem {

    TextView name;
    TextView code;
    TextView quantity;
    TextView value;
    TextView totalValue;
    TextView purchaseDate;
    LinearLayout layout;

}
