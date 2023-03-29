package com.example.myskin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecordsPage extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    String userid;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<PredictionDetails> list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_page);
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView=findViewById(R.id.recyclerView1);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        userid=user.getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Predictions");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myAdapter = new MyAdapter(this, list);
        recyclerView.setAdapter(myAdapter);


        Query query = myRef.orderByChild("uid").equalTo(userid);
        if(user==null){
            Intent intent=new Intent(getApplicationContext(),LoginPage.class);
            startActivity(intent);
        }
        else{

        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                PredictionDetails predictionsDetails = dataSnapshot.getValue(PredictionDetails.class);
                Log.d("Firebase", dataSnapshot.toString());
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    System.out.println(childSnapshot.toString());
                    PredictionDetails pred=childSnapshot.getValue(PredictionDetails.class);
                    System.out.println(pred.toString());
                    list.add(pred);
                    Log.d("Firebase", childSnapshot.toString());
//                    Object value=childSnapshot.getValue();
//                    valueArray.add(value.toString());
                }
                myAdapter.notifyDataSetChanged();
//                System.out.println(valueArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem logoutItem = menu.findItem(R.id.logout);
        logoutItem.setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        MenuItem infoItem = menu.findItem(R.id.info);
        infoItem.setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(getApplicationContext(),LoginPage.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.info) {
            Intent intent=new Intent(getApplicationContext(), AboutPage.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}