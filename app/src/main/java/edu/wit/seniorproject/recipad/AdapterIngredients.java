package edu.wit.seniorproject.recipad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by kempm on 7/17/2016.
 */
public class AdapterIngredients extends ArrayAdapter<Item>
{

    /* The ingredients list is stored here */
    private ArrayList list = new ArrayList<Item>();

    /* Context! */
    private Context context;

    /* The ingredient to be edited */
    private Item thisItem;

    /* id of the item */
    private String id;

    /**
     * Constructor
     */
    public AdapterIngredients(Context context, ArrayList<Item> list) {
        super(context, R.layout.view_ingredient, list);
        this.list = list;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Set the layout for each item
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.view_ingredient, parent, false);

        // Position, view, and parent for passing later
        final int itemPosition = position;
        final View itemView = convertView;
        final ViewGroup itemParent = parent;

        // Position of each item ... each is gone through on activity create
        thisItem = getItem(position);

        TextView ingredientName = (TextView) customView.findViewById(R.id.txt_ingredient_name);
        TextView ingredientQty = (TextView) customView.findViewById(R.id.txt_ingredient_qty);
        Button remove = (Button) customView.findViewById(R.id.remove_item);


        /**
         * On name press, open a dialog so user can edit name, qty, and exp of item.
         */
        ingredientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemDetails(itemPosition, itemParent);
            }
        });

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


        Log.v("hi", thisItem.getName());

        // Show the correct values in the ui
        ingredientName.setText(thisItem.getName());
        ingredientQty.setText(thisItem.getQty());

        return customView;
    }

    /**
     * Creates a dialog so that user can edit item details.
     * Takes the position for pressing, and the parent to inflate.
     */
    public void updateItemDetails(int position, ViewGroup parent)
    {
        // Set the layout for the input dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.input_ingredient, parent, false);

        // Position of item (to use when update button is clicked)
        // This makes sure it's referring to the same item.
        final int updatePosition = position;

        /**
         * This is the specific item we're updating. Get its position and id.
         */
        thisItem = (Item) getItem(position);
        id = thisItem.getId();

        // Set up the edit text fields in dialog
        final EditText edit_name = (EditText) view.findViewById(R.id.item_name);
        final EditText edit_qty = (EditText) view.findViewById(R.id.item_qty);

        /* Populate POP UP with item name */
        edit_name.setText(thisItem.getName());
        edit_qty.setText(thisItem.getQty());

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

                Log.v("updatez", thisItem.getName());

                // Set up the edit texts for dialog
                String update_name = edit_name.getText().toString();
                String update_qty = edit_qty.getText().toString();

                // Update the UI
                thisItem.setName(update_name);
                thisItem.setQty(update_qty);

                notifyDataSetChanged();
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

                        // Removes the list item from ui
                        list.remove(removePosition);
                        notifyDataSetChanged();
                    }
                })

                /**
                 * On cancel do nothing.
                 */
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        android.support.v7.app.AlertDialog dialog = remove.create();
        dialog.show();
    }
}
