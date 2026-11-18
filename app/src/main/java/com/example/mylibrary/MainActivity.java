package com.example.mylibrary;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    EditText etBook, etAuthor, etPages;
    TextView tvStatus;
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
        db = FirebaseFirestore.getInstance();
        etBook = findViewById(R.id.etBook);
        etAuthor = findViewById(R.id.etAuthor);
        etPages = findViewById(R.id.etPages);
        tvStatus = findViewById(R.id.tvStatus);
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
                Map<String, Object> book = new HashMap<>();
                book.put("title", etBook.getText().toString());
                book.put("author", etAuthor.getText().toString());
                book.put("pages", Integer.parseInt(etPages.getText().toString()));

                tvStatus.setText("שומר...");

                db.collection("books").document("current").set(book)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                tvStatus.setText("נשמר בענן");
                                Toast.makeText(MainActivity.this, "הספר נשמר", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                tvStatus.setText("השמירה נכשלה: " + e.getMessage());
                            }
                        });
            }

        });
        btnLoad.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tvStatus.setText("טוען...");
                db.collection("books").document("current").get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override

                            public void onSuccess(DocumentSnapshot doc) {
                                if (!doc.exists()) {
                                    tvStatus.setText("עוד אין ספר שמור");
                                    return;
                                }
                                etBook.setText(doc.getString("title"));
                                etAuthor.setText(doc.getString("author"));
                                Long pages = doc.getLong("pages");
                                if (pages != null) {
                                    etPages.setText(String.valueOf(pages));
                                } else {
                                    etPages.setText("");
                                }
                                tvStatus.setText("נטען מהענן");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                tvStatus.setText("הטעינה נכשלה: " + e.getMessage());
                            }
                        });
            }
        });
    }
}