package com.example.fgand1.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Model {
    private String ip;
    private int port;
    private double aileron;
    private double elevator;
    private double rudder;
    private double throttle;
    private ExecutorService pool;

    //ctor
    public Model(){
        //default values
        this.aileron = 0.0;
        this.elevator = 0.0;
        this.rudder = 0.0;
        this.throttle = 0.0;

        //the thread pool(with one thread as asked)
        this.pool = Executors.newFixedThreadPool(1);
    }

    //updates the ip and the port
    public void updateIpAndPort(String ip, int port)
    {
        this.ip = ip;
        this.port = port;

    }

    //sending data by the field and the value to the FG by the format
    public void sendData(String pathOnFG, double value)
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                OutputStream output = null;
                try {
                    //creating the socket
                    Socket s = new Socket(ip, port);

                    //creating the PrintWriter and sending the data to the FG
                    output = s.getOutputStream();
                    PrintWriter out = new PrintWriter(output, true);
                    out.print("set " + pathOnFG + " " + value + "\r\n");
                    out.flush();

                    //closing the socket and the PrintWriter
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        //running the thread
        this.pool.execute(runnable);
    }

    //sets the value of the aileron + updates the FG
    public void setAileron(double aileron) {
        this.aileron = aileron;

        //updates FG
        sendData("/controls/flight/aileron", aileron);
    }

    //sets the value of the elevator + updates the FG
    public void setElevator(double elevator) {
        this.elevator = elevator;

        //updates FG
        sendData("/controls/flight/elevator", elevator);
    }

    //sets the value of the rudder + updates the FG
    public void setRudder(double rudder){
        this.rudder = rudder;

        //updates FG
        sendData("/controls/flight/rudder", rudder);
    }

    //sets the value of the throttle + updates the FG
    public void setThrottle(double throttle){
        this.throttle = throttle;

        //updates FG
        sendData("/controls/engines/current-engine/throttle", throttle);
    }
}
