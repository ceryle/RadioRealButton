package co.ceryle.radiorealbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import co.ceryle.radiorealbutton.library.RadioRealButton;
import co.ceryle.radiorealbutton.library.RadioRealButtonGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioRealButtonGroup rrbg1 = (RadioRealButtonGroup) findViewById(R.id.radioRealButtonGroup_1);
        rrbg1.setOnClickedButtonPosition(new RadioRealButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                Toast.makeText(MainActivity.this, "Clicked position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        RadioRealButton radioRealButton;
    }
}
