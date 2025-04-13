package com.example.cinebook.resetpassword;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cinebook.R;

public class ResetPasswordTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_2_reset_password);

        // Initialize views
        ImageView imageViewLogo = findViewById(R.id.imageView_logo);
        ImageView imageViewDivider = findViewById(R.id.imageView_divider);
        TextView textCheckEmail = findViewById(R.id.text_check_email);
        TextView textEmailInstruction = findViewById(R.id.text_email_instruction);
        TextView textResendInstruction = findViewById(R.id.text_resend_instruction);

        // Load images using Glide
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/tdbijxyp_expires_30_days.png")
                .into(imageViewLogo);

        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/gdzsjy5o_expires_30_days.png")
                .into(imageViewDivider);

        // Optional: Display the email address
        String email = getIntent().getStringExtra("EMAIL");
        if (email != null && !email.isEmpty()) {
            textEmailInstruction.setText("A password reset link has been sent to " + email + ". Please check your inbox and spam folder.");
        } else {
            textEmailInstruction.setText("A password reset link has been sent to your email. Please check your inbox and spam folder.");
        }
    }
}