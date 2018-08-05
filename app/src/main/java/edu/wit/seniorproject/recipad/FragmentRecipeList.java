package edu.wit.seniorproject.recipad;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nguyenj11 on 7/12/2016.
 */
public class FragmentRecipeList extends DialogFragment implements View.OnClickListener {

    /**
     * Recipe list is the list of recipe
     * In the HashMap the string is the name of the recipe
     * The List of strings show the information of the Recipe
     */

    public static ArrayList<Instruction> staticInstructionList;
    ExpandableListView expandableListView;
    AdapterExpandableRecipes adapterExpandableRecipes;
    public HashMap<String, List<String>> recipeAdapter = new HashMap<String, List<String>>();
    public List<String> recipeName = new ArrayList<String>();
    public static ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
    public TextView requiredIngredientsTV;
    public TextView ownedIngredientsTV;
    public static String edit_flag_id = "";
    DatabaseHelper myDb;
    String tag = "myApp";
    Button viewAll;
    ArrayList<Item> finalRequiredIng;

    /**
     * Then it set up the expandable list adapter and the on on child click listener
     * @param savedInstanceState
     */

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        expandableListView = (ExpandableListView) getView().findViewById(R.id.exp_listview);

        /* Create the database */
        myDb = new DatabaseHelper(getContext());
        loadRecipeList();

        //Open the database
        myDb.openDB();

        //Load the recipe list
        for (Recipe recipe : recipeList)
        {
            ArrayList<Item> recipeIngredients = recipe.getIngredients();
            int numIngredients = recipe.getIngredients().size();
            int numberInInventory = 0;

            for (Item ingredient : recipeIngredients)
            {
                //Start of the cursor
                Cursor inventoryCursor = myDb.getAllInventoryData();

                //Loop through database of inventory data
                while (inventoryCursor.moveToNext())
                {
                    // Name of current inventory item
                    String invName = inventoryCursor.getString(1);
                    if (invName.toLowerCase().matches(ingredient.getName().toLowerCase()))
                    {
                        numberInInventory++;
                    }
                }
            }
            if (numberInInventory == numIngredients)
            {
                recipe.setHasAllIngredients(true);
            }
        }

        // Close the database
        myDb.close();

        adapterExpandableRecipes = new AdapterExpandableRecipes(getView().getContext(), recipeName, recipeAdapter);

        expandableListView.setAdapter(adapterExpandableRecipes);

