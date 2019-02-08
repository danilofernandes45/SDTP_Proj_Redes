package br.ufal.tsensor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private int numEnvironments = 3;
    TcpClient[] mTcpClient = new TcpClient[numEnvironments];
    private TextView[] result = new TextView[numEnvironments];
    private TextView date;
    private TextView inf;

    private Button btBrightness;
    private Button btTemperature;
    private Button btHumidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result[0] = (TextView) findViewById(R.id.result1);
        result[1] = (TextView) findViewById(R.id.result2);
        result[2] = (TextView) findViewById(R.id.result3);
        date = (TextView) findViewById(R.id.date);
        inf = (TextView) findViewById(R.id.inf);

        btBrightness = (Button)findViewById(R.id.brightness);
        btTemperature = (Button)findViewById(R.id.temperature);
        btHumidity = (Button)findViewById(R.id.humidity);

        btBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBrightness();
            }
        });

        btTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTemperature();
            }
        });

        btHumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHumidity();
            }
        });

    }

    private void getBrightness(){
        inf.setText("Brightness");
        for( int i=1; i<=numEnvironments; i++){

            new ConnectTask("GET:brightness:SDTP/0.9", i).execute("");

        }
    }

    private void getTemperature(){
        inf.setText("Temperature");
        for(int i = 1; i <= numEnvironments; i++) {

            new ConnectTask("GET:temperature:SDTP/0.9", i).execute("");
        }
    }

    private void getHumidity(){
        inf.setText("Humidity");
        for(int i = 1; i <= numEnvironments; i++) {

            new ConnectTask("GET:humidity:SDTP/0.9", i).execute("");
        }
    }


    public class ConnectTask extends AsyncTask<String, String, TcpClient> {

        private String msg;
        private int env;
        private TcpClient tcpClient;
        public ConnectTask(String msg, int env){
            this.msg = msg;
            this.env = env;
        }

        @Override
        protected TcpClient doInBackground(String... message) {

            //we create a TCPClient object
            tcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            tcpClient.run(msg, env);

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //response received from server
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            date.setText( dateFormat.format( new Date() ) );

            String response = values[0].split(":")[0].split(" ")[1];
            String status_code = values[0].split(":")[0].split(" ")[0];

            if(status_code.equals("100")){
                response = values[0].split(":")[1];
            }


            String output = "Enviroment " + env + " : " + response;
            result[env - 1].setText(output);

            //process server response here....
            tcpClient.stopClient();

        }
    }

}

