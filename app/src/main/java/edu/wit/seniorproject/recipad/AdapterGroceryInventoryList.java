package edu.wit.seniorproject.recipad;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kempm on 7/11/2016.
 */

class AdapterGroceryInventoryList extends ArrayAdapter<Item> {

    /* The grocery list is stored here */
    private ArrayList list = new ArrayList<Item>();

    /* The item to be edited */
    private Item thisItem;

    /* Context! */
    private Context context;

    /* id of the item */
    private String id;

    /* Database to work with */
    private DatabaseHelper myDb;

    // Grocery? Inventory?
    private String listType;

    /* Item edit window input  */
    private EditText edit_name;
    private EditText edit_qty;
    private TextView edit_exp;
    private Button btnDate;

    /* For use in the update item method */
    private String update_name;
    private String update_qty;
    private String update_exp;

    /* Calender for setting expiration date */
    private Calendar expirationDate = Calendar.getInstance();
    private Calendar currentDate = Calendar.getInstance();

    /* Current year, month, day */
    private int startYear = currentDate.get(Calendar.YEAR);
    private int startMonth = currentDate.get(Calendar.MONTH) + 1;
    private int startDay = currentDate.get(Calendar.DAY_OF_MONTH);

    /* Current day in M/dd/yyyy format */
    private String currentDay = startMonth + "/" + startDay + "/" + startYear;

    /* Month day and year of item expiration, set by user */
    int cday, cmonth, cyear;

    /* For check all buttons */
    boolean checkAll;
    boolean deselectAll;


    private final boolean[] mCheckedState;


    /**
     * Constructor for grocery list
     *
     */
    public AdapterGroceryInventoryList(Context context, ArrayList<Item> list, String listType, boolean checkAll) {
        super(context, R.layout.view_item, list);
        this.list = list;
        this.listType = listType;
        this.context = context;
        this.checkAll = checkAll;

        mCheckedState = new boolean[list.size()];
    }


    /**
     * Gets individual items.
     */
    @Override
    public Item getItem(int position) {
        return (Item) list.get(position);
    }


    /**
     * Size of the grocery list.
     */
    @Override
    public int getCount() {
        return this.list.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Set the layout for each item
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.view_item, parent, false);

        // Position of each item ... each is gone through on activity create
        thisItem = getItem(position);

        // Position, view, and parent for passing later
        final int itemPosition = position;
        final View itemView = convertView;
        final ViewGroup itemParent = parent;

        // Set up text views, button, and image view for view_item
        TextView item_name = (TextView) customView.findViewById(R.id.txt_name);
        TextView item_qty = (TextView) customView.findViewById(R.id.txt_qty);
        TextView item_exp = (TextView) customView.findViewById(R.id.txt_exp);
        CheckBox item_chkBox = (CheckBox) customView.findViewById(R.id.checkBox);
        Button remove = (Button) customView.findViewById(R.id.remove_item);

        /**
         * Set text view font colors.
         * If expiration date is > 3 days away, set green.
         * If expiration date is three days away or under, color orange.
         * If expiration date is past, color red.
         * Else color green.
         */
        if (thisItem.getExpiration().matches(""))
        {
            Log.v("hi", "woo");
        }
        else
        {
            expirationColorCheck(item_name, item_exp, thisItem);
        }


        /**
         * Checks a checkbox if it is empty.
         * Otherwise, uncheck it and set checkAll variable in Grocery List to false.
         */
        item_chkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onItemCheck(itemPosition, v);
            }
        });

