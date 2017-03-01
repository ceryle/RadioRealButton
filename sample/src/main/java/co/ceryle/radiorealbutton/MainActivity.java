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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import co.ceryle.radiorealbutton.library.RadioRealButton;
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

        rrbg.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                updateText(position);
            }
        });

        rrbg.setOnLongClickedButtonListener(new RadioRealButtonGroup.OnLongClickedButtonListener() {
            @Override
            public boolean onLongClickedButton(RadioRealButton button, int position) {
                Toast.makeText(MainActivity.this, "Long Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = rrbg.getPosition();
                position = ++position % rrbg.getNumberOfButtons();
                rrbg.setPosition(position);

                updateText(position);
            }
        });
    }

    private void updateText(int position) {
        button.setText("Position: " + position);
    }
}