        //This is the set on child click listener it will get which item is clicked on the adapter
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, int childPosition, long id) {

            /**
             * If the child click is 1, it clicks for the ingredients
             * It will have the builder and show the required amount of ingredients
             * It will show the user ingredients they have currently for each ingredients
             * Then there is an add button to add the required
             */
            if (childPosition == 1)
            {
                /**
                 * USER CLICKED TAP FOR INGREDIENTS
                 */

                //Open the database
                myDb.openDB();

                //String Buffer for the ingredients required and ingredients owned
                StringBuffer ownIngredientsText = new StringBuffer("");
                StringBuffer requireIngredientsText = new StringBuffer("");

                //Start of the cursor
                Cursor inventoryCursor = myDb.getAllInventoryData();

                //Set Flag if it owns it or not
                boolean owned_flag = false;

                //Clone List and store it in required Ingredient
                ArrayList<Item> requiredIng = null;
                try {
                    requiredIng = cloneList(recipeList.get(groupPosition).getIngredients());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                //Loop through the recipe list of ingredients
                /**
                 * This loops builds the string buffer for the own and require ingredients and
                 * it also gets alters the requiredIng so that requiredIng only show what is required
                 * from the inventory
                 */
                for (int i = 0; i < recipeList.get(groupPosition).getIngredients().size(); i++)
                {

                    //Store the current item
                    Item temp = recipeList.get(groupPosition).getIngredients().get(i);
                    requireIngredientsText.append(temp.getName()+" : "+temp.getQty()+"\n\n");

                    owned_flag = false;
                    //Loop through database of inventory data
                    while (inventoryCursor.moveToNext())
                    {
                        String dbCurrentItemName = inventoryCursor.getString(1);
                        String dbCurrentItemQty = inventoryCursor.getString(2);

                        //If current item qty is nothing then it is 1
                        if (dbCurrentItemQty.matches("")) {dbCurrentItemQty = "1";}

                        //If the temp item name is equal to the current item then it would display how much the current item is and change the  requiredIng
                        if (temp.getName().toLowerCase().equals( dbCurrentItemName.toLowerCase()))
                        {
                            //They owned it so now set flag to true
                            owned_flag = true;

                            //The have all items needed. Set that boolean flag to true;
                            recipeList.get(groupPosition).setHasAllIngredients(true);
                            Log.v("color", recipeList.get(groupPosition).getName() + " " + recipeList.get(groupPosition).getHasAllIngredients());

                            //Append the text to ingredients
                            ownIngredientsText.append( dbCurrentItemName + " : " + dbCurrentItemQty + "\n\n"); //" / " + temp.getQty() +

                            int tempQty = Integer.parseInt(temp.getQty());
                            int dbItemQty = Integer.parseInt(dbCurrentItemQty);
                            int diff_current_inventory = tempQty - dbItemQty;

                            Log.v(tag, String.valueOf(diff_current_inventory));

                            if (diff_current_inventory > 0)
                            {   requiredIng.get(i).setQty(String.valueOf(diff_current_inventory));  }
                            else
                            {   requiredIng.get(i).setQty("0"); }
                        }
                    }
                    if (owned_flag == false)
                    {
                        ownIngredientsText.append(temp.getName() + " : " + "0" + "\n\n"); //  + "/"+ temp.getQty()
                    }
                    inventoryCursor = myDb.getAllInventoryData();
                }
                LayoutInflater inflater = LayoutInflater.from(getContext());

                //Set the view of the current view
                View customView = inflater.inflate(R.layout.fragment_recipe_qty, parent, false);
                requiredIngredientsTV = (TextView) customView.findViewById(R.id.ingredients_r_txt);
                ownedIngredientsTV = (TextView) customView.findViewById(R.id.ingredients_o_txt);

                //Set the text for the owned and required Ingredients text view
                requiredIngredientsTV.setText(requireIngredientsText);
                ownedIngredientsTV.setText(ownIngredientsText);

                //When click add required, it will add the amount needed in the grocery shopping list
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                finalRequiredIng = requiredIng;

                //Add the required if user does not have some required items
                builder.setPositiveButton("Add Required", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    addRequireIngredients(myDb, finalRequiredIng);

                    FragmentGroceryList fragment = new FragmentGroceryList();
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.commit();
                    getActivity().setTitle("Shopping List");
                    }
                });

                //Do nothing on cancel
                builder.setNegativeButton("Cancel", null).create();
                builder.setView(customView);
                builder.show();
            }

            /**
             * When the child clicked is 2 then the instruction is clicked
             * It will check if they have the required amount in the inventory
             * Next then after it confirms they have enough then it will go to
             * the instruction setting the static variable "current instructions"
             * to the current instructions.
             */
            else if(childPosition == 2)
            {
                /**
                 * USER CLICKED TAP FOR INSTRUCTIONS
                 */

                // Open database
                myDb.openDB();

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Confirm");
                    builder.setMessage("Do you want to build this recipe? \n");

                    builder.setPositiveButton("Build", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                        //Check if the user is able to subtract the ingredient first
                        Boolean startInstruct = subtractRequireIngredients(recipeList.get(groupPosition).getIngredients(), myDb);
                        if (startInstruct) {
                            // THIS IS THE FIRST RECIPE'S INSTRUCTION ARRAY
                            staticInstructionList = recipeList.get(groupPosition).getInstruction();
                            startInstructions();
                        }
                        //If user doesn't have enough ingredient it will ask if the user wants to read it anyways
                        else {
                            AlertDialog.Builder notEnough = new AlertDialog.Builder(getContext());

                            notEnough.setMessage("You don't have the necessary ingredients! Do you want to read the " +
                                "recipe anyway?")
                                .setTitle("Not Enough Ingredients")
                                .setPositiveButton("Just Read", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        staticInstructionList = recipeList.get(groupPosition).getInstruction();
                                        startInstructions();
                                    }
                                })

                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                            AlertDialog notEnoughDialog = notEnough.create();
                            notEnoughDialog.show();
                        }
                        dialog.dismiss();
                        }
                    });

                    builder.setNeutralButton("Just Read", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            staticInstructionList = recipeList.get(groupPosition).getInstruction();
                            startInstructions();
                        }
                    });

                    builder.setNegativeButton("Cancel", null).create();
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                /**
                 * When the user click the edit button
                 * Build a alert dialog then check if they want to edit the recipe
                 */
                else if(childPosition == 3)
                {
                    // USER CLICKED EDIT BUTTON
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Confirm");
                    builder.setMessage("Do you want to edit this recipe? \n");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            editRecipe(recipeList.get(groupPosition));
                            goToAddNewRecipe();
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("Cancel", null).create();
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                /**
                 * DELETE RECIPE
                 * Go through the database and go to that id
                 * and delete that item
                 */
                else if(childPosition == 4)
                {
                    /**
                     * DELETE RECIPE
                     */
                    android.support.v7.app.AlertDialog.Builder remove = new android.support.v7.app.AlertDialog.Builder(getContext());
                    remove.setTitle("Remove item")
                        .setMessage("Are you sure you want to remove this recipe?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int clickedId) {

                            Recipe hi = recipeList.get(groupPosition);
                            String clickedRecipeName = hi.getName();

                            String clickedRecipeId = "";

                            myDb.openDB();

                            // Search through the database to see if it's already there
                            Cursor res = myDb.getAllRecipeData();

                            while (res.moveToNext())
                            {
                                String recipeNameInDatabase = res.getString(1);

                                if (clickedRecipeName.toLowerCase().matches(recipeNameInDatabase.toLowerCase()))
                                {
                                    clickedRecipeId = res.getString(0);
                                }
                            }

                            int deletedRow = myDb.deleteRecipeItem(clickedRecipeId);

                            // Check if data was deleted or not
                            if (deletedRow > 0)
                                Toast.makeText(getContext(), "Data deleted", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getContext(), "Data not deleted", Toast.LENGTH_LONG).show();

                            // Close database
                            myDb.close();

                            // REFRESH
                            FragmentRecipeList fragment = new FragmentRecipeList();
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragment);
                            fragmentTransaction.commit();
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                    android.support.v7.app.AlertDialog dialog = remove.create();
                    dialog.show();
                }
                return true;
            }
        });
    }


    /**
     * Start the instructions and go to the instruction for current recipe
     */

    private void startInstructions(){
        FragmentInstructionList fragment = new FragmentInstructionList();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Subtract the require ingredients from the inventory list
     * @param requiredIngredientList
     * @param db
     * @return
     */

    private boolean subtractRequireIngredients(ArrayList<Item> requiredIngredientList, DatabaseHelper db) {
        //Temporarily store current list of recipeList
        Cursor res = db.getAllInventoryData();
        ArrayList<Item> requireI = null;
        try {
            requireI = cloneList(requiredIngredientList);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        //Loop through the recipe list of ingredients
        for(int i = 0; i < requiredIngredientList.size(); i++){

            //Store the current item
            Item temp = requiredIngredientList.get(i);

            //Loop through database of inventory data
            while (res.moveToNext()) {
                String dbCurrentItemName = res.getString(1);
                String dbcurrentItemQty = res.getString(2);
                //If current item qty is nothing then it is 1
                if (dbcurrentItemQty.matches("")) {dbcurrentItemQty = "1";}

                //If the temp item name is equal to the current item then it would display how much the current item is and change the  requiredIng
                if (temp.getName().toLowerCase().equals( dbCurrentItemName.toLowerCase())) {

                    int diff_current_inventory = (Integer.parseInt(temp.getQty()) - Integer.parseInt(dbcurrentItemQty));
                    if (diff_current_inventory >= 0) {
                        requireI.get(i).setQty(String.valueOf(diff_current_inventory));
                    } else {
                        requireI.get(i).setQty("0");
                    }
                }
            }
            res = db.getAllInventoryData();
        }
        int checkRequire = 0;
        for(int i = 0; i < requireI.size(); i++) {
            Log.v(tag, "This is the require amount needed "+ requireI.get(i).getQty());
            if(Integer.parseInt(requireI.get(i).getQty()) > 0){
                checkRequire++;
            }
        }
        res = db.getAllInventoryData();
        //If there is non required then it begin subtract
        if(checkRequire == 0){
            Toast.makeText(getContext(), "Item is subtracted from the inventory", Toast.LENGTH_LONG).show();
            while (res.moveToNext()) {
                for(int i = 0; i < requiredIngredientList.size(); i++) {
                    if (res.getString(1).toLowerCase().equals(requiredIngredientList.get(i).getName().toLowerCase())) {
                        Log.v(tag, "This is the require amount needed " + requiredIngredientList.get(i).getQty());

                        db.deleteInventoryItem(res.getString(0));
                        int inventoryItemQty = Integer.parseInt(res.getString(2)) - Integer.parseInt(requiredIngredientList.get(i).getQty());
                        String InventoryItemQ = String.valueOf(inventoryItemQty);
                        if(inventoryItemQty != 0) {
                            db.insertInventoryData(requireI.get(i).getName(), InventoryItemQ, "");
                        }
                    }
                }
            }
            db.close();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Add Require Ingredients, adds the ingredients required for the recipe
     * Checks both the grocery list and the inventory list
     * @param myDb
     * @param requiredIng
     */

    public void addRequireIngredients(DatabaseHelper myDb, ArrayList<Item> requiredIng){
        //Start the Cursor for grocery data
        Cursor grocCursor = myDb.getAllGroceryData();
        int numberRequiredItem = 0;

        /**
         * Loops through the required ingredients
         */

        for(int i = 0; i < requiredIng.size(); i++){
            Item temp = requiredIng.get(i);
            String itemQty = temp.getQty();
            numberRequiredItem = numberRequiredItem + Integer.parseInt(itemQty);

            /**
             * If they have nothing required and it does nothing
             */

            if (Integer.parseInt(itemQty) == 0){
                //If it is zero it do nothing to the grocery list
            }

            /**
             * Else if compares it and change the quantity
             */

            else {
                //Compares it
                grocCursor = myDb.getAllGroceryData();
                while (grocCursor.moveToNext()) {
                    String currentItemName = grocCursor.getString(1).toLowerCase();
                    Log.v(tag, "This is current : " + currentItemName + " This is Required : " + temp.getName());

                    //If current name is equal to the temp/require name
                    if (currentItemName.matches(temp.getName().toLowerCase())) {

                        //Get string qty of groc resource
                        String currentItemQty = grocCursor.getString(2);
                        if (currentItemQty.matches("")) {
                            currentItemQty = "1";
                        }
                        Log.v(tag, "Item Found Equal " + currentItemName + "\n The QTY is" + currentItemQty + " in the grocery list");
                        if (Integer.parseInt(currentItemQty) >= Integer.parseInt(itemQty)) {
                            itemQty = String.valueOf(Integer.parseInt(currentItemQty));
                        }
                        myDb.deleteGroceryItem(grocCursor.getString(0));
                    }
                }
                myDb.insertGroceryData(temp.getName(),itemQty,"");
            }
        }
        myDb.close();
        Toast.makeText(getView().getContext(), "Added "+ numberRequiredItem + " items to the grocery list", Toast.LENGTH_LONG).show();
    }

    /**
     * On Create View adds the button and set the onclicks
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe_book, container, false);

        //This is the edit flag it initialize it to empty if the user edit
        //a recipe then the flag is off and the user is editing a recipe in the
        //recipe maker
        edit_flag_id = "";

        /**
         * Set the buttons with the onclicks
         */
        Button addRecipeButton = (Button) v.findViewById(R.id.add_recipe);
        addRecipeButton.setOnClickListener(this);
        Button options = (Button) v.findViewById((R.id.options));
        options.setOnClickListener(this);
        //Button help = (Button) v.findViewById(R.id.btn_help);
        //help.setOnClickListener(this);

        return v;
    }


    /** Load Recipe List or Ingredient List from an SQL
     * Open SQL database
     * SQL Info to an array of Recipe list
     */
    private void loadRecipeList()
    {
        // Clear the current recipe list
        recipeList.clear();;

        //1st load the db
        myDb.openDB();

        // second go through the database
        Cursor cursor = myDb.getAllRecipeData();

            while (cursor.moveToNext())
            {
                /**
                 * Clear the recipe
                 * while (go through the database){
                 * String name = cursor getString 1
                 * String time = cursor getString 2
                 * ArrayList<String> instruction = stringtoArrayMethod(getString)
                 * ArrayList<Item> Ingredient = stringto item array method(get string)
                 * add new recipe
                 */

                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String time = cursor.getString(2);
                ArrayList<Instruction> instructions = stringToInstructionList(cursor.getString(4));    // convert to array list
                ArrayList<Item> ingredients = stringToIngredientList(cursor.getString(3));   // convert
                //Recipe recipe = new Recipe(id, name, time, )
                //Item item = new Item(id, name, qty, exp);

                Log.v("recipe", name);
                addRecipe(name, time, ingredients, instructions);
            }
    }

    /**
     * Convert a string back to an instruction list
     * breaks into an array list and returns that array list
     * @param longListString
     * @return
     */

    public static ArrayList<Instruction> stringToInstructionList(String longListString){
        ArrayList<Instruction> instructionList = new ArrayList<Instruction>();

        ArrayList<String> instructionListSplit = new ArrayList<String>(Arrays.asList(longListString.split(" separ, ")));
        for(int i = 0; i < instructionListSplit.size(); i++){
            Instruction newItem = new Instruction(  instructionListSplit.get(i),
                    instructionListSplit.get(i+1),
                    instructionListSplit.get(i+2),
                    instructionListSplit.get(i+3));
            instructionList.add(newItem);
            i = i + 3;
        }
        return instructionList;
    }

    //
    /**
     * Convert a string back into the ingredient list
     * breaks into an array list of items and returns it
     * @param longListString
     * @return
     */
    public static ArrayList<Item> stringToIngredientList(String longListString)
    {
        ArrayList<Item> ingredientList = new ArrayList<Item>();

        ArrayList<String> ingredientListSplit = new ArrayList<String>(Arrays.asList(longListString.split(" separ, ")));
        for(int i = 0; i < ingredientListSplit.size(); i++){
            Item newItem = new Item("" , ingredientListSplit.get(i), ingredientListSplit.get(i+1), "" );
            ingredientList.add(newItem);
            i = i + 1;
        }
        return ingredientList;
    }


    /**
     * Create a recipe and adds it to the recipe list
     * Add name to to the names list and put the adapter name and recipe info
     */

    public void addRecipe(String name, String time, ArrayList ingredients, ArrayList instructions)
    {
        Recipe recipe = new Recipe("", name, time, instructions, ingredients);                                     //////// seting the id to "" may cause problems
        recipeName.add(name);
        recipeList.add(recipe);
        List<String> recipeinfo = addRecipeInfo(recipe);
        recipeAdapter.put(name , recipeinfo);
    }

    /**
     * Add Information/Child List to each of the Expandable List Header
     */

    public List<String> addRecipeInfo(Recipe recipe){
        List<String> recipeInfo = new ArrayList<String>();
        recipeInfo.add("(Time to make: "+recipe.getTime() + ")");
        recipeInfo.add("Check Ingredients");
        recipeInfo.add("Make Recipe");
        recipeInfo.add("Edit Recipe");
        recipeInfo.add("Delete Recipe");
        return recipeInfo;
    }

    /**
     * OnClick is a method to manage the buttons
     * and their onclick methods
     * @param v
     */

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_recipe:
                goToAddNewRecipe();
                Log.v(tag,"add button");
                break;
            case R.id.options:
                options();
                break;
        }
    }

    //Option method for the alert dialog box
    private void options() {
        /* Give the user sort options */
        android.support.v7.app.AlertDialog.Builder sort = new android.support.v7.app.AlertDialog.Builder(getView().getContext());

        sort.setTitle("Options")
                .setItems(R.array.optionRecipe, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        /* The 'which' argument contains the index position of the selected item */
                        if (which == 0) {
                            sortRecipeList();
                        }
                        if (which == 1) {
                            help();
                        }
                    }
                });

        android.support.v7.app.AlertDialog dialog = sort.create();
        dialog.show();
    }

    /**
     * Edit Recipe
     * Goes to the recipe
     */
    private void editRecipe(Recipe recipe)
    {
        //Set the static variables as the recipe name
        FragmentRecipeMaker.recipeName = recipe.getName();
        FragmentAddIngredients.ingredientsList = recipe.getIngredients();
        FragmentAddInstructions.instructionList = recipe.getInstruction();
        ArrayList<String> splitTime = new ArrayList<String>(Arrays.asList(recipe.getTime().split(" ")));
        FragmentRecipeMaker.recipeTime = splitTime.get(0);

        int timeType = 0;
        if(splitTime.get(1).equals("MIN"))
        {
            timeType = 1;
        }
        FragmentRecipeMaker.currentTimeType = timeType;

        // Search through the database to see if it's already there
        myDb.openDB();
        Cursor res = myDb.getAllRecipeData();
        while (res.moveToNext())
        {
            String recipeNameInDatabase = res.getString(1);
            if (recipe.getName().toLowerCase().matches(recipeNameInDatabase.toLowerCase()))
            {
                edit_flag_id = res.getString(0);
            }
        }
        myDb.close();
    }

    /**
     * To Go Add New Recipe fragment
     */
    private void goToAddNewRecipe()
    {
        FragmentRecipeMaker fragment = new FragmentRecipeMaker();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


    /**
     * Sort the recipes.
     */
    public void sortRecipeList()
    {
        if (recipeList.isEmpty()) {
            /* List is empty. Can't sort it */
            Toast.makeText(getView().getContext(),
                    "Your recipe book is empty!", Toast.LENGTH_LONG).show();
        }
        else
        {
            /* Give the user sort options */
            android.support.v7.app.AlertDialog.Builder sort = new android.support.v7.app.AlertDialog.Builder(getView().getContext());

            sort.setTitle("Sort By")
                    .setItems(R.array.sort_recipies_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {

                            /* The 'which' argument contains the index position of the selected item */
                            if (which == 0)
                            {
                                /* Sort alphabetically */
                                Collections.sort(recipeList, new Comparator<Recipe>()
                                {
                                    @Override
                                    public int compare(Recipe lhs, Recipe rhs) {
                                        Log.v("sort", lhs.getName() + " " + rhs.getName());
                                        Log.v("sort", "" + lhs.getName().compareTo(rhs.getName()));
                                        return lhs.getName().compareTo(rhs.getName());
                                    }
                                });

                                updateOnSort();
                            }
                            else if (which == 1)
                            {
                                /* Sort reverse alphabetically */
                                Collections.sort(recipeList, new Comparator<Recipe>()
                                {
                                    @Override
                                    public int compare(Recipe lhs, Recipe rhs) {

                                        return lhs.getName().compareTo(rhs.getName());
                                    }
                                });

                                Collections.reverse(recipeList);

                                updateOnSort();

                            }
                            else if (which == 2)
                            {
                                /* Sort by color - if have all items */
                                /* Sort alphabetically */
                                Collections.sort(recipeList, new Comparator<Recipe>()
                                {
                                    @Override
                                    public int compare(Recipe lhs, Recipe rhs) {
                                        Log.v("sort", "yo");
                                        String leftSide;
                                        String rightSide;

                                        //if (lhs.getHasAllIngredients() == false || rhs.getHasAllIngredients() == false)
                                        //    return 0;

                                        if (lhs.getHasAllIngredients())
                                            leftSide = "true";
                                        else
                                            leftSide = "false";

                                        //Log.v("sort", "" + lhs.getHasAllIngredients());

                                        if (rhs.getHasAllIngredients())
                                            rightSide = "true";
                                        else
                                            rightSide = "false";

                                        //Log.v("sort", "" + rhs.getHasAllIngredients());

                                        Log.v("sort", leftSide);
                                        Log.v("sort", rightSide);
                                        Log.v("sort", "" + rightSide.compareTo(leftSide));

                                        return leftSide.compareTo(rightSide);


                                        //return 0;

                                    }
                                });

                                Collections.reverse(recipeList);
                                updateOnSort();
                                //Log.v("sort", "" + recipeList.toString());

                            }
                            else if (which == 3)
                            {
                                /* Sort by cooking time */


                            }
                        }
                    });

            android.support.v7.app.AlertDialog dialog = sort.create();
            dialog.show();

        }
    }


    /**
     * When the user clicks a sort option, do the sort.
     * Delete all the database items and insert new ones that are sorted.
     */
    public void updateOnSort()
    {
        //OPEN DATAbase
        myDb.openDB();

        //DELETE ALL THAT'S THERE
        myDb.deleteAllRecipe();

        // GET ALL SORTED VALUES
        for (Recipe recipe : recipeList)
        {
            String name = recipe.getName();
            String time = recipe.getTime();
            String ingredients = FragmentRecipeMaker.itemListToString(recipe.getIngredients());
            String instructions = FragmentRecipeMaker.instructionListToString(recipe.getInstruction());

            //INSERT RECIPE INTO DATABASE
            myDb.insertRecipeData(name, time, ingredients, instructions);
        }

        myDb.close();

        // REFRESH
        FragmentRecipeList fragment = new FragmentRecipeList();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void colorCode()
    {
        // Get text views
        // Change their color based on boolean
        // if (owned_flag)


    }

    /**
     * Clone the array list and returns it
     * Mainly used for duplicating a temporary array list
     */

    private ArrayList<Item> cloneList(ArrayList<Item> list) throws CloneNotSupportedException {
        ArrayList<Item> clone = new ArrayList<Item>(list.size());
        for(Item item : list){
            clone.add((Item) item.clone());
        }
        return clone;
    }

    /**
     * View the entire list database
     */
    public void ViewAll()
    {
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Gets all the data and shows an error if nothing is found. */
                Cursor res = myDb.getAllRecipeData();
                if (res.getCount() == 0)
                {
                    showMessage("Error","Nothing found");
                    return;
                }

                /* Creates a modifiable string that we can add items to. */
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext())
                {
                    buffer.append("Id :" + res.getString(0) + "\n");
                    buffer.append("Name :" + res.getString(1) + "\n");
                    buffer.append("Qty :" + res.getString(2) + "\n");
                    buffer.append("Ingredients :" + res.getString(3) + "\n");
                    buffer.append("Instructions :" + res.getString(4) + "\n\n");
                }

                /* Show all data in an alert box.  */
                showMessage("Data", buffer.toString());
            }
        });
    }

    /**
     * Goes with show database list.
     * @param title
     * @param message
     */
    public void showMessage(String title, String message)
    {
        /* Alert box to show messages. */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    /**
     * Help button
     */
    public void help()
    {
        /* Alert box to show messages. */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("On the recipe list page, you can:");
        builder.setMessage("- Tap Add Recipe to create a new recipe. \n\n" +
                "- Tap a recipe name to see options for that specific recipe. \n\n" +
                "- Tap Check Ingredients to see required ingredients. \n\n" +
                "- Tap Make Recipe to see recipe instructions. \n\n" +
                "- Tap Options to see more options. \n\n" +
                "Recipes will be colored green when all required ingredients are in the inventory.");
        builder.show();
    }
}
