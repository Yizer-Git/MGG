package com.just.cn.mgg.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.just.cn.mgg.R;
import com.just.cn.mgg.utils.ToastUtils;

/**
 * 帮助与反馈
 */
public class FeedbackActivity extends AppCompatActivity {

    private TextInputEditText etFeedback;
    private TextInputEditText etContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initToolbar();
        initViews();
    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        etFeedback = findViewById(R.id.etFeedback);
        etContact = findViewById(R.id.etContact);

        MaterialCardView cardFaq1 = findViewById(R.id.cardFaq1);
        MaterialCardView cardFaq2 = findViewById(R.id.cardFaq2);
        MaterialCardView cardFaq3 = findViewById(R.id.cardFaq3);

        TextView answer1 = findViewById(R.id.tvFaqAnswer1);
        TextView answer2 = findViewById(R.id.tvFaqAnswer2);
        TextView answer3 = findViewById(R.id.tvFaqAnswer3);

        View.OnClickListener faqListener = v -> {
            if (v == cardFaq1) {
                toggleAnswer(answer1);
            } else if (v == cardFaq2) {
                toggleAnswer(answer2);
            } else if (v == cardFaq3) {
                toggleAnswer(answer3);
            }
        };

        cardFaq1.setOnClickListener(faqListener);
        cardFaq2.setOnClickListener(faqListener);
        cardFaq3.setOnClickListener(faqListener);

        MaterialButton btnSubmit = findViewById(R.id.btnSubmitFeedback);
        btnSubmit.setOnClickListener(v -> submitFeedback());
    }

    private void toggleAnswer(TextView answerView) {
        if (answerView.getVisibility() == View.VISIBLE) {
            answerView.setVisibility(View.GONE);
        } else {
            answerView.setVisibility(View.VISIBLE);
        }
    }

    private void submitFeedback() {
        String feedback = etFeedback.getText() != null ? etFeedback.getText().toString().trim() : "";
        if (feedback.isEmpty()) {
            etFeedback.setError(getString(R.string.feedback_input_hint));
            return;
        }

        // 联系方式可选
        if (etContact.getText() != null && TextUtils.isEmpty(etContact.getText().toString().trim())) {
            etContact.setError(null);
        }

        ToastUtils.show(this, getString(R.string.feedback_submit_success));
        etFeedback.setText("");
        if (etContact.getText() != null) {
            etContact.setText("");
        }
    }
}
