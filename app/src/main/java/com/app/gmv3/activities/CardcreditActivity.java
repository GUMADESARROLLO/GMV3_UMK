package com.app.gmv3.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.gmv3.R;
import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;

public class CardcreditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardcredit);
        CardForm cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {
                //Your code here!! use card.getXXX() for get any card property
                //for instance card.getName();
            }
        });
    }
}
