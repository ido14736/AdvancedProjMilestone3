package com.example.fgand1.viewModel;

import com.example.fgand1.model.Model;

public class ViewModel {
    private Model m;

    //ctor
    public ViewModel(Model model)
    {
        this.m = model;
    }

    //updates the ip and the port in the model
    public void updateIpAndPort(String ip, int port)
    {
        this.m.updateIpAndPort(ip, port);
    }

    //sets the aileron in the model
    public void setAileron(double aileron) {
        this.m.setAileron(aileron);
    }

    //sets the elevator in the model
    public void setElevator(double elevator) {
        this.m.setElevator(elevator);
    }

    //sets the rudder in the model
    public void setRudder(double rudder) {
        this.m.setRudder(rudder);
    }

    //sets the throttle in the model
    public void setThrottle(double throttle) {
        this.m.setThrottle(throttle);
    }
}
