package com.ufu.bomfim.aluno;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.ufu.bomfim.aluno.adapter.AlunosAdapter;
import com.ufu.bomfim.aluno.alarm.AlarmReceiver;
import com.ufu.bomfim.aluno.helper.AlunoDbHelper;
import com.ufu.bomfim.aluno.model.Aluno;

import java.util.List;

import static android.R.id.message;
import static com.ufu.bomfim.aluno.R.id.fab;

public class MainActivity extends AppCompatActivity {

    private static String ALARM_APP = "ALARM_APP";
    private static String APP_ALUNOS_PREFS = "APP_ALUNOS_PREFS";

    private Holder holder;
    private AlunosAdapter alunosAdapter;
    private List<Aluno> alunosList;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        AlunoDbHelper.init(this);
        initView();
    }
    /*
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Aluno aluno = (Aluno) alunosAdapter.getItem(i);

            if(aluno != null) {

                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra("ID", aluno.getId());
                startActivity(intent);
            }
        }
    };
    */

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            android.app.AlertDialog.Builder dialog;
            final int idAluno = ((Aluno) alunosAdapter.getItem(position)).getId();

            final CharSequence[] dialogitem = {"Ligar","Enviar SMS","Achar no mapa","Navegar no site","Enviar e-mail","Alterar Aluno", "Excluir"};
            dialog = new android.app.AlertDialog.Builder(MainActivity.this);
            dialog.setCancelable(true);
            dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0 :
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:0377778888"));
                            if (callIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(callIntent);
                            }
                            break;
                        case 1:
                            Uri smsUri = Uri.parse("tel: " + "0377778888");
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW, smsUri);
                            smsIntent.putExtra("address", "Street 9");
                            smsIntent.putExtra("sms_body", message);
                            smsIntent.setType("vnd.android-dir/mms-sms");//here setType will set the previous data null.
                            if (smsIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(smsIntent);
                            }
                            break;
                        case 2:
                            Intent mapsIntent = new Intent();
                            mapsIntent.setAction(Intent.ACTION_VIEW);
                            String data = "geo:0,0?q=" + "1st%20%26%20Pike%2C%20Seattle";

                            mapsIntent.setData(Uri.parse(data));
                            if (mapsIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(mapsIntent);
                            }
                            break;
                        case 3:
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                            if (browserIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(browserIntent);
                            }
                            break;
                        case 4:
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("plain/text");
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "some@email.address" });
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "mail body");
                            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(Intent.createChooser(emailIntent, ""));
                            }
                            break;
                        case 5:
                            Intent alterIntent = new Intent(MainActivity.this, AddEditActivity.class);
                            alterIntent.putExtra("ID", idAluno);
                            startActivity(alterIntent);
                            break;
                        case 6:
                            AlunoDbHelper.getInstance().deleteById(idAluno);
                            alunosAdapter.refreshAdapter();
                            break;
                    }
                }
            }).show();


            return false;
        }
    };

    private void initView() {
        holder = new Holder(this);

        alunosList = AlunoDbHelper.getInstance().getAllAlunos();
        alunosAdapter = new AlunosAdapter(this, alunosList);
        holder.alunosListView.setAdapter(alunosAdapter);

        //holder.alunosListView.setOnItemClickListener(onItemClickListener);
        holder.alunosListView.setOnItemLongClickListener(onItemLongClickListener);

        holder.addAlunoFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivity(intent);
            }
        });

        if(getAlarmePrefs()) {
            holder.alarmSwitch.setChecked(true);
        }
        holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    startAlarm();
                } else {
                    cancelAlarm();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        alunosAdapter.refreshAdapter();
    }

    public void startAlarm() {
        Log.e("ALARM", "START");
        saveAlarmePrefs(true);

        int interval = 1000 * 60 * 1;
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        /* Repeating on every 1 minutes interval */
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "ON", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm() {
        Log.e("ALARM", "CANCEL");
        saveAlarmePrefs(false);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show();
    }

    private synchronized void saveAlarmePrefs(boolean isChecked) {
        SharedPreferences.Editor editor = getSharedPreferences("AppAlunoPrefs", MODE_PRIVATE).edit();
        editor.putBoolean(ALARM_APP, isChecked);
        editor.apply();
    }

    private synchronized boolean getAlarmePrefs() {
        SharedPreferences prefs = getSharedPreferences(APP_ALUNOS_PREFS, MODE_PRIVATE);
        return prefs.getBoolean(ALARM_APP, false);
    }

    private class Holder {

        private ListView alunosListView;
        private FloatingActionButton addAlunoFloatingActionButton;
        private SwitchCompat alarmSwitch;

        private Holder(Activity activity) {
            alunosListView = (ListView) activity.findViewById(R.id.list_view);
            addAlunoFloatingActionButton = (FloatingActionButton) activity.findViewById(fab);
            alarmSwitch = (SwitchCompat) activity.findViewById(R.id.alarm);
        }
    }

}
