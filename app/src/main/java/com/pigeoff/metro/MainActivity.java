package com.pigeoff.metro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pigeoff.metro.data.Station;
import com.pigeoff.metro.utils.ReadFile;
import com.pigeoff.metro.utils.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Station> network;
    String from = "Mairie de Saint-Ouen";
    String to = "Rue du bac";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editFrom = findViewById(R.id.editTextFrom);
        EditText editTo = findViewById(R.id.editTextTo);
        Button submit = findViewById(R.id.buttonSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                from = editFrom.getText().toString();
                to = editTo.getText().toString();
                performSearch();
            }
        });

    }

    private void performSearch() {
        Intent intent = new Intent(this, RouteActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("to", to);
        startActivity(intent);
    }

}