package edu.csulb.mediease;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private static final String MY_PREFS = "MyPrefs";
    private static final String COUNT = "count";
    private static final String SUNDAY = "SUNDAY";
    private static final String MONDAY = "MONDAY";
    private static final String TUESDAY = "TUESDAY";
    private static final String WEDNESDAY = "WEDNESDAY";
    private static final String THURSDAY = "THURSDAY";
    private static final String FRIDAY = "FRIDAY";
    private static final String SATURDAY = "SATURDAY";
    private static final String DAYS[] = {"", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    private static int timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    private static int timeMinute = Calendar.getInstance().get(Calendar.MINUTE);
    private Activity activity;
    private List<Medicine> medicineList = new ArrayList<>();
    private AlarmManager alarmManager;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private List<String> times;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    MedicineAdapter(Activity activity, List<Medicine> medicineList) {
        this.activity = activity;
        this.medicineList = medicineList;
        alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(activity, AlarmReceiver.class);
        times = new ArrayList<>(Collections.nCopies(medicineList.size(), ""));
        preferences = activity.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicineViewHolder holder, int position) {
        holder.txtName.setText(medicineList.get(position).getName());
        holder.txtFreq.setText(medicineList.get(position).getFrequency());
        holder.txtTime.setText(medicineList.get(position).getTimes());
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    class MedicineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtName, txtFreq, txtTime;
        private ImageButton btnTime;
        private Button btnSetAlarm;
        private String setTime;

        MedicineViewHolder(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.textViewMedicineName);
            txtFreq = (TextView) itemView.findViewById(R.id.textViewFrequency);
            txtTime = (TextView) itemView.findViewById(R.id.textViewTime);
            btnTime = (ImageButton) itemView.findViewById(R.id.imageButtonTime);
            btnSetAlarm = (Button) itemView.findViewById(R.id.buttonSetAlarm);

            btnTime.setOnClickListener(this);
            btnSetAlarm.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();

            if (id == btnTime.getId()) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                final String[] time = new String[1];
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        int hour = selectedHour % 12;
                        time[0] = String.format("%02d:%02d %s", hour == 0 ? 12 : hour,
                                selectedMinute, selectedHour < 12 ? "am" : "pm");

                        txtTime.setText(time[0]);
                        setTime = time[0];
                        times.set(getAdapterPosition(), selectedHour + ":" + selectedMinute);
                        btnSetAlarm.setEnabled(true);
                    }
                }, hour, minute, false);//true =  24 hour view
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
            if (id == btnSetAlarm.getId()) {
                int adapterPosition = getAdapterPosition();

                MySQLiteHelper db = new MySQLiteHelper(activity);
                db.insertTime(getAdapterPosition() + 1, setTime);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, timeHour);
                calendar.set(Calendar.MINUTE, timeMinute);

                Medicine medicine = medicineList.get(adapterPosition);
                String frequency = medicine.getFrequency();
                ArrayList<Integer> days = new ArrayList<>();
                if (frequency.contains(SUNDAY))
                    days.add(1);
                if (frequency.contains(MONDAY))
                    days.add(2);
                if (frequency.contains(TUESDAY))
                    days.add(3);
                if (frequency.contains(WEDNESDAY))
                    days.add(4);
                if (frequency.contains(THURSDAY))
                    days.add(5);
                if (frequency.contains(FRIDAY))
                    days.add(6);
                if (frequency.contains(SATURDAY))
                    days.add(7);

                System.out.println("days = " + days);
                System.out.println("time = " + times.get(adapterPosition));

                for (int day : days) {
                    int count = 1;
                    editor = preferences.edit();
                    if (!preferences.contains(COUNT)) {
                        editor.putInt(COUNT, 1);
                    } else {
                        count = preferences.getInt(COUNT, -1);
                        editor.putInt(COUNT, ++count);
                    }
                    editor.apply();

                    String timeTemp[] = times.get(adapterPosition).split(":");
                    System.out.println("Current day = " + DAYS[day]);
                    int hour = Integer.parseInt(timeTemp[0]);
                    int minute = Integer.parseInt(timeTemp[1]);

                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(Calendar.HOUR_OF_DAY, hour);
                    calendar1.set(Calendar.MINUTE, minute);
                    calendar1.set(Calendar.SECOND, 0);
                    if (calendar1.get(Calendar.DAY_OF_WEEK) != day)
                        calendar1.set(Calendar.DAY_OF_WEEK, day);
                    System.out.println("current millis = " + Calendar.getInstance().getTimeInMillis());
                    System.out.println("millis with day = " + calendar1.getTimeInMillis());

                    long milliTime = calendar1.getTimeInMillis();
                    long timeDiff = milliTime - Calendar.getInstance().getTimeInMillis();
                    alarmIntent.putExtra("db_id", adapterPosition + 1); //if need to get id when triggered; starts from 0
                    alarmIntent.putExtra("count", count);
                    pendingIntent = PendingIntent.getBroadcast(activity, count, alarmIntent, 0);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, milliTime, pendingIntent);
                }

                Toast.makeText(activity, "Alarms set", Toast.LENGTH_SHORT).show();
                btnSetAlarm.setEnabled(false);
            }
        }
    }
}
