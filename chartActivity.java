public class chartActivity extends AppCompatActivity {
    private LineChart lineChart;
    private Thread thread;
    private static final String TAG = "chartActivity";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mq7_chart); 
        lineChart = (LineChart) findViewById(R.id.lineChart); 
        LineData chartData = new LineData();
        lineChart.getDescription().setEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(true);
        chartData.setValueTextColor(Color.WHITE);
        lineChart.animateXY(1000,1000);
        lineChart.getDescription().setTextColor(Color.WHITE);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(10f);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.WHITE);
        yLAxis.setAxisMaximum(100f);
        yLAxis.setAxisMinimum(0f);
        yLAxis.setDrawGridLines(false);
        yLAxis.setGridColor(Color.WHITE);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setEnabled(false);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 전체화면
        lineChart.setData(chartData);
        feedMultiple();

    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "CO data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.GREEN);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.GREEN);
        set.setDrawFilled(true); // 선 아래로 색상 표시
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(true);
        return set;
    }

    private void feedMultiple() {
        if(thread != null) {
            thread.interrupt();
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addEntry();
            }
        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    runOnUiThread(runnable);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    private void addEntry() {
        Intent intent = getIntent();
        LineData data = lineChart.getData();
        String[] idandname = intent.getStringArrayExtra("idandname");
        float[] CO_Value = intent.getFloatArrayExtra("values");
        int max = CO_Value.length;
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
                int i = 0;
                while(i < max) {
                    float CO_data = CO_Value[i];
                    data.addEntry(new Entry(set.getEntryCount(), CO_data), 0);
                    i++;
                }
            }
            else { 
                data.addEntry(new Entry(set.getEntryCount(), CO_Value[max-1]), 0);
            }
            data.notifyDataChanged();
            lineChart.invalidate();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(120);
            lineChart.moveViewToX(data.getEntryCount());
        }
    }
}
