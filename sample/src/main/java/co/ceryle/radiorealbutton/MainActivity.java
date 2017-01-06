/*
 * Copyright (C) 2016 Ege Aker <egeaker@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.ceryle.radiorealbutton;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import co.ceryle.radiorealbutton.library.RadioRealButtonGroup;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private RadioRealButtonGroup rrbg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        rrbg = (RadioRealButtonGroup) findViewById(R.id.radioRealButtonGroup_1);

        button.setTransformationMethod(null);
        updateText(rrbg.getPosition());


        rrbg.setOnClickedButtonPosition(new RadioRealButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                updateText(position);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = rrbg.getPosition();
                position = ++position % rrbg.getNumberOfButton();
                rrbg.setPosition(position, true);

                updateText(position);
            }
        });


        rrbg.setEnabled(false);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rrbg.setEnabled(true);
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    private void updateText(int position) {
        button.setText("Position: " + position);
    }
}
