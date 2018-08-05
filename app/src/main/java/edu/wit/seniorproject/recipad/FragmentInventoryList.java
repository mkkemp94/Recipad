package edu.wit.seniorproject.recipad;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentInventoryList extends Fragment {

    /* Layout for the actual list */
    private ListView listView;

    /* Adapter for modifying list view */
    private ArrayAdapter<Item> adapter;

    /* List of Items created and added to by the user */
    private ArrayList<Item> inventoryList = new ArrayList<Item>();

    /* For user input */
    private EditText etAdd;
    private Button btnAdd;
    private Button btnOption;
    private Button btnCheckAll;

    /* List database */
    private DatabaseHelper myDb;

    /* For toast messages */
    private String toast;

    /* Booleans for item checked */
    static boolean checkAll = false;
    static boolean deselectAll = false;

    boolean isAnythingChecked;


    /* A hastable for the barcode scanner*/
    Hashtable<String, String> barCodeTable;


    public FragmentInventoryList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);

    }

    /**
     * 'On create' version for fragments
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Create the database */
        myDb = new DatabaseHelper(getContext());

        hideSoftKeyboard();

        /* Build list adapter */
        listAdapter();

        /* Populate list view */
        displayList();

        /**
         * User Input
         */
        etAdd = (EditText) getView().findViewById((R.id.et_add_ingredient));
        btnAdd = (Button) getView().findViewById((R.id.btn_add));
        btnOption = (Button) getView().findViewById(R.id.btn_option);
        btnCheckAll = (Button) getView().findViewById((R.id.btn_check_all));


        /* Scan an item and add it to the inventory */
        Button scan = (Button) getView().findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanFromFragment();
            }
        });


        /**
         * Add an item to the shopping list
         */
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                addToList();
            }
        });

        /**
         * Show options for sorting and clearing inventory
         */
        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                option();
            }
        });

        /**
         * Check/uncheck all items
         */
        btnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //checkAll = !checkAll;
                

                if (checkAll == false)
                    checkAll = true;

                else if (checkAll == true && deselectAll == false)
                    deselectAll = true;


                listAdapter();


                /**
                 * Reload fragment. Shopping list will rebuild from database.
                 * No direct modification of the shopping list is needed.
                 */

                FragmentInventoryList fragment = new FragmentInventoryList();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();

            }
        });

        barCodeTable = new Hashtable<String, String>();
        createDefaultBarcodeTable(barCodeTable);
    }

    /**
     * Preset barcodes. Add more here to recognize more barcodes.
     */
    private void createDefaultBarcodeTable(Hashtable<String, String> barCodeTable) {
        barCodeTable.put("305212750003", "Vaseline");
        barCodeTable.put("814914021903", "Cookies");
        barCodeTable.put("012000071744", "Orange Juice");
        barCodeTable.put("051500251621", "Vegetable Oil");
        barCodeTable.put("688267060380", "Apples");
        barCodeTable.put("041390011108", "Teriyaki Baste & Glaze");
        barCodeTable.put("600699661027", "Jet-Puffed Marshmallows");
        barCodeTable.put("04446307", "Honey Maid Gram Crackers");
        barCodeTable.put("03424005", "Hershey's Milk Chocolate");
        barCodeTable.put("688267070082", "Spaghetti");
        barCodeTable.put("688267052095", "Pasta Sauce");
    }

    /**
     * Build List Adapter
     */
    private void listAdapter()
    {
        adapter = new AdapterGroceryInventoryList(
                getView().getContext(), // context
                //R.layout.view_item,     // layout of item
                inventoryList,
                "Inventory",
                checkAll);          // items array

        listView = (ListView) getView().findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    /**
     * For barcode scanning
     */
    public void scanFromFragment()
    {
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    /**
     * Add database items to the list
     */
    public void displayList()
    {
        //Avoid duplications
        inventoryList.clear();

        //OPEN
        myDb.openDB();

        //RETRIEVE
        Cursor cursor = myDb.getAllInventoryNames();

        while (cursor.moveToNext())
        {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String qty = cursor.getString(2);
            String exp = cursor.getString(3);

            // Make a new item from data just retrieved from database
            Item item = new Item(id, name, qty, exp);
            inventoryList.add(item);

            // Notify the adapter
            adapter.notifyDataSetChanged();
        }

        //CLOSE DB
        myDb.close();
    }

    /**
     * Displays any toast message
     */
    private void displayToast()
    {
        if (getActivity() != null && toast != null)
        {
            Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
            toast = null;
        }
    }

    /**
     * For barcode scanning.
     * Once item is scanned, add it to the inventory database.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null)
        {
            String barCodeItem = null;
            if (result.getContents() == null) {
                //toast = "Cancelled from barcode scanner";
            } else {
                //toast = "Scanned from barcode scanner: " + result.getContents();
                barCodeItem = barCodeTable.get(result.getContents());
            }

            if (barCodeItem !=null && !barCodeItem.isEmpty())
            {
                toast = "Scanned from barcode scanner.";

                /* Make a new item. Pass it the name the user gave it */
                myDb.openDB();

                String itemQty = "1";
                String itemExp = "";

                // GO THROUGH INVENTORY DATABASE TABLE
                Cursor res = myDb.getAllInventoryData();

                while (res.moveToNext()) {

                    // Get current search item and set it to lowercase for no case sensitivity
                    String currentItemName = res.getString(1).toLowerCase();

                    if (currentItemName.matches(barCodeItem.toLowerCase())) {

                        // If the current quantity was never entered, set it to one
                        String currentItemQty = res.getString(2);
                        if (currentItemQty.matches("")) {
                            currentItemQty = "1";
                        }

                        // Cast to int so we can increment it
                        int qty = Integer.parseInt(currentItemQty);

                        qty++;

                        // Cast back to String and set it to the original item quantity value
                        itemQty = Integer.toString(qty);

                        // Get the old expiration date to retain it
                        itemExp = res.getString(3);

                        // Delete the old item. We're making a new one with incremented quantity
                        myDb.deleteInventoryItem(res.getString(0));
                    }
                }

                // INSERT SCANNED ITEM INTO INVENTORY
                myDb.insertInventoryData(barCodeItem, itemQty, itemExp);

                myDb.close();

                // REDISPLAY LIST
                displayList();
            }

            // At this point we may or may not have a reference to the activity
            displayToast();
        }
    }

    /**
     * Add new item to the shopping list, as long as the user typed something.
     */
    public void addToList()
    {
        // IF NO ITEM TYPED IN
        if (etAdd.getText().toString().matches(""))
        {
            /* User hasn't typed anything */
            Toast.makeText(getView().getContext(),
                    "Specify an item name", Toast.LENGTH_LONG).show();
        }
        else
        {
            //OPEN
            myDb.openDB();

            // Name of item to add (entered in) ... trim space off end if it exists
            String itemToAdd = etAdd.getText().toString().trim().toLowerCase();

            // And it's quantity and expiration (start off empty)
            String itemQty = "1";
            String itemExp = "";

            // Search through the database to see if it's already there
            Cursor res = myDb.getAllInventoryData();

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
                    int qty = Integer.parseInt(currentItemQty);

                    qty++;

                    // Cast back to String and set it to the original item quantity value
                    itemQty = Integer.toString(qty);

                    // Get the old expiration date to retain it
                    itemExp = res.getString(3);

                    // Delete the old item. We're making a new one with incremented quantity
                    myDb.deleteInventoryItem(res.getString(0));
                }
            }

            //INSERT NEW ITEM (or increment if it already existed... new itemQty value)
            long result = myDb.insertInventoryData(itemToAdd, itemQty, itemExp);

            if (result != -1)
            {
                Toast.makeText(getContext(), "Item added. Tap name to edit", Toast.LENGTH_LONG).show();

                /* Clear edit text for next input and hide keyboard */
                etAdd.setText("");
            }
            else
            {
                Toast.makeText(getContext(), "Data not inserted", Toast.LENGTH_LONG).show();
            }

            //CLOSE DB
            myDb.close();

            // HIDE KEYBOARD
            hideSoftKeyboard();

            /* Show updated list */
            displayList();
        }
    }

    /**
     * Clear the inventory list, as long as there is stuff in it.
     */
    public void clearInventory()
    {
        // CHECK IF ANYTHING IS CHECKED
        for (Item item : inventoryList)
        {
            if (item.isSelected())
                isAnythingChecked = true;
        }

        //IF NOTHING IS THERE
        if (inventoryList.isEmpty())
        {
            /* List is empty already */
            Toast.makeText(getView().getContext(),
                    "Your inventory list is empty!", Toast.LENGTH_LONG).show();
        }

        // CHECK IF ANYTHING IS CHECKED
        else if (isAnythingChecked == false)
        {
            Toast.makeText(getContext(), "No items are selected.",
                    Toast.LENGTH_LONG).show();
        }

        // THERE'S STUFF THERE, AND SOME STUFF ARE CHECKED
        else
        {
            /* Make sure this is what the user wants */
            AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());

            builder.setMessage("Clear selected items from your inventory?")
                    .setTitle("Clear Inventory")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            if (isAnythingChecked == true)
                            {
                                //OPEN
                                myDb.openDB();

                                // GO THROUGH EACH ITEM
                                for (Item item : inventoryList) {

                                    if (item.isSelected()) {

                                        // CURRENT ITEM VALUES
                                        String itemId = "";
                                        String itemName = item.getName();

                                        // INVENTORY CURSOR TO SEARCH INVENTORY
                                        Cursor inventory_res = myDb.getAllInventoryData();


                                        // SEARCH INVENTORY LIST TO SEE IF ITEM ALREADY EXISTS THERE
                                        while (inventory_res.moveToNext()) {

                                            // NAME OF CURRENT ITEM BEING LOOKED AT
                                            String inventoryItemName = inventory_res.getString(1);

                                            // IF THE ITEM IS FOUND
                                            if (inventoryItemName.toLowerCase().
                                                    matches(itemName.toLowerCase())) {

                                                // DELETE THE OLD INVENTORY ITEM
                                                myDb.deleteInventoryItem(inventory_res.getString(0));
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

                                FragmentInventoryList fragment = new FragmentInventoryList();
                                android.support.v4.app.FragmentTransaction fragmentTransaction =
                                        getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, fragment);
                                fragmentTransaction.commit();
                            }
                            // Not entirely necessary but just in case
                            else
                                Toast.makeText(getContext(), "No items are selected.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("No", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /**
     * Sort the items in the shopping list by alphabetical order.
     * Or by expiration date.
     */
    public void sortList()
    {
        // IF IT'S EMPTY
        if (inventoryList.isEmpty()) {
            /* List is empty. Can't sort it */
            Toast.makeText(getView().getContext(),
                    "Your shopping list is empty!", Toast.LENGTH_LONG).show();
        } else
        {
            /* Give the user sort options */
            android.support.v7.app.AlertDialog.Builder sort = new android.support.v7.app.AlertDialog.Builder(getView().getContext());

            sort.setTitle("Sort By")
                    .setItems(R.array.sort_groceries_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        /* The 'which' argument contains the index position of the selected item */
                            if (which == 0)
                            {
                            /* Sort alphabetically */
                                Collections.sort(inventoryList, new Comparator<Item>()
                                {
                                    @Override
                                    public int compare(Item lhs, Item rhs) {
                                        return lhs.getName().compareTo(rhs.getName());
                                    }
                                });
                            }
                            else if (which == 1)
                            {
                                /* Sort reverse alphabetically */
                                Collections.sort(inventoryList, new Comparator<Item>()
                                {
                                    @Override
                                    public int compare(Item lhs, Item rhs) {

                                        return lhs.getName().compareTo(rhs.getName());
                                    }
                                });

                                Collections.reverse(inventoryList);
                            }
                            else if (which == 2)
                            {
                                /* Sort by expiration*/
                                Collections.sort(inventoryList, new Comparator<Item>()
                                {
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
     * Button the user can click on the grocery list fragment to get to
     * Sort, Clear, and Help features
     */
    private void option() {
        /* Give the user sort options */
        android.support.v7.app.AlertDialog.Builder sort = new android.support.v7.app.AlertDialog.Builder(getView().getContext());

        sort.setTitle("Options")
                .setItems(R.array.optionInventory, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                            /* The 'which' argument contains the index position of the selected item */
                        if (which == 0) {
                            sortList();
                        }
                        if (which == 1) {
                            clearInventory();
                        }
                        if (which == 2) {
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
        builder.setTitle("On the inventory page, you can:");
        builder.setMessage("- Add items to the inventory list using the text field at the bottom of the screen. \n\n" +
                "- Update an item's quantity and expiration date by clicking an item name. \n\n" +
                "- Select the items to send to the inventory when shopping is complete. \n\n" +
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
