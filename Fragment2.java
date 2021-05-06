public class Fragment2 extends Fragment {
    MainActivity mActivity;

    TextView co_state;
    TextView co_value;

    ImageButton graphButton;
    Button turnoff_alarm;

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        co_state = (TextView) rootView.findViewById(R.id.co_state);
        co_value = (TextView) rootView.findViewById(R.id.co_value);

        graphButton = (ImageButton)rootView.findViewById(R.id.graphButton);
        turnoff_alarm =(Button) rootView.findViewById(R.id.turnoff_alarm);

        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.test();
            }
        });
        turnoff_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.sendMsg("TURNOFF_WARNING", "WRNFF");
            }
        });

        return rootView;
    }
