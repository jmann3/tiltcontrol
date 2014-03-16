/*
Copyright 2013 Michael DiGiovanni glass@mikedg.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.mikedg.glass.control;

import android.app.Activity;
import android.os.Bundle;

public abstract class MainActivity extends Activity {
    private MainPresentationModel mPresentationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TuggableView(this, R.layout.activity_main));

        mPresentationModel = new MainPresentationModel();
        //FIXME: I'm probably going to forget that this happens
        GlassControlService.launch(this); //Launch immediately so it helps people with disabilities!
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresentationModel.tearDown();
    }

    protected void onItemSelected(int itemId) {
        switch (itemId) {
            case R.string.enable:
                GlassControlService.launch(this);
            break;
            case R.string.disable:
                GlassControlService.turnOff(this);
            break;
            case R.layout.card_start_tilt_setting:
                changeStartTiltSetting();
            break;
        }
    }

    protected abstract void changeStartTiltSetting();


    protected MainPresentationModel getPresentationModel() {
        return mPresentationModel;
    }

    protected abstract void onCommandsChanged(); //What should happen when the underlying commands available change, basically refresh our views into the model
}
