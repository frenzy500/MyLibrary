package com.example.mylibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    EditText etBook, etAuthor, etPages, etRead;
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
        etRead = findViewById(R.id.etRead);
        tvStatus = findViewById(R.id.tvStatus);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnLoad = findViewById(R.id.btnLoad);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnLogout = findViewById(R.id.btnLogout);

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
                if (etRead.getText().toString().trim().isEmpty()) {
                    etRead.setError("יש למלא עמודים שקראתי");
                    return;
                }
                int pages = Integer.parseInt(etPages.getText().toString().trim());
                int pagesRead = Integer.parseInt(etRead.getText().toString().trim());
                if (pages <= 0) {
                    etPages.setError("מספר עמודים חייב להיות גדול מ-0");
                    return;
                }
                if (pagesRead < 0 || pagesRead > pages) {
                    etRead.setError("מספר בין 0 ל-" + pages);
                    return;
                }
                Book book = new Book(
                        etBook.getText().toString(),
                        etAuthor.getText().toString(),
                        pages,
                        pagesRead
                );

                tvStatus.setText("שומר...");

                db.collection("books").document("current").set(book)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                int percent = (pagesRead * 100) / pages;
                                tvStatus.setText("קראת " + pagesRead + " מתוך " + pages + " עמודים (" + percent + "%)");
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
                loadBook();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("מחיקת ספר")
                        .setMessage("האם למחוק את הספר מהענן?")
                        .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvStatus.setText("מוחק...");
                                db.collection("books").document("current").delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                etBook.setText("");
                                                etAuthor.setText("");
                                                etPages.setText("");
                                                etRead.setText("");
                                                tvStatus.setText("הספר נמחק מהענן");
                                                Toast.makeText(MainActivity.this, "הספר נמחק", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                tvStatus.setText("המחיקה נכשלה: " + e.getMessage());
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("לא", null)
                        .show();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        loadBook();
    }
    void loadBook() {
        tvStatus.setText("טוען...");
        db.collection("books").document("current").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override

                    public void onSuccess(DocumentSnapshot doc) {
                        if (!doc.exists()) {
                            tvStatus.setText("עוד אין ספר שמור");
                            return;
                        }
                        Book book = doc.toObject(Book.class);
                        if (book != null) {
                            etBook.setText(book.title);
                            etAuthor.setText(book.author);
                            etPages.setText(String.valueOf(book.pages));
                            etRead.setText(String.valueOf(book.pagesRead));
                            if (book.pages > 0) {
                                int percent = (book.pagesRead * 100) / book.pages;
                                tvStatus.setText("קראת " + book.pagesRead + " מתוך " + book.pages + " עמודים (" + percent + "%)");
                            } else {
                                tvStatus.setText("נטען מהענן");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tvStatus.setText("הטעינה נכשלה: " + e.getMessage());
                    }
                });
    }
}