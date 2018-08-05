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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by nguyenj11 on 7/16/2016.
 */
public class FragmentAddIngredients extends Fragment
    implements MainActivity.OnBackPressedListener {

    /* Adapter for modifying list view */
    private ArrayAdapter<Item> adapter;

    /* Layout for the actual list */
    private ListView listView;

    /* List of Items created and added to by the user */
    static public ArrayList<Item> ingredientsList = new ArrayList<Item>();

    /* User input */
    private EditText etAddIngredient;
    private Button btnAddIngredient;
    private Button saveIngredients;


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
        View v = inflater.inflate(R.layout.fragment_recipe_maker_ingredients, container, false);
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

        // HIDE KEYBOARD
        hideSoftKeyboard();


        /**
         * Initializations
         */
        btnAddIngredient = (Button) getView().findViewById(R.id.btn_add_ingredient);
        etAddIngredient = (EditText) getView().findViewById(R.id.et_add_ingredient);
        saveIngredients = (Button) getView().findViewById(R.id.save_ingredients);

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToIngredientList();
            }
        });

        saveIngredients.setOnClickListener(new View.OnClickListener() {
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
        adapter = new AdapterIngredients (
                getView().getContext(),       // context
                ingredientsList);             // items array

        listView = (ListView) getView().findViewById(R.id.list);

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    /**
     * Add new item to list.
     */
    private void addToIngredientList()
    {
        if (etAddIngredient.getText().toString().matches(""))
        {
            /* User hasn't typed anything */
            Toast.makeText(getView().getContext(),
                    "Enter an ingredient name!", Toast.LENGTH_LONG).show();
        }
        else
        {
            // Name of item to add (entered in) ... trim space off end
            String ingredientName = etAddIngredient.getText().toString().trim();

            // Keeps track of if that name is already in the list
            boolean alreadyInList = false;

            // Search the list to see if its already there
            for (Item item : ingredientsList)
            {
                // Name of current search item
                String ingredientSearch = item.getName();

                // If it's found, tell the user it already exists.
                if (ingredientName.toLowerCase().matches(ingredientSearch.toLowerCase()))
                {
                    Toast.makeText(getContext(), "That ingredient is already in the list!",
                            Toast.LENGTH_LONG).show();

                    // It's already there, so don't add it later.
                    alreadyInList = true;
                }
            }

            // If it's not already in the list, add it!
            if (alreadyInList == false)
            {
                // Ingredient to add
                Item ingredient = new Item("", ingredientName, "1", "");

                // Add it to list and notify adapter
                ingredientsList.add(ingredient);
                adapter.notifyDataSetChanged();
            }

            /* Clear edit text for next input and hide keyboard */
            etAddIngredient.setText("");

            hideSoftKeyboard();
        }
    } // End method


    /**
     * Clone the array list and returns it
     * Mainly used for duplicating a temporary array list
     */

    private ArrayList<Item> cloneList(ArrayList<Item> list) throws CloneNotSupportedException
    {
        ArrayList<Item> clone = new ArrayList<Item>(list.size());
        for(Item item : list)
        {
            clone.add((Item) item.clone());
        }
        return clone;
    }


    /**
     * Hides the keyboard.
     */
    private void hideSoftKeyboard()
    {
        if (getActivity().getCurrentFocus()!= null && getActivity().getCurrentFocus() instanceof EditText)
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etAddIngredient.getWindowToken(), 0);
        }
    }


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
}
