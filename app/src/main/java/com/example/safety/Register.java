package com.example.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Register extends AppCompatActivity {
Button b1,b2,b3;
EditText e1;
ListView listView;
SQLiteOpenHelper s1;
SQLiteDatabase s2;
DataBaseHandler myDB;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e1 = findViewById( R.id.phone);
        b1 = findViewById(R.id.add);
    b2 = findViewById(R.id.delete);
    b3 = findViewById(R.id.view);
    listView = findViewById(R.id.list);
    myDB = new DataBaseHandler(this);
    b1.setOnClickListener(v -> {
        String sr = e1.getText().toString();
        addData(sr);
        Toast.makeText(Register.this, "Data added", Toast.LENGTH_SHORT).show();
        e1.setText("");
    });
    b2.setOnClickListener(v -> {
        s2 = myDB.getWritableDatabase();
        String x = e1.getText().toString();
        DeleteData(x);
        Toast.makeText(Register.this, "Data Deleted", Toast.LENGTH_SHORT).show();
    });
    b3.setOnClickListener(v -> loadData()
    );
    }


    private void loadData() {
        ArrayList<String> theList = new ArrayList<>();
        Cursor data = myDB.getListContents();
        if (data.getCount()==0){
            Toast.makeText(Register.this, "There is no content", Toast.LENGTH_SHORT).show();
        }
        else{
            while(data.moveToNext()){
                theList.add(data.getString(1));
                ListAdapter listAdapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theList);
                listView.setAdapter(listAdapter);
            }
        }
}
    private void DeleteData(String x) {
        s2.delete(DataBaseHandler.TABLE_NAME, DataBaseHandler.COL2 + "=?", new String[]{x});
    }

    private void addData(String newEntry) {
    boolean insertData = myDB.addData(newEntry);
    if (insertData){
        Toast.makeText(Register.this, "Data added  ", Toast.LENGTH_SHORT).show();
    }
    else{
        Toast.makeText(Register.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
    }
    }
}

