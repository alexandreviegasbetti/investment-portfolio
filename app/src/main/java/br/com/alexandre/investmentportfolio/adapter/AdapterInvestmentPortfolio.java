package br.com.alexandre.investmentportfolio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import br.com.alexandre.investmentportfolio.R;
import br.com.alexandre.investmentportfolio.entity.Investment;
import br.com.alexandre.investmentportfolio.entity.SupportItem;

import static java.util.Objects.isNull;

public class AdapterInvestmentPortfolio extends BaseAdapter {

    private final List<Investment> investmentList;
    private final LayoutInflater inflater;

    public AdapterInvestmentPortfolio(Context context, List<Investment> investmentList) {
        this.investmentList = investmentList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return investmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return investmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SupportItem item;

        if (isNull(convertView)) {
            convertView = inflater.inflate(R.layout.layout_investment_list, null);

            item = SupportItem.builder()
                    .name(convertView.findViewById(R.id.nameList))
                    .code(convertView.findViewById(R.id.codeList))
                    .quantity(convertView.findViewById(R.id.quantityList))
                    .value(convertView.findViewById(R.id.valueList))
                    .totalValue(convertView.findViewById(R.id.totalValueList))
                    .purchaseDate(convertView.findViewById(R.id.purchaseDateList))
                    .layout(convertView.findViewById(R.id.listBackground))
                    .build();

            convertView.setTag(item);
        } else {
            item = (SupportItem) convertView.getTag();
        }

        Investment investment = investmentList.get(position);
        item.getName().setText(investment.getName());
        item.getCode().setText(investment.getCode());
        item.getQuantity().setText(String.valueOf(investment.getQuantity()));
        item.getValue().setText(String.valueOf(investment.getValue()));
        item.getTotalValue().setText(String.valueOf(investment.getTotalValue()));
        item.getPurchaseDate().setText(investment.getPurchaseDate());

        return convertView;
    }
}