//        onItemCheck(position, customView);

        /**
         * On name press, open a dialog so user can edit name, qty, and exp of item.
         */
        item_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemDetails(itemPosition, itemParent);
            }
        });


        /**
         * On name press, open a dialog so user can edit name, qty, and exp of item.
         */
        item_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemDetails(itemPosition, itemParent);
            }
        });




        // TODO FIX THIS
        /**
         * If the user has pressed the check all button
         */
        if (checkAll == true)
        {
            thisItem.setSelected(true);
        }
        if (FragmentGroceryList.deselectAll == true)
        {
            for (int i = 0; i < list.size(); i++) {
                getItem(i).setSelected(false);
            }

            FragmentGroceryList.deselectAll = false;
            FragmentGroceryList.checkAll = false;

            // CULPRIT
            //thisItem.setSelected(false);
        }
        if (FragmentInventoryList.deselectAll == true)
        {
            for (int i = 0; i < list.size(); i++) {
                getItem(i).setSelected(false);
            }

            FragmentInventoryList.deselectAll = false;
            FragmentInventoryList.checkAll = false;
        }


        /**
         * On X button press, remove the item from the list.
         */
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                removeItem(itemPosition);
            }
        });

        //Toast.makeText(getContext(), thisItem.getName() + " Checked = " + thisItem.isSelected(), Toast.LENGTH_SHORT).show();
        //item_chkBox.setChecked(thisItem.isSelected());


        if (item_chkBox.isShown())
        {
            item_chkBox.setChecked(true);
        }

        // Show the correct values in the ui
        item_name.setText(thisItem.getName());
        item_qty.setText(thisItem.getQty());
        item_exp.setText(thisItem.getExpiration());
        item_chkBox.setChecked(thisItem.isSelected());


        //Log.v("check", "" + thisItem.getName() + thisItem.isSelected());


