package com.example.user.dusttest;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements GetDustJson {

    TextView text, result;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cbsradio://rainbow"));
                startActivity(intent);
            }
        });
        text = findViewById(R.id.result);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hm.. it is slow.. why???s
            }
        });
        result = findViewById(R.id.result);
        ApiDust.getInstance().getDustInfo(MainActivity.this, MainActivity.this);

        //this is github test
    }

    @Override
    public void getDust(String parsing) {
        result.setText(parsing);
    }
}
