package edu.wit.seniorproject.recipad;


import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGroceryList extends Fragment {

    /* Layout for the actual list */
    private ListView listView;

    /* Adapter for modifying list view */
    private ArrayAdapter<Item> adapter;

    /* List of Items created and added to by the user */
    private ArrayList<Item> shoppingList = new ArrayList<Item>();

    /* For user input */
    private EditText etAdd;
    private Button btnAdd;
    private Button btnOption;
    private Button btnFinishShopping;
    private Button btnCheckAll;

    /* List database */
    DatabaseHelper myDb;

    /* Booleans for item checked */
    static boolean checkAll = false;
    static boolean deselectAll = false;

    boolean isAnythingChecked;


    public FragmentGroceryList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grocery_list, container, false);
    }

    /**
     * 'On create' version for fragments
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Create the database */
        myDb = new DatabaseHelper(getContext());

        /* Build list adapter */
        listAdapter();

        /* Populate list view */
        displayList();


        /**
         * User Input
         */
        etAdd = (EditText) getView().findViewById((R.id.et_add_item));

        btnAdd = (Button) getView().findViewById((R.id.btn_add));
        btnFinishShopping = (Button) getView().findViewById((R.id.btn_finish));
        btnOption = (Button) getView().findViewById((R.id.btn_option));
        btnCheckAll = (Button) getView().findViewById((R.id.btn_check_all));


        /**
         * Add an item to the shopping list
         */
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToList();
            }
        });

        /**
         * Clear the entire shopping list
         */
        btnFinishShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishShopping();

            }
        });


        /**
         * Show options for sorting, clearing, and saving a shopping list
         */
        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option();
            }
        });


        /**
         * Check/uncheck all items
         */
        btnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkAll == false)
                    checkAll = true;
                else if (checkAll == true && deselectAll == false)
                    deselectAll = true;
                //else
                //    deselectAll = true;

                listAdapter();


                /**
                 * Reload fragment. Shopping list will rebuild from database.
                 * No direct modification of the shopping list is needed.
                 */

                FragmentGroceryList fragment = new FragmentGroceryList();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();


            }
        });
    }


    /**
     * Build List Adapter
     */
    private void listAdapter() {
        adapter = new AdapterGroceryInventoryList(
                getView().getContext(), // context
                shoppingList,           // items array
                "Grocery",              // Type of list
                checkAll);              // Tell whether to check all items or not

        listView = (ListView) getView().findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

    /**
     * Add database items to the list view for displaying
     */
    public void displayList() {
        //Avoid duplications
        shoppingList.clear();

        //OPEN
        myDb.openDB();

        //RETRIEVE
        Cursor cursor = myDb.getAllGroceryNames();

        while (cursor.moveToNext()) {
            // ITEM VALUES FROM DATABASE
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String qty = cursor.getString(2);
            String exp = cursor.getString(3);

            // ADD ITEM TO LIST VIEW
            Item item = new Item(id, name, qty, exp);
            shoppingList.add(item);

            adapter.notifyDataSetChanged();
        }

        //CLOSE DB
        myDb.close();
    }

    /**
     * Add new item to the shopping list, as long as the user typed something.
     */
    public void addToList() {
        if (etAdd.getText().toString().matches("")) {
            /* User hasn't typed anything */
            Toast.makeText(getView().getContext(),
                    "Specify an item name", Toast.LENGTH_LONG).show();
        } else {
            //OPEN
            myDb.openDB();

            // Name of item to add (entered in) ... trim space off end
            String itemToAdd = etAdd.getText().toString().trim().toLowerCase();

            // And it's quantity and exp
            String itemQty = "1";
            String itemExp = "";

            // Search through the database to see if it's already there
            Cursor res = myDb.getAllGroceryData();

            while (res.moveToNext()) {

                // Get current search item and set it to lowercase for no case sensitivity
                String currentItemName = res.getString(1).toLowerCase();

                // If the name of an item already exists in the database, update the quantity
                if (currentItemName.matches(itemToAdd)) {

                    // If the current quantity was never entered, set it to one
                    String currentItemQty = res.getString(2);
                    if (currentItemQty.matches("")) {
                        currentItemQty = "1";
                    }

                    // Cast to int so we can increment it
                    int qty = Integer.parseInt(currentItemQty) + 1;

                    // Cast back to String and set it to the original item quantity value
                    itemQty = Integer.toString(qty);

                    // Get the old expiration date to retain it
                    itemExp = res.getString(3);

                    // Delete the old item. We're making a new one with incremented quantity
                    myDb.deleteGroceryItem(res.getString(0));
                }
            }

            //INSERT NEW ITEM (or increment if it already existed... new itemQty value)
            long result = myDb.insertGroceryData(etAdd.getText().toString(), itemQty, itemExp);

            if (result != -1) {
                Toast.makeText(getContext(), "Item added. Tap name to edit", Toast.LENGTH_SHORT).show();

                /* Clear edit text for next input and hide keyboard */
                etAdd.setText("");
            } else {
                Toast.makeText(getContext(), "Data not inserted", Toast.LENGTH_LONG).show();
            }

            //CLOSE DB
            myDb.close();

            //HIDE THE SOFT KEYBOARD
            hideSoftKeyboard();

            /* Show updated list */
            displayList();
        }
    }

    /**
     * Sort the items in the shopping list by alphabetical order.
     * Or by expiration date.
     */
    public void sortList() {
        if (shoppingList.isEmpty()) {
            /* List is empty. Can't sort it */
            Toast.makeText(getView().getContext(),
                    "Your shopping list is empty!", Toast.LENGTH_LONG).show();
        } else {
            /* Give the user sort options */
            android.support.v7.app.AlertDialog.Builder sort = new android.support.v7.app.AlertDialog.Builder(getView().getContext());

            sort.setTitle("Sort By")
                    .setItems(R.array.sort_groceries_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            /* The 'which' argument contains the index position of the selected item */
                            if (which == 0) {
                            /* Sort alphabetically */
                                Collections.sort(shoppingList, new Comparator<Item>() {
                                    @Override
                                    public int compare(Item lhs, Item rhs) {
                                        return lhs.getName().compareTo(rhs.getName());
                                    }
                                });
                            } else if (which == 1) {
                                /* Sort reverse alphabetically */
                                Collections.sort(shoppingList, new Comparator<Item>() {
                                    @Override
                                    public int compare(Item lhs, Item rhs) {

                                        return lhs.getName().compareTo(rhs.getName());
                                    }
                                });

                                Collections.reverse(shoppingList);
                            } else if (which == 2) {
                                /* Sort by expiration*/
                                Collections.sort(shoppingList, new Comparator<Item>() {
                                    public int compare(Item o1, Item o2) {
                                        if (o1.getExpiration() == null || o2.getExpiration() == null)
                                            return 0;
                                        return o1.getExpiration().compareTo(o2.getExpiration());
                                    }
                                });

                            }

                            /* Notify Adapter */
                            adapter.notifyDataSetChanged();
                        }
                    });

            android.support.v7.app.AlertDialog dialog = sort.create();
            dialog.show();

            // Hide keyboard
            hideSoftKeyboard();
        }
    }

    /**
     * Clear the shopping list, as long as there is stuff in it.
     */
    public void clearShoppingList() {
        for (Item item : shoppingList) {
            if (item.isSelected())
                isAnythingChecked = true;
        }

        if (shoppingList.isEmpty()) {
             /* List is empty already */
            Toast.makeText(getView().getContext(),
                    "Your inventory list is empty!", Toast.LENGTH_LONG).show();
        }

        // CHECK IF ANYTHING IS CHECKED
        else if (isAnythingChecked == false) {
            Toast.makeText(getContext(), "No items are selected.",
                    Toast.LENGTH_LONG).show();
        } else {
             /* Make sure this is what the user wants */
            AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());

            builder.setMessage("Clear selected items from your shopping list?")
                    .setTitle("Clear Inventory")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            if (isAnythingChecked == true) {
                                //OPEN
                                myDb.openDB();

                                // GO THROUGH EACH ITEM
                                for (Item item : shoppingList) {

                                    if (item.isSelected()) {

                                        // CURRENT ITEM VALUES
                                        String itemId = "";
                                        String itemName = item.getName();

                                        // INVENTORY CURSOR TO SEARCH INVENTORY
                                        Cursor grocery_res = myDb.getAllGroceryData();


                                        // SEARCH INVENTORY LIST TO SEE IF ITEM ALREADY EXISTS THERE
                                        while (grocery_res.moveToNext()) {

                                            // NAME OF CURRENT ITEM BEING LOOKED AT
                                            String groceryItemName = grocery_res.getString(1);

                                            // IF THE ITEM IS FOUND
                                            if (groceryItemName.toLowerCase().
                                                    matches(itemName.toLowerCase())) {

                                                // DELETE THE OLD INVENTORY ITEM
                                                myDb.deleteGroceryItem(grocery_res.getString(0));
                                            }
                                        }
                                    }
                                }

                                //CLOSE DB
                                myDb.close();


                                /**
                                 * Reload fragment. Shopping list will rebuild from database.
                                 * No direct modification of the shopping list is needed.
                                 */

                                FragmentGroceryList fragment = new FragmentGroceryList();
                                android.support.v4.app.FragmentTransaction fragmentTransaction =
                                        getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, fragment);
                                fragmentTransaction.commit();
                            } else
                                Toast.makeText(getContext(), "No items are selected.", Toast.LENGTH_LONG).show();

                        }
                    })
                    .setNegativeButton("No", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /**
     * Add checked items to the inventory.
     */
    public void finishShopping()
    {
        isAnythingChecked = false;

        for (Item item : shoppingList) {
            if (item.isSelected())
                isAnythingChecked = true;
        }

        // CHECK IF LIST IS EMPTY
        if (shoppingList.isEmpty()) {
            Toast.makeText(getView().getContext(),
                    "Your shopping list is empty!", Toast.LENGTH_LONG).show();
        }

        // CHECK IF ANYTHING IS CHECKED
        else if (isAnythingChecked == false) {
            Toast.makeText(getContext(), "No items are selected.",
                    Toast.LENGTH_LONG).show();
        }

        // IF IT IS NOT EMPTY, PROCEED
        else {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getView().getContext());

            builder.setMessage("Add selected shopping items to your inventory?")
                    .setTitle("Finish Shopping")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (isAnythingChecked == true)
                                moveToInventory();

                        }
                    })
                    .setNegativeButton("No", null);

            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    /**
     * Go through each item in the shopping list.
     * If an item is selected, move it to the inventory.
     * If the item already exists in the inventory, update quantity.
     */
    public void moveToInventory() {
        //OPEN DATABASE
        myDb.openDB();

        // GO THROUGH EACH ITEM
        for (Item item : shoppingList) {
            if (item.isSelected()) {

                // CURRENT ITEM VALUES
                String itemId = "";
                String itemName = item.getName();
                String itemQty = item.getQty();
                String itemExp = item.getExpiration();


                // INVENTORY CURSOR TO SEARCH INVENTORY
                Cursor inventory_res = myDb.getAllInventoryData();


                // GROCERY CURSOR TO SEARCH GROCERY
                Cursor grocery_res = myDb.getAllGroceryData();


                // SEARCH SHOPPING LIST FOR CURRENT ITEM NAME
                while (grocery_res.moveToNext()) {
                    // SAVE ITEM ID FOR LATER IF FOUND
                    if (item.getName().matches(grocery_res.getString(1))) {
                        itemId = grocery_res.getString(0);
                    }
                }


                // SEARCH INVENTORY LIST TO SEE IF ITEM ALREADY EXISTS THERE
                while (inventory_res.moveToNext()) {
                    // NAME OF CURRENT ITEM BEING LOOKED AT
                    String inventoryItemName = inventory_res.getString(1);

                    // IF THE ITEM IS FOUND
                    if (inventoryItemName.toLowerCase().
                            matches(itemName.toLowerCase())) {

                        // GET THE ITEM QUANTITY
                        String stringGroceryQty = item.getQty();
                        if (stringGroceryQty.matches("")) {
                            // MAKE IT 1 IF NO QUANTITY EXISTS
                            stringGroceryQty = "1";
                        }

                        // GET THE INVENTORY ITEM'S QUANTITY
                        String stringInventoryQty = inventory_res.getString(2);
                        if (stringInventoryQty.matches("")) {
                            // MAKE IT 1 IF NO QUANTITY EXISTS
                            stringInventoryQty = "1";
                        }

                        // MAKE BOTH QUANTITIES INTEGERS
                        int groceryQty = Integer.parseInt(stringGroceryQty);
                        int inventoryQty = Integer.parseInt(stringInventoryQty);

                        // ADD TOTAL QUANTITY AND CONVERT BACK TO STRING
                        itemQty = Integer.toString(inventoryQty + groceryQty);

                        // DELETE THE OLD INVENTORY ITEM
                        myDb.deleteInventoryItem(inventory_res.getString(0));
                    }
                }

                // DELETE ITEM FROM DATABASE
                myDb.deleteGroceryItem(itemId);

                // ADD ITEM TO INVENTORY
                myDb.insertInventoryData(itemName, itemQty, itemExp);
            }


            /**
             * Reload fragment. Shopping list will rebuild from database.
             * No direct modification of the shopping list is needed.
             */

            FragmentGroceryList fragment = new FragmentGroceryList();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        }

        //CLOSE DB
        myDb.close();
    }


    /**
     * User can save their shopping list to the database and load it at any time.
     */
    public void customSave()
    {
        if (!shoppingList.isEmpty())
        {
            // OPEN DATABASE
            myDb.openDB();

            long result = 0;

            myDb.deleteAllSaved();

            // GO THROUGH EACH ITEM
            for (Item item : shoppingList)
            {
                String itemName = item.getName();
                String itemQty = item.getQty();
                String itemExp = item.getExpiration();

                // ADD ITEM TO SAVED TABLE
                result = myDb.insertSavedData(itemName, itemQty, itemExp);
            }

            if (result != -1)
            {
                Toast.makeText(getContext(), "Grocery list saved", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
            }

            //CLOSE DB
            myDb.close();
        }
    }


    /**
     * Load that saved list form the databe.
     */
    public void customLoad()
    {
        // OPEN DATABASE
        myDb.openDB();

        long result = 0;

        // CURSOR TO SEARCH SAVED LIST
        Cursor saved_res = myDb.getAllSavedData();

        while (saved_res.moveToNext())
        {
            // EACH SAVED ITEM...
            String itemName = saved_res.getString(1);
            String itemQty = saved_res.getString(2);
            String itemExp = saved_res.getString(3);


            // GO THROUGH SHOPPING LIST AND SEE IF IT ALREADY EXISTS
            for (Item item : shoppingList)
            {
                // ALREADY EXISTS!
                if (item.getName().matches(itemName))
                {
                    // GET ID OF ITEM IN GROCERY DATABASE FOR UPDATING / DELETING
                    String itemId;

                    // UPDATE QTY
                    String newQty = String.valueOf(Integer.parseInt(item.getQty()) + Integer.parseInt(itemQty));

                    // GROCERY CURSOR TO SEARCH GROCERY
                    Cursor grocery_res = myDb.getAllGroceryData();

                    // LOOK FOR THE RIGHT DATABASE ENTRY
                    while (grocery_res.moveToNext())
                    {
                        // IF FOUND
                        if (item.getName().matches(grocery_res.getString(1)))
                        {
                            // GET OLD ITEM ID
                            itemId = grocery_res.getString(0);

                            // DELETE THE OLD ITEM
                            myDb.deleteGroceryItem(itemId);
                        }
                    }

                    itemQty = newQty;

                }
            }

            // INSERT THE LOADED LIST INTO THE GROCERY LIST DATABASE
            result = myDb.insertGroceryData(itemName, itemQty, itemExp);
        }

        if (result != -1) {
            Toast.makeText(getContext(), "Custom list loaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
        }

        displayList();

        //CLOSE DB
        myDb.close();
    }

    /**
     * Button the user can click on the grocery list fragment to get to
     * Save, Load, Sort, Clear, and Help features
     */
    private void option() {
        /* Give the user sort options */
        android.support.v7.app.AlertDialog.Builder sort = new android.support.v7.app.AlertDialog.Builder(getView().getContext());

        sort.setTitle("Options")
                .setItems(R.array.optionShopping, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                            /* The 'which' argument contains the index position of the selected item */
                        if (which == 0) {
                            customSave();
                        }
                        if (which == 1) {
                            customLoad();
                        }
                        if (which == 2) {
                            sortList();
                        }
                        if (which == 3) {
                            clearShoppingList();
                        }
                        if (which == 4) {
                            help();
                        }
                    }
                });

        android.support.v7.app.AlertDialog dialog = sort.create();
        dialog.show();
    }


    /**
     * Help button
     */
    public void help()
    {
        /* Alert box to show messages. */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("On the shopping list page, you can:");
        builder.setMessage("- Add items to the shopping list using the text field at the bottom of the screen. \n\n" +
                "- Update an item's quantity and expiration date by clicking that item's name. \n\n" +
                "- Select items to send to the inventory when shopping is complete. \n\n" +
                "- Tap options to see more options. \n\n" +
                "Items near their expiration date will turn red.");
        builder.show();
    }


    /**
     * Hides the keyboard.
     */
    private void hideSoftKeyboard()
    {
        if (getActivity().getCurrentFocus()!= null && getActivity().getCurrentFocus() instanceof EditText)
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etAdd.getWindowToken(), 0);
        }
    }

}