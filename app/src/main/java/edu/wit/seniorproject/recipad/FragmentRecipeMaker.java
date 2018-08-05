package edu.wit.seniorproject.recipad;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by nguyenj11 on 7/16/2016.
 */
public class FragmentRecipeMaker extends Fragment implements View.OnClickListener, MainActivity.OnBackPressedListener {

    public String tag = "myApp";
    public EditText addRecipeName;
    public EditText addRecipeTime;
    public static String recipeName;
    public static String recipeTime;
    Button requiredIngredientButton;
    Button requiredInstructionsButton;
    Button AddRecipeButton;
    public static Spinner timeSpinner;
    Button help;

    DatabaseHelper db;

    public static int currentTimeType = 0;
    public String[] timetype = new String[]{"HR","MIN"};

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe_maker, container, false);

        hideSoftKeyboard();

        db = new DatabaseHelper(getContext());

        Log.v(tag, "This is the recipe edit flag id : "+ FragmentRecipeList.edit_flag_id);

        requiredIngredientButton = (Button) v.findViewById(R.id.add_required_ingredient);
        requiredIngredientButton.setOnClickListener(this);

        requiredInstructionsButton = (Button) v.findViewById(R.id.add_instructions);
        requiredInstructionsButton.setOnClickListener(this);

        AddRecipeButton = (Button) v.findViewById(R.id.add_recipe);
        requiredInstructionsButton.setOnClickListener(this);

        AddRecipeButton = (Button) v.findViewById(R.id.complete_add_Recipe);
        AddRecipeButton.setOnClickListener(this);

        addRecipeTime = (EditText) v.findViewById(R.id.new_time);
        addRecipeName = (EditText) v.findViewById(R.id.new_recipe_name);

        /**
         * Determine the type of the time whether it is hours or minutes
         */

        timeSpinner = (Spinner) v.findViewById(R.id.new_time_type);
        timeSpinner.setSelection(currentTimeType);

        //Checks when the spinner changed and then changes the static variable for the time type
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, timetype);
        timeSpinner.setAdapter(adapter);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        currentTimeType = 0;
                        Log.v(tag, "Hours");
                        break;
                    case 1:
                        currentTimeType = 1;
                        Log.v(tag, "Minutes");
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Get the static variable and set the textview as it if they changed it
        if(recipeName != null){
            addRecipeName.setText(recipeName);
        }
        if(recipeTime != null){
            addRecipeTime.setText(recipeTime);
        }
        timeSpinner.setSelection(currentTimeType);

        //If the user press done then it set the static variable for name and time as the inputted selection
        addRecipeTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    recipeTime = addRecipeTime.getText().toString();
                }
                return false;
            }
        });

        addRecipeName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    recipeName = addRecipeName.getText().toString();
                }
                return false;
            }
        });

        help = (Button) v.findViewById(R.id.help);

        help.setOnClickListener(this);

        return v;
    }

    /**
     * OnClick put the onclick methods for
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_required_ingredient:
                Log.v(tag,"Ingredient button");
                goToAddRequireIngredient();
                break;
            case R.id.add_instructions:
                Log.v(tag,"Instruction button");
                goToAddInstructions();
                break;
            case R.id.complete_add_Recipe:
                Log.v(tag,"Recipe Complete");
                addRecipeComplete();
                break;
            case R.id.help:
                help();
                break;
        }
    }

    /**
     * Goes to corresponding fragment, ingredient and instructions
     */

    private void goToAddRequireIngredient() {
        hideSoftKeyboard();
        recipeName = addRecipeName.getText().toString();
        recipeTime = addRecipeTime.getText().toString();
        FragmentAddIngredients fragment = new FragmentAddIngredients();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void goToAddInstructions() {
        hideSoftKeyboard();
        recipeName = addRecipeName.getText().toString();
        recipeTime = addRecipeTime.getText().toString();
        FragmentAddInstructions fragment = new FragmentAddInstructions();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Check all the fields to see if the instructions are set
     */

    private void addRecipeComplete() {
        int missingField = 0;

        recipeName = addRecipeName.getText().toString();
        recipeTime = addRecipeTime.getText().toString();

        /**
         * The if statements check if the static variables are empty or not
         * If it is empty then it would notic the user that is it empty
         * Else it will create a new recipe and add it to the database
         */
        if (addRecipeName.getText().toString().matches("")){
            addRecipeName.setError("The name can't be empty");
            missingField++;
        }
        if (addRecipeTime.getText().toString().matches("")){
            addRecipeTime.setError("The time can't be empty");
            missingField++;
        }
        if (FragmentAddIngredients.ingredientsList.size() == 0){
            requiredIngredientButton.setError("The list of ingredients are empty");
            Toast.makeText(getContext(), "Ingredients list Size is empty", Toast.LENGTH_LONG).show();
            missingField++;
        }
        if (FragmentAddInstructions.instructionList.size() == 0){
            requiredInstructionsButton.setError("The list of instructions are empty");
            Toast.makeText(getContext(), "Ingredients list Size is empty", Toast.LENGTH_LONG).show();
            missingField++;
        }

        //This edit flag id check if the user is editing a current recipe or not
        int edit_id = 0;
        if(FragmentRecipeList.edit_flag_id.equals("") || FragmentRecipeList.edit_flag_id.equals(null)) {
            edit_id = 0;
        }
        else{
            edit_id = Integer.parseInt(FragmentRecipeList.edit_flag_id);
        }

        //If the edit flag is on then the it changes the recipe that is being added
        if (edit_id > 0){
            if (missingField == 0) {

                db = new DatabaseHelper(getContext());
                db.openDB();

                //String id, String name, String time, String ingredients, String instructions
                String name = recipeName.trim();
                String time = recipeTime + " " + timeSpinner.getSelectedItem();
                String ingredients = itemListToString(FragmentAddIngredients.ingredientsList);
                String instructions = instructionListToString(FragmentAddInstructions.instructionList);

                db.updateRecipeData(FragmentRecipeList.edit_flag_id, name, time, ingredients, instructions);

                db.close();
                FragmentRecipeList.edit_flag_id = "0";

                clearRecipeMaker();
                goToRecipeList();
            }
            else {
                Toast.makeText(getContext(), "Some of the fields are empty!", Toast.LENGTH_LONG).show();
            }
        }

        //Else if the edit flag is off then it still check the duplicate flag if the name already exist
        //If it doesn't exit then the recipe will be created
        else {
            Boolean duplicateNameCheck = checkDupName();
            if (duplicateNameCheck) {
                Toast.makeText(getContext(), "The recipe name is already taken. Please rename.", Toast.LENGTH_LONG).show();
                missingField++;
            }

            if (missingField == 0) {
                printNewRecipe();

                String name = recipeName.trim();
                String time = recipeTime + " " + timeSpinner.getSelectedItem();
                String ingredients = itemListToString(FragmentAddIngredients.ingredientsList);
                String instructions = instructionListToString(FragmentAddInstructions.instructionList);
                Log.v(tag, "Name: " + name + "\n Time: " + time + "\n Ingredients: " +
                        ingredients + "\n Instructions: " + instructions);

                db = new DatabaseHelper(getContext());

                db.openDB();
                db.insertRecipeData(name, time, ingredients, instructions);
                db.close();

                clearRecipeMaker();
                goToRecipeList();
            } else {
                Toast.makeText(getContext(), "Some of the fields are empty!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * A method to check if there are any duplicates by recipe name
     * @return
     */
    private Boolean checkDupName() {
        boolean dups = false;
        db.openDB();

        // second go through the database
        Cursor cursor = db.getAllRecipeData();
        while (cursor.moveToNext())
        {
            String name = cursor.getString(1);
            Log.v(tag, "Name : " + name);
            for(int i = 0; i < FragmentRecipeList.recipeList.size(); i++){
                if(name.toLowerCase().equals(addRecipeName.getText().toString().toLowerCase())){
                    dups = true;
                }
            }
        }
        db.close();
        return dups;
    }

    /**
     * Method to take an list of items and make into a string
     * @param list
     * @return
     */
    public static String itemListToString(ArrayList<Item> list){
        StringBuffer stringBuffer = new StringBuffer("");
        for(Item item : list){
            stringBuffer.append(item.getName()+" separ, "+item.getQty()+ " separ, ");
        }
        String listString = stringBuffer.toString();
        return listString;
    }

    /**
     * Method to take an list of instructions and make it into one string
     * @param list
     * @return
     */
    //Convert a instruction list into a string
    public static String instructionListToString(ArrayList<Instruction> list){
        StringBuffer stringBuffer = new StringBuffer("");
        for(Instruction instruction : list) {
            stringBuffer.append(instruction.getStep() + " separ, " +
                    instruction.getHr() + " separ, " +
                    instruction.getMin() + " separ, " +
                    instruction.getSec() + " separ, ");
        }
        String listString = stringBuffer.toString();
        return listString;
    }

    /**
     * Goes to the recipe list
     */
    public void goToRecipeList() {
        FragmentRecipeList fragment = new FragmentRecipeList();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Clear the recipeMaker
     */

    public void clearRecipeMaker(){
        addRecipeName.setText("");
        addRecipeTime.setText("");
        recipeName = "";
        recipeTime = "";
        currentTimeType = 0;
        FragmentRecipeList.edit_flag_id = "";
        FragmentAddIngredients.ingredientsList = new ArrayList<Item>();
        FragmentAddInstructions.instructionList = new ArrayList<Instruction>();
    }

    /**
     * Go print out the new recipe for debugging purposes
     */
    public void printNewRecipe(){
        Log.v(tag, "This is what the spinner is currently holding " + timeSpinner.getSelectedItem());
        Log.v(tag, "It is completed the recipe is ");
        Log.v(tag, "Name is " + recipeName + ", Time required is "+ recipeTime);
        Log.v(tag, "This is the instructions ");
        for(Instruction instruct : FragmentAddInstructions.instructionList){
            Log.v(tag, instruct.getStep() + " ");}
        Log.v(tag, "This is the ingredients ");
        for(Item item : FragmentAddIngredients.ingredientsList){
            Log.v(tag, "This is the name " + item.getName() + ". This is the qty " + item.getQty());}
    }

    /**
     * Method to hide the keyboard
     */
    private void hideSoftKeyboard()
    {
        if (getActivity().getCurrentFocus()!= null && getActivity().getCurrentFocus() instanceof EditText)
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(addRecipeTime.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(addRecipeName.getWindowToken(), 0);
        }
    }

    /**
     * On back button pressed go to recipe list and clear current recipe
     */

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Confirm");
        builder.setMessage("Do you want to leave the recipe maker? \n");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                clearRecipeMaker();
                FragmentRecipeList fragment = new FragmentRecipeList();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", null).create();

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Help button
     */
    public void help()
    {
        /* Alert box to show messages. */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("In the recipe maker, you can:");
        builder.setMessage("1. Name your recipe. \n\n" +
                "2. Specify how much time it will take to prepare. \n\n" +
                "3. Add the ingredients needed to make the recipe.  \n\n" +
                "4. List the recipe instructions. \n\n" +
                "5. Tap Finish to save the recipe!");
        builder.show();
    }

    //On switch clear the recipe maker
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(tag, "On Destroy");
        clearRecipeMaker();
    }

}