package com.example.mylibrary;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText etBook = findViewById(R.id.etBook);
        EditText etAuthor = findViewById(R.id.etAuthor);
        EditText etPages = findViewById(R.id.etPages);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnLoad = findViewById(R.id.btnLoad);

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (etBook.getText().toString().trim().isEmpty()) {
                    etBook.setError("יש למלא שם ספר");
                    return;
                }
                if (etAuthor.getText().toString().trim().isEmpty()) {
                    etAuthor.setError("יש למלא שם סופר");
                    return;
                }
                if (etPages.getText().toString().trim().isEmpty()) {
                    etPages.setError("יש למלא מספר עמודים");
                    return;
                }
                Toast.makeText(MainActivity.this,"הנתונים תקינים", Toast.LENGTH_LONG).show();
            }

        });
    }
}