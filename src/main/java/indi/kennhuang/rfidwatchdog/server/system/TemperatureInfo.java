package indi.kennhuang.rfidwatchdog.server.system;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import java.util.List;

public class TemperatureInfo {

    private static boolean init = false;
    private static double temperature;

    public static void dumpCPUInfo(){
        Components components = JSensors.get.components();
        List<Cpu> cpus = components.cpus;
        if (cpus != null) {
            for (final Cpu cpu : cpus) {
                System.out.println("Found CPU component: " + cpu.name);
                if (cpu.sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = cpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                    }

                    //Print fan speed
                    List<Fan> fans = cpu.sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                    }
                }
            }
        }
    }

    public static void start(){
        if(!init){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            temperature = getCPUTemp();
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    public static double getAutoTemp(){
        return temperature;
    }

    public static double getCPUTemp(){
        Components components = JSensors.get.components();
        List<Cpu> cpus = components.cpus;
        if (cpus != null) {
            double tempSum = 0;
            int count = 0;
            for (final Cpu cpu : cpus) {
                if (cpu.sensors != null) {
                    List<Temperature> temps = cpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        tempSum += temp.value;
                        count++;
                    }
                }
            }
            return tempSum/count;
        }
        return 0;
    }
}
