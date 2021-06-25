package com.example.fgand1.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fgand1.R;
import com.example.fgand1.model.Model;
import com.example.fgand1.viewModel.ViewModel;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements JoystickView.joystickListener {
    private ViewModel vm;
    private JoystickView joystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.vm = new ViewModel(new Model());
        SeekBar throttleSB = (SeekBar)findViewById(R.id.throttle) ;
        SeekBar rudderSB = (SeekBar)findViewById(R.id.rudder) ;
        TextView throttleTV = (TextView)findViewById(R.id.textView);
        TextView rudderTV = (TextView)findViewById(R.id.textView2);
        Button connectButton = (Button)findViewById(R.id.connect_button);
        EditText portET = (EditText)findViewById(R.id.port_number);
        EditText ipET = (EditText)findViewById(R.id.ip_number);
        this.joystick = (JoystickView)findViewById(R.id.planeJoystick);

        throttleSB.setVisibility(View.GONE);
        rudderSB.setVisibility(View.GONE);
        throttleTV.setVisibility(View.GONE);
        rudderTV.setVisibility(View.GONE);
        joystick.setVisibility(View.GONE);

        //setting onClick for the button
        connectButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //if the user isn't connected - updates the ip + the port and connecting
                if(connectButton.getText().equals("Connect"))
                {
                    updateIpAndPort(ipET.getText().toString(), Integer.parseInt(portET.getText().toString()));
                }

                //if the user isn't connected - disconnecting
                else if(connectButton.getText().equals("Disconnect"))
                {
                    connectButton.setText("Connect");
                    connectButton.setBackgroundColor(0xff0099cc);
                    throttleSB.setVisibility(View.GONE);
                    rudderSB.setVisibility(View.GONE);
                    throttleTV.setVisibility(View.GONE);
                    rudderTV.setVisibility(View.GONE);
                    joystick.setVisibility(View.GONE);

                    //default values
                    throttleSB.setProgress(0);
                    rudderSB.setProgress(50);
                }
            }
        });

        //listener for a change in the value of the seek bar
        throttleSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //in a value change of the throttle seek bar - updates the throttle value in the FG
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    updateThrottle((double)progress/100);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //listener for a change in the value of the seek bar
        rudderSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //in a value change of the rudder seek bar - updates the rudder value in the FG
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    updateRudder((2*(double)progress/100) - 1.0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    //updating the throttle in the VM
    private void updateThrottle(double val) throws IOException {
        this.vm.setThrottle(val);
    }

    //updating the rudder in the VM
    private void updateRudder(double val) throws IOException {
        this.vm.setRudder(val);
    }

    //checking if the ip and port are valid.
    //if valid - updating the VM, if not - toasting a message to the app screen
    private void updateIpAndPort(String ip, int port)
    {
        //checking if the ip and port are legal - checking input
        final boolean[] areVallid = {true};
        Thread thread = new Thread(){
            public void run() {
                try {
                    //trying to open the socket
                    Socket s = new Socket(ip, port);
                    s.close();
                } catch (Exception e) {
                    //in an exception - invalid port/ip
                    areVallid[0] = false;
                }
            }
        };
        thread.start();

        boolean isTimeout = false;
        try {
            //waiting for the socket creation
            thread.join(3000);

            //if the socket didn't finished during the waiting time - timeout
            if(thread.isAlive()){
                isTimeout = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //if timeout
        if(isTimeout == true)
        {
            Toast.makeText(getApplicationContext(), "Timeout for the connection.", Toast.LENGTH_LONG).show();
        }

        //if invallid ip/port
        else if(areVallid[0] == false)
        {
            Toast.makeText(getApplicationContext(), "Invallid IP or port.", Toast.LENGTH_LONG).show();
        }

        //valid ip and port(successful connection) - updating the viewModel(and then the model)
        else if(areVallid[0] == true)
        {
            Button b = (Button)findViewById(R.id.connect_button);
            b.setText("Disconnect");

            SeekBar sbt = (SeekBar)findViewById(R.id.throttle) ;
            SeekBar sbr = (SeekBar)findViewById(R.id.rudder) ;
            TextView tt = (TextView)findViewById(R.id.textView);
            TextView tr = (TextView)findViewById(R.id.textView2);
            b.setBackgroundColor(Color.RED);
            sbt.setVisibility(View.VISIBLE);
            sbr.setVisibility(View.VISIBLE);
            tt.setVisibility(View.VISIBLE);
            tr.setVisibility(View.VISIBLE);
            joystick.setVisibility(View.VISIBLE);

            this.vm.updateIpAndPort(ip, port);
        }
    }

    //will be called by the listener in "JoystickView" in a movement of the joystick
    //updates the values of aileron and elevator by the current position of the joystick
    @Override
    public void joytickMoved(float newAileron, float newElevator) {
        this.vm.setAileron(newAileron);
        this.vm.setElevator(newElevator);
    }
}