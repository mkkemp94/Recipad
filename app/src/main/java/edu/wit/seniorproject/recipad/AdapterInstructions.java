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

import java.util.ArrayList;

/**
 * Created by kempm on 7/17/2016.
 */
public class AdapterInstructions extends ArrayAdapter<Instruction> {

    /* The instruction list is stored here */
    private ArrayList list = new ArrayList<Instruction>();

    /* Context! */
    private Context context;

    /* The ingredient to be edited */
    private Item thisItem;

    //private String thisInstruction;
    private Instruction thisInstruction;

    /* id of the item */
    private String id;

    /* Step attributes*/
    String thisStep;
    String thisHr;
    String thisMin;
    String thisSec;

    /**
     * Constructor
     */
    public AdapterInstructions(Context context, ArrayList<Instruction> list) {
        super(context, R.layout.view_instruction, list);
        this.list = list;
        this.context = context;
    }

    /**
     * Gets individual items.
     */
    @Override
    public Instruction getItem(int position) {
        return (Instruction) list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Set the layout for each item
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.view_instruction, parent, false);

        // Position, view, and parent for passing later
        final int itemPosition = position;
        final View itemView = convertView;
        final ViewGroup itemParent = parent;


        // Initialize text and butoon
        TextView txtInstructionName = (TextView) customView.findViewById(R.id.txt_instruction_name);
        TextView txtInstructionTime = (TextView) customView.findViewById(R.id.txt_instruction_time);
        Button remove = (Button) customView.findViewById(R.id.remove_item);

        // Individual instruction
        thisInstruction = getItem(position);

        // Attributes
        thisStep = thisInstruction.getStep();
        thisHr = thisInstruction.getHr();
        thisMin = thisInstruction.getMin();
        thisSec = thisInstruction.getSec();


        /**
         * On name press, open a dialog so user can edit name, qty, and exp of item.
         */
        txtInstructionName.setOnClickListener(new View.OnClickListener() {
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


        // Show the correct values in the ui
        txtInstructionName.setText((position+1) + ". " + thisInstruction.getStep());

        // IF NO TIME SET, DON'T SHOW TIME
        if (thisInstruction.getHr().matches("00") && thisInstruction.getMin().matches("00") && thisInstruction.getSec().matches("00"))
            txtInstructionTime.setText("");
        else
            txtInstructionTime.setText(thisInstruction.getHr() + ":" + thisInstruction.getMin() + ":" + thisInstruction.getSec());

        return customView;
    }

    /**
     * Creates a dialog so that user can edit instruction name.
     */
    public void updateItemDetails(int position, ViewGroup parent)
    {
        // Set the layout for the input dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.input_instruction, parent, false);

        final int updatePosition = position;

        // Current instruction to edit
        thisInstruction = getItem(position);

        // Set up the edit text fields in dialog
        final EditText edit_name = (EditText) view.findViewById(R.id.edit_instruction);
        final EditText edit_hr = (EditText) view.findViewById(R.id.edit_hours);
        final EditText edit_min = (EditText) view.findViewById(R.id.edit_minutes);
        final EditText edit_sec = (EditText) view.findViewById(R.id.edit_seconds);
        //Log.v("edit", edit_name.getText().toString());

        /* Populate POP UP with item name */
        edit_name.setText(thisInstruction.getStep());
        edit_hr.setText(thisInstruction.getHr());
        edit_min.setText(thisInstruction.getMin());
        edit_sec.setText(thisInstruction.getSec());


        // Start the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        /**
         * On update set the new instruction name
         */
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                thisInstruction = getItem(updatePosition);
                //id = thisItem.getId();

                String update_instruction = edit_name.getText().toString();
                String update_hr = edit_hr.getText().toString();
                String update_min = edit_min.getText().toString();
                String update_sec = edit_sec.getText().toString();

                if(Integer.parseInt(update_min) > 59){
                    update_min = "59";
                }
                if(Integer.parseInt(update_sec) > 59){
                    update_sec = "59";
                }

                // ON UPDATE, SET STEP AND TIME TO INSTRUCTION OBJECT
                thisInstruction.setStep(update_instruction);
                thisInstruction.setHr(update_hr);
                thisInstruction.setMin(update_min);
                thisInstruction.setSec(update_sec);
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
                .setMessage("Are you sure you want to clear this instruction?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int clickedId) {
                        thisInstruction = getItem(removePosition);

                        // Removes the list item from ui
                        list.remove(thisInstruction);
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
