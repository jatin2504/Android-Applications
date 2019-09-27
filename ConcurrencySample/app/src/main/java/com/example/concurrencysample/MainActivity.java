package com.example.concurrencysample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Updating Progress");
//        progressDialog.setMax(100);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setCancelable(false);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                switch (message.what) {
                    case DoWork.STATUS_START:
                        progressDialog.setProgress(0);
                        progressDialog.show();
                        Log.d("demo", "Starting.....");
                        break;
                    case DoWork.STATUS_STOP:
                        progressDialog.dismiss();
                        Log.d("demo", "Stopped.....");
                        break;
                    case DoWork.STATUS_PROGRESS:
                        progressDialog.setProgress(message.getData().getInt(DoWork.PROGRESS));
                        Log.d("demo", "Progressing....." + message.getData().getInt(DoWork.PROGRESS));
                        break;
                }
                return false;
            }
        });

       // new Thread(new DoWork()).start();
        new DoWorkAsyncTask().execute(100000);

    }

    class DoWorkAsyncTask extends AsyncTask<Integer, Integer, Double> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Updating Progress");
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Double aDouble) {
            super.onPostExecute(aDouble);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected Double doInBackground(Integer... values) {
            double average = 0;
            int count = 0;
            Random random = new Random();
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < values[0]; j++) {
                    count++;
                    average = random.nextDouble() + average;
                }
                publishProgress(i);
            }
            return average/count;
        }
    }

    class DoWork implements Runnable {
        static final int STATUS_START = 0x00;
        static final int STATUS_PROGRESS = 0x01;
        static final int STATUS_STOP = 0x02;
        static final String PROGRESS = "PROGRESS";


        @Override
        public void run() {

            Message startMsg = new Message();
            startMsg.what = STATUS_START;
            handler.sendMessage(startMsg);

            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 10000000; j++) {
                }
                Message progressMsg = new Message();
                progressMsg.what = STATUS_PROGRESS;
                Bundle bundle = new Bundle();
                bundle.putInt(PROGRESS, i);
                progressMsg.setData(bundle);
                handler.sendMessage(progressMsg);
            }
            Message stopMsg = new Message();
            stopMsg.what = STATUS_STOP;
            handler.sendMessage(stopMsg);
        }
    }
}