//        CheckBox result = (CheckBox)convertView;
//        if (result == null) {
//            result = new CheckBox(context);
//        }
 //       item_chkBox.setChecked(mCheckedState[position]);
        //return result;

        return customView;
    }

    /**
     * Change the color of an item to green if it has an expiration date
     * but that date is far away.
     * Else to orange if it's close (3 days away),
     * else red if expired.
     */
    private void expirationColorCheck(TextView item_name, TextView item_exp, Item item)
    {
        // Change the item expiration date and the current date format into one that
        // can be subtracted. To find the days between.
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.ENGLISH);
        Date expires = null;
        Date now = null;

        try
        {
            expires = dateFormat.parse(item.getExpiration());
            now = dateFormat.parse(currentDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // GET DAYS BETWEEN
        long dayCount = (expires.getTime() - now.getTime())/(24*60*60*1000);

        // COLOR CODE ITEM NAME
        if (dayCount > 3) {
            item_name.setTextColor(context.getResources().getColor(R.color.colorGreen));
        } else if (dayCount > 0 && dayCount <= 3) {
            item_name.setTextColor(context.getResources().getColor(R.color.colorOrange));
        } else if (dayCount <= 0) {
            item_name.setTextColor(context.getResources().getColor(R.color.colorDarkRed));
        } else {
            item_name.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
        }
    }


    /**
     * Checks a checkbox if it is empty.
     * Otherwise, uncheck it and set checkAll variable in Grocery List to false.
     */
    public void onItemCheck(int itemPosition, View v)
    {
        // This is the item from before.
        // Setting the position ensures that we're updating the same one.
        thisItem = (Item) getItem(itemPosition);
        id = thisItem.getId();

        // is item checked?
        // Clicking makes check/uncheck
        if (((CheckBox) v).isChecked()) {
            Toast.makeText(context, thisItem.getName() + " is checked!", Toast.LENGTH_SHORT).show();
            thisItem.setSelected(true);
            Log.v("check", "" + thisItem.getName() + thisItem.isSelected());
        }
        else
        {
            Toast.makeText(context, "Item is unchecked!", Toast.LENGTH_SHORT).show();
            thisItem.setSelected(false);
            checkAll = false;
            FragmentGroceryList.checkAll = false;
            FragmentInventoryList.checkAll = false;
        }

       // list.get(itemPosition).(buttonView.isChecked());

       // v.isShown()
    }


    /**
     * Creates a dialog so that user can edit item details.
     * Takes the position for pressing, and the parent to inflate.
     */
    public void updateItemDetails(int position, ViewGroup parent)
    {
        // Set the layout for the input dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.input_item_details, parent, false);

        // Position of item (to use when update button is clicked)
        // This makes sure it's referring to the same item.
        final int updatePosition = position;

        /** This is the specific item we're updating. Get its position and id.
         * MUST BE DONE HERE
         */
        thisItem = (Item) getItem(position);
        id = thisItem.getId();

        // Set up the edit text fields in dialog
        edit_name = (EditText) view.findViewById(R.id.item_name);
        edit_qty = (EditText) view.findViewById(R.id.item_qty);
        edit_exp = (TextView) view.findViewById(R.id.item_expiration);
        btnDate = (Button) view.findViewById(R.id.btn_date);

        /* Populate POP UP with item name */
        edit_name.setText(thisItem.getName());
        edit_qty.setText(thisItem.getQty());
        edit_exp.setText(thisItem.getExpiration());

        // Clicking the date button opens a calender dialog to set date
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new DatePickerDialog(getContext(), dateListener,
                       startYear, startMonth-1, startDay).show();
            }
        });

        // Start the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        /**
         * On update set the name, qty, and exp date of item to new values.
         */
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // This is the item from before.
                // Setting the position ensures that we're updating the same one.
                thisItem = (Item) getItem(updatePosition);
                id = thisItem.getId();

                // Set up the edit texts for dialog
                update_name = edit_name.getText().toString();
                update_qty = edit_qty.getText().toString();
                update_exp = edit_exp.getText().toString();


                // Update the database
                myDb = new DatabaseHelper(getContext());

                myDb.openDB();

                // IF GROCERY UPDATE
                if (listType.matches("Grocery"))
                    myDb.updateGroceryData(id, update_name, update_qty, update_exp);

                // IF INVENTORY UPDATE
                else if (listType.matches("Inventory"))
                    myDb.updateInventoryData(id, update_name, update_qty, update_exp);

                myDb.close();

                // Update the UI
                thisItem.setName(update_name);
                thisItem.setQty(update_qty);
                thisItem.setExpiration(update_exp);

                notifyDataSetChanged();

                Log.v("expires", "Item update expiration is now " + thisItem.getExpiration());

            }
        });

        /**
         * On cancel do nothing.
         */
        builder.setNegativeButton("Cancel", null).create();

        // Set the view and show dialog
        builder.setView(view);
        builder.show();
    }


    /**
     * Remove the item at the given position on X button press.
     * First removes from database, then from the ui.
     */
    public void removeItem(int position)
    {
        final int removePosition = position;

        /** This is the item we're working with. Get it's position and id.
         * MUST BE DONE HERE
         */
        thisItem = (Item) getItem(removePosition);

        android.support.v7.app.AlertDialog.Builder remove = new android.support.v7.app.AlertDialog.Builder(getContext());
        remove.setTitle("Remove item")
                .setMessage("Are you sure you want to remove " + thisItem.getName() + "?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int clickedId) {

                        /** This is the item we're working with. Get it's position and id.
                         * MUST BE DONE HERE
                         */
                        thisItem = (Item) getItem(removePosition);
                        id = thisItem.getId();


                        // Set up database
                        myDb = new DatabaseHelper(getContext());
                        myDb.openDB();

                        // DELETE BY ID
                        Integer deletedRow = 0;

                        //IF ACCESSING FROM THE GROCERY LIST, UPDATE THE GROCERY TABLE
                        if (listType == "Grocery") {
                            deletedRow =
                                    myDb.deleteGroceryItem(id);
                        }
                        //IF ACCESSING FROM THE INVENTORY LIST, UPDATE THE INVENTORY TABLE
                        else if (listType == "Inventory")
                        {
                            deletedRow =
                                    myDb.deleteInventoryItem(id);
                        }

                        // Check if data was deleted or not
                        if (deletedRow > 0)
                            Toast.makeText(getContext(), "Data deleted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getContext(), "Data not deleted", Toast.LENGTH_LONG).show();

                        // Close database
                        myDb.close();


                        // Removes the list item from ui
                        list.remove(removePosition);
                        notifyDataSetChanged();
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

    /**
     * For showing the calender dialog
     */
    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            cday = dayOfMonth;
            cmonth = monthOfYear + 1;
            cyear = year;

            // SET EXPIRATION DATE TO THE DATE SELECTED BY USER
            expirationDate.set(cyear, cmonth, cday);

            // APPLY TO ITEM
            thisItem.setMyExpiration(expirationDate);

            // SET TEXT IN DIALOG
            edit_exp.setText(cmonth + "/" + cday + "/" + cyear);
        }
    };

}