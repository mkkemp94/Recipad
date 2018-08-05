package edu.wit.seniorproject.recipad;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by nguyenj11 on 7/16/2016.
 */
public class FragmentInstructionList extends Fragment {

    //Tag for log.v messages
    public String tag ="myApp";

    TextView instructText;
    TextView instructTime;
    ArrayList<Instruction> currentInstructionList;
    Button nextInstructButton;
    Button startButton, stopButton;
    int currentStep;
    boolean goToRecipe;

    //Flag to check the timer
    boolean resumeFlag, pauseFlag,startFlag;
    CountDownTimer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_instructions, container, false);

        instructText = (TextView) v.findViewById(R.id.instruction_step);
        nextInstructButton = (Button) v.findViewById(R.id.next_instruction_btn);
        instructTime = (TextView) v.findViewById(R.id.instruction_time);
        startButton = (Button) v.findViewById(R.id.btnStart);
        stopButton = (Button) v.findViewById(R.id.btnStop);

        currentInstructionList = FragmentRecipeList.staticInstructionList;

        //Set the first instruction step and time
        instructText.setText("1. " + currentInstructionList.get(0).getStep());
        instructTime.setText(currentInstructionList.get(0).startTime());
        timer = new CounterClass(Integer.parseInt(currentInstructionList.get(0).convertToMillis()), 1000);

        //Change the visibility of the time stuff if there is no time!
        changeVisibility();

        currentStep = 0;

        //Set gotoRecipe flag false then set it to true since it finish going through the instructions
        goToRecipe = false;

        nextInstructButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goToRecipe == false) {
                    goToNextInstruction();
                }
                else {
                    goToRecipeList();
                }
            }
        });

        //Set Flags for the pause and start buttons
        startFlag = true;
        pauseFlag = false;
        pauseFlag = false;
        /**
         * This is the start button on click method
         * first once it is on click then it changes the text to pause and set that flag on
         * if it is clicked again the text will change to resume and changes flag and keep the current time
         */
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start
                if(startFlag == true){
                    timer.start();
                    startButton.setText("Pause");
                    pauseFlag = true;
                    startFlag = false;
                }
                //Resume
                else if(pauseFlag == true){

                    if(instructTime.getText().equals("Completed.")||instructTime.getText().equals("00:00:00")){
                        reset();
                    }
                    else {
                        resumeFlag = true;
                        pauseFlag = false;
                        String tempText = (String) instructTime.getText();
                        timer.cancel();
                        instructTime.setText(tempText);
                        startButton.setText("Resume");
                        timer = new CounterClass(Integer.parseInt(convertToMillis(tempText)), 1000);
                    }
                }
                //Pause
                else if(resumeFlag == true){
                    pauseFlag = true;
                    resumeFlag = false;
                    timer.start();
                    startButton.setText("Pause");
                }

            }
        });
        /**
         * Stop button resets the time back to were it originally was
         */
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });


        return v;
    }


    //Reset the timer and the flag
    public void reset(){
        timer.cancel();
        startFlag = true;
        pauseFlag = false;
        resumeFlag = false;
        startButton.setText("Start");
        instructTime.setText(currentInstructionList.get(currentStep).startTime());
        timer = new CounterClass( Integer.parseInt(currentInstructionList.get(currentStep).convertToMillis()), 1000);
    }

    /**
     * Goes to the recipe list
     */
    private void goToRecipeList() {
        FragmentRecipeList fragment = new FragmentRecipeList();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Convert the string into milliseconds froma "00:00:00" format
     * @param tempText
     * @return
     */

    private String convertToMillis(String tempText) {
        ArrayList<String> timeSplit = new ArrayList<String>(Arrays.asList(tempText.split(":")));
        String millis = "";
        //Convert String into Long
        long hrLong = Long.parseLong(timeSplit.get(0));
        long minLong = Long.parseLong(timeSplit.get(1));
        long secLong = Long.parseLong(timeSplit.get(2));

        //Add all the long after converting into milliseconds
        long totalLong = TimeUnit.HOURS.toMillis(hrLong)
                + TimeUnit.MINUTES.toMillis(minLong) +
                TimeUnit.SECONDS.toMillis(secLong);

        //Convert it back to a string
        millis = String.valueOf(totalLong);

        return millis;
    }

    /**
     * Goes to the next instruction
     * Check if current step is higher or not than the current instruction size
     * if it isn't greater than the size then it set the text and time accordingly
     */
    private void goToNextInstruction() {
        currentStep++;

        if(currentStep>= currentInstructionList.size()){
            nextInstructButton.setText("Go to Recipe List");
            instructText.setText("Completed");
            instructTime.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.INVISIBLE);
            stopButton.setVisibility(View.INVISIBLE);
            timer.cancel();
            goToRecipe = true;
        }
        else{
            timer.cancel();
            instructText.setText((currentStep+1)+". "+currentInstructionList.get(currentStep).getStep());
            instructTime.setText(currentInstructionList.get(currentStep).startTime());
            timer = new CounterClass( Integer.parseInt(currentInstructionList.get(currentStep).convertToMillis()), 1000);

            //If the millisecond of the current time is equal to 0 then it will be invisible
            changeVisibility();
        }
    }


    //Change the visibility of the buttons and time
    public void changeVisibility(){
        if(Integer.parseInt(currentInstructionList.get(currentStep).convertToMillis()) > 0){
            startButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            instructTime.setVisibility(View.VISIBLE);
        }
        else {
            instructTime.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.INVISIBLE);
            stopButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Counter Class Object used for updating the time text view
     */
    public class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis)
                    , TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            Log.v(tag, hms);
            instructTime.setText(hms);
        }

        @Override
        public void onFinish() {
            instructTime.setText("Completed.");
        }
    }

}
