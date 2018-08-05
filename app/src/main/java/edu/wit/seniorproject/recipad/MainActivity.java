package edu.wit.seniorproject.recipad;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(getApplicationContext(), "Swipe from the left to access application drawer!",
                Toast.LENGTH_LONG).show();

        /* Database */
        myDb = new DatabaseHelper(this);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false))
        {
            // <---- run your one time code here
            addDefaultRecipes();
            //databaseSetup();

            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

        {
            FragmentGroceryList fragment = new FragmentGroceryList();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            setTitle("Shopping List");
        }


        /* For toolbar at top */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /* Initialize navigation drawer */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /* nav_view specified in activity_main */
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        /* Hide the keyboard */
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /**
     * Adds the default recipes
     */
    private void addDefaultRecipes()
    {

        myDb.openDB();

        // APPLE PIE

        ArrayList<Instruction> applePieInstructions = new ArrayList<>();
        applePieInstructions.add(new Instruction("Heat oven to 425 degrees", "0", "0", "0"));
        applePieInstructions.add(new Instruction("Place 1 pie crust in ungreased 9-inch glass", "0", "0", "0"));
        applePieInstructions.add(new Instruction("Mix ingredients", "0", "0", "0"));
        applePieInstructions.add(new Instruction("Top with crust", "0", "0", "0"));
        applePieInstructions.add(new Instruction("Bake 40 minutes", "0", "40", "0"));
        applePieInstructions.add(new Instruction("Cool on ranch 2 hrs before serving", "2", "0", "0"));

        ArrayList<Item> applePieIngredients = new ArrayList<>();
        applePieIngredients.add(new Item("", "Pie crust", "1", ""));
        applePieIngredients.add(new Item("", "Sliced apples", "6", ""));
        applePieIngredients.add(new Item("", "Sugar 3/4 cup", "1", ""));
        applePieIngredients.add(new Item("", "Flour 2 tbsp", "1", ""));
        applePieIngredients.add(new Item("", "Cinnamon 3/4 tsp", "1", ""));
        applePieIngredients.add(new Item("", "Salt 1/4 tsp", "1", ""));
        applePieIngredients.add(new Item("", "Nutmeg 1/8 tsp", "1", ""));
        applePieIngredients.add(new Item("", "Lemon Juice 1 tbsp", "1", ""));

        //INSERT RECIPE INTO DATABASE
        myDb.insertRecipeData("Apple Pie", "3 HR",
                FragmentRecipeMaker.itemListToString(applePieIngredients),
                FragmentRecipeMaker.instructionListToString(applePieInstructions));


        // PANCAKES

        ArrayList<Instruction> pancakesInstructions = new ArrayList<>();
        pancakesInstructions.add(new Instruction("Mix flour, baking powder, salt, and sugar in large bowl", "0", "0", "0"));
        pancakesInstructions.add(new Instruction("Pour in milk, egg, and melted butter", "0", "0", "0"));
        pancakesInstructions.add(new Instruction("Mix until smooth", "0", "0", "0"));
        pancakesInstructions.add(new Instruction("Heat lightly oiled griddle", "0", "0", "0"));
        pancakesInstructions.add(new Instruction("Brown on both sides", "0", "15", "0"));
        pancakesInstructions.add(new Instruction("Serve hot", "0", "0", "0"));

        ArrayList<Item> pancakesIngredients = new ArrayList<>();
        pancakesIngredients.add(new Item("", "Flour 1 1/2 cups", "1", ""));
        pancakesIngredients.add(new Item("", "Baking powder 3 1/2 tsp", "6", ""));
        pancakesIngredients.add(new Item("", "Salt 1 tsp", "1", ""));
        pancakesIngredients.add(new Item("", "Sugar 1 tbsp", "1", ""));
        pancakesIngredients.add(new Item("", "Milk 1 1/4 cups", "1", ""));
        pancakesIngredients.add(new Item("", "Egg", "1", ""));
        pancakesIngredients.add(new Item("", "Melted Butter 3 tbsp", "1", ""));

        //INSERT RECIPE INTO DATABASE
        myDb.insertRecipeData("Pancakes", "20 MIN",
                FragmentRecipeMaker.itemListToString(pancakesIngredients),
                FragmentRecipeMaker.instructionListToString(pancakesInstructions));


        // TACOS

        ArrayList<Instruction> tacosInstructions = new ArrayList<>();
        tacosInstructions.add(new Instruction("Heat oven to 250 degrees", "0", "0", "0"));
        tacosInstructions.add(new Instruction("Brown ground beef and onion over medium heat, stirring frequently", "0", "10", "0"));
        tacosInstructions.add(new Instruction("Drain", "0", "0", "0"));
        tacosInstructions.add(new Instruction("Stir in chili powder, salt, garlic powder, and tomato sauce", "0", "0", "0"));
        tacosInstructions.add(new Instruction("Reduce heat to low. Cover and simmer", "0", "10", "0"));
        tacosInstructions.add(new Instruction("Place taco shells on ungreased cookie sheet. Heat at 250 degrees", "0", "5", "0"));
        tacosInstructions.add(new Instruction("Layer beef, cheese, veggies in each taco shell. Serve with salsa and top with sour cream", "0", "0", "0"));

        ArrayList<Item> tacosIngredients = new ArrayList<>();
        tacosIngredients.add(new Item("", "Ground beef 1 lb", "1", ""));
        tacosIngredients.add(new Item("", "Medium Onion", "6", ""));
        tacosIngredients.add(new Item("", "Chili Powder 1 tsp", "1", ""));
        tacosIngredients.add(new Item("", "Salt 1/2 teaspoon", "1", ""));
        tacosIngredients.add(new Item("", "Garlic Powder 1/2 teaspoon", "1", ""));
        tacosIngredients.add(new Item("", "8-oz Can Tomato Sauce", "1", ""));
        tacosIngredients.add(new Item("", "Taco Shells", "12", ""));
        tacosIngredients.add(new Item("", "Shredded Cheese 1 1/2 cup", "1", ""));
        tacosIngredients.add(new Item("", "Shredded lettuce 2 cup", "1", ""));
        tacosIngredients.add(new Item("", "Tomatoes, chopped", "2", ""));
        tacosIngredients.add(new Item("", "Salsa 3/4 cup", "1", ""));
        tacosIngredients.add(new Item("", "Sour cream 3/4 cup", "1", ""));

        //INSERT RECIPE INTO DATABASE
        myDb.insertRecipeData("Tacos", "25 MIN",
                FragmentRecipeMaker.itemListToString(tacosIngredients),
                FragmentRecipeMaker.instructionListToString(tacosInstructions));

        myDb.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
        }

        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            //TODO: Perform your logic to pass back press here
            for(Fragment fragment : fragmentList){
                if(fragment instanceof OnBackPressedListener){
                    ((OnBackPressedListener)fragment).onBackPressed();
                }
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.grocery_list)
        {
            FragmentGroceryList fragment = new FragmentGroceryList();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            setTitle("Shopping List");
        }
        else if (id == R.id.inventory_list)
        {
            FragmentInventoryList fragment = new FragmentInventoryList();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            setTitle("Inventory");
        }
        else if (id == R.id.recipe_book)
        {
            FragmentRecipeList fragment = new FragmentRecipeList();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            setTitle("Recipe Book");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

}
