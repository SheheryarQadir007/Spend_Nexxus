package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Income {
    private String amount;
    private String incomeId;
    private String source;
    private String userId;

    public Income() {
        // Default constructor required for Firebase
    }

    public Income(String amount, String incomeId, String source, String userId) {
        this.amount = amount;
        this.incomeId = incomeId;
        this.source = source;
        this.userId = userId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(String incomeId) {
        this.incomeId = incomeId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static class IncomeAdapter extends ArrayAdapter<Income> {
        public IncomeAdapter(Context context, int resourceId, List<Income> incomes) {
            super(context, resourceId, incomes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.income_list_item, null);
            }

            Income income = getItem(position);

            TextView amountTextView = view.findViewById(R.id.amountTextView);
            amountTextView.setText(income.getAmount());

            TextView sourceTextView = view.findViewById(R.id.sourceTextView);
            sourceTextView.setText(income.getSource());

            return view;
        }
    }
}