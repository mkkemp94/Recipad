package edu.wit.seniorproject.recipad;

import java.util.concurrent.TimeUnit;

public class Instruction extends Throwable implements Cloneable{

    private String instructionStep;
    private String instructionHr;
    private String instructionMin;
    private String instructionSec;

    //Default constructor for instructions
    public Instruction() {
        this.instructionStep = "";
        this.instructionHr = "0";
        this.instructionMin = "0";
        this.instructionSec = "0";
    }

    //Regular constructor for instructions class
    //If the instruction hours, minutes, and second is null or empty then it is zero
    public Instruction(String instructionStep, String instructionHr, String instructionMin, String instructionSec) {
        this.instructionStep = instructionStep;
        this.instructionHr = checkIfZero(instructionHr);
        this.instructionMin = checkIfZero(instructionMin);
        this.instructionSec = checkIfZero(instructionSec);
    }

    //Small function to check if the number is zero
    public String checkIfZero(String number){
        String zero = "00";
        if(number.equals("") || number.equals(null)){   return zero; }
        else{ return number;}
    }

    //Convert time to milliseconds
    public String convertToMillis(){
        String millis = "";
        //Convert String into Long
        long hrLong = Long.parseLong(this.getHr());
        long minLong = Long.parseLong(this.getMin());
        long secLong = Long.parseLong(this.getSec());

        //Add all the long after converting into milliseconds
        long totalLong = TimeUnit.HOURS.toMillis(hrLong)
                + TimeUnit.MINUTES.toMillis(minLong) +
                TimeUnit.SECONDS.toMillis(secLong);

        //Convert it back to a string
        millis = String.valueOf(totalLong);

        return millis;
    }

    //Convert Total millisecond into readable time ex. 00:00:00
    public String startTime(){
        long millis = Long.parseLong(convertToMillis());

        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis)
                , TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    //Getter and Setters
    public String getStep() {    return instructionStep;    }
    public void setStep(String instructionStep) {    this.instructionStep = instructionStep;    }
    public String getHr() {  return instructionHr;    }
    public void setHr(String instructionHr) {    this.instructionHr = instructionHr;    }
    public String getMin() { return instructionMin;    }
    public void setMin(String instructionMin) {  this.instructionMin = instructionMin;    }
    public String getSec() { return instructionSec;    }
    public void setSec(String instructionSec) {  this.instructionSec = instructionSec;    }

    //to String Method for debugging purposes
    @Override
    public String toString() {
        return "Instruction{" +
                "instructionStep='" + instructionStep + '\'' +
                ", instructionHr='" + instructionHr + '\'' +
                ", instructionMin='" + instructionMin + '\'' +
                ", instructionSec='" + instructionSec + '\'' +
                '}';
    }

    //Clone method
    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
}

