package edu.wit.seniorproject.recipad;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by nguyenj11 on 7/16/2016.
 */
public class FragmentAddInstructions extends Fragment
        implements MainActivity.OnBackPressedListener {

    /* Adapter for modifying list view */
    private ArrayAdapter<Instruction> adapter;

    /* Layout for the actual list */
    private ListView listView;

    /* List of Items created and added to by the user */
    static public ArrayList<Instruction> instructionList = new ArrayList<Instruction>();

    // USER INPUT
    private EditText etAddInstruction;
    private Button btnAddInstruction;
    private Button saveInstructions;
    private EditText edit_name;
    private TextView instructionName;

    /**
     * Create fragment view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe_maker_instructions, container, false);
        return v;
    }


    /**
     * 'On create' version for fragments
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // BUILD LIST
        populateItemList();

        // Hide keyboard
        hideSoftKeyboard();

        /**
         * Initializations
         */
        btnAddInstruction = (Button) getView().findViewById(R.id.btn_add_instruction);
        etAddInstruction = (EditText) getView().findViewById(R.id.et_add_instruction);
        saveInstructions = (Button) getView().findViewById(R.id.save_instruction);
        edit_name = (EditText) getView().findViewById(R.id.item_name);
        instructionName = (TextView) getView().findViewById(R.id.txt_ingredient_name);

        btnAddInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToInstructionList();
            }
        });

        saveInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Build Adapter
     */
    private void populateItemList()
    {
        adapter = new AdapterInstructions(
                getView().getContext(),       // context
                instructionList);             // items array

        listView = (ListView) getView().findViewById(R.id.list);

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Add new item to list.
     */
    private void addToInstructionList()
    {
        if (etAddInstruction.getText().toString().matches(""))
        {
            /* User hasn't typed anything */
            Toast.makeText(getView().getContext(),
                    "Enter an instruction!", Toast.LENGTH_LONG).show();
        }
        else
        {
            // Name of item to add (entered in) ... trim space off end
            String instructionName = etAddInstruction.getText().toString().trim();

            // Keeps track of if that name is already in the list
            boolean alreadyInList = false;

            // Search the list to see if its already there
            for (Instruction existingInstruction : instructionList)
            {
                // If it's found, tell the user it already exists.
                if (instructionName.toLowerCase().matches(existingInstruction.getStep().toLowerCase()))
                {
                    Toast.makeText(getContext(), "That instruction is already in the list!",
                            Toast.LENGTH_LONG).show();

                    // It's already there, so don't add it later.
                    alreadyInList = true;
                }
            }

            // If it's not already in the list, add it!
            if (alreadyInList == false)
            {
                // Add it to list and notify adapter
                Instruction newInstruction = new Instruction(instructionName, "", "", "");
                instructionList.add(newInstruction);
                adapter.notifyDataSetChanged();
            }

            /* Clear edit text for next input and hide keyboard */
            etAddInstruction.setText("");

            hideSoftKeyboard();
        }
    } // End method

    /**
     * Go back to recipe maker on back press.
     */
    @Override
    public void onBackPressed() {

        FragmentRecipeMaker fragment = new FragmentRecipeMaker();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

    }

    /**
     * Hides the keyboard.
     */
    private void hideSoftKeyboard()
    {
        if (getActivity().getCurrentFocus()!= null && getActivity().getCurrentFocus() instanceof EditText)
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etAddInstruction.getWindowToken(), 0);
        }
    }
}
