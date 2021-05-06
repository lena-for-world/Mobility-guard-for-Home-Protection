public class MainActivity extends AppCompatActivity {
    private MqttAndroidClient client;
    TabLayout tabs;
    private String mJsonString;
    private static String IP_ADDRESS = 'localhost';
    private static final String TAG = "MainActivity";
    float[] valueArray;
    int tmpValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String clientId = MqttClient.generateClientId();

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CO";
            String description = "CO alert";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CO", name, importance);
            channel.setDescription(description);
            final NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CO")
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle("CO ALERT")
                .setContentText("CO warning")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        final Notification noti = builder.build();

        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://18.224.71.242",
                clientId);
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            IMqttToken token = client.connect(options);
            testbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String topic = "TURNOFF_WARNING";
                    MqttMessage message = new MqttMessage();
                    message.setPayload("TURNOFF_WARNING".getBytes());
                    try {
                        client.publish(topic, message);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            });
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) { 
                    Log.d(TAG, "onSuccess");
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) { 
                    Log.d("Connect_success", "onSuccess");
                    try {
                        client.subscribe("CO", 0);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "nothing comes from MQ7");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        final Fragment2 fragment2 = new Fragment2();

        final Fragment3 fragment3 = new Fragment3();

        fragment4 = new Fragment4();

        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment2).commit();
        tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("집 관리"));
        tabs.addTab(tabs.newTab().setText("침입감지"));
        tabs.addTab(tabs.newTab().setText("다이어리"));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;
                if (position == 0)
                    selected = fragment2;
                else if (position == 1)
                    selected = fragment3;
                else if (position == 2)
                    selected = fragment4;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                
                ...

                else if(topic.equals("CO")){
                    
                    ...

                    notificationManager.notify(1, noti);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


    }

    public void test(){
        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getjson.php", "");
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result == null){
            }
            else {
                mJsonString = result;
                showGraph();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = params[1];
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();
                return null;
            }

        }
    }
    private void showGraph(){

        String TAG_JSON="root";
        String TAG_ID = "id";
        String TAG_NAME = "name";
        String TAG_VALUE = "value";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Intent intent = new Intent(this, chartActivity.class);
            int array_len = jsonArray.length();
            valueArray = new float[array_len-1];
            for(int i=0;i<array_len;i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if (i + 1 < array_len) {
                    valueArray[i] = Float.parseFloat(jsonObject.getString(TAG_VALUE));
                }
            }
            intent.putExtra("values", valueArray);
            startActivity(intent);
        } catch (JSONException e) {
            Log.d(TAG, "showGraph : ", e);
        }
    }
}
