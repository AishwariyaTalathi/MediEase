package edu.csulb.mediease;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

public class AlarmsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicineAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new MedicineAdapter(this, getData());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        onNewIntent(getIntent());

        MySQLiteHelper db = new MySQLiteHelper(this);
        if (db.getCount() == 0) {
            Toast.makeText(this, "No prescriptions found", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }

        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(AlarmsActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null)
            if (bundle.containsKey("count")) {
                Intent stopIntent = new Intent(this, RingtonePlayingService.class);
                stopService(stopIntent);
                /*int count = bundle.getInt("count", -1);
                Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                alarmIntent.putExtra("db_id", -1); //if need to get id when triggered; starts from 0
                alarmIntent.putExtra("count", -1);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, count, alarmIntent, 0);
                alarmManager.cancel(pendingIntent);*/
            }
    }

    public List<Medicine> getData() {
        MySQLiteHelper db = new MySQLiteHelper(AlarmsActivity.this);
        return db.getData();
    }

}
