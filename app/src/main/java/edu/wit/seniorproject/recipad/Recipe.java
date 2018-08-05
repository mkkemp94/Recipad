package edu.wit.seniorproject.recipad;

import java.util.ArrayList;

/**
 * Created by nguyenj11 on 7/12/2016.
 */
public class Recipe {
    private String id;
    private String name;
    private String time;
    private ArrayList<Instruction> instructions;
    private ArrayList<Item> ingredients = new ArrayList<Item>();

    private boolean hasAllIngredients;


    /**
     * Default and Regular Constructors
     */

    public Recipe(){
        super();
        this.name = "";
        this.time = "";
        this.instructions = new ArrayList<Instruction>();
        this.ingredients = new ArrayList<Item>();
    }

    public Recipe(String id, String name, String time, ArrayList<Instruction> instructionsList, ArrayList<Item> ingredientsList){
        this.id = id;
        this.name = name;
        this.time = time;
        this.instructions = instructionsList;
        this.ingredients = ingredientsList;
    }

    /**
     * Add or Remove Ingredients_List required
     */

    public void addIngredients(Item ingredients){   this.ingredients.add(ingredients);  }
    public void removeIngredients(Item instructions){   this.ingredients.remove(instructions);  }

    /**
     * Add or Remove Recipe Instructions
     */

    public void addInstructions(Instruction instructions){   this.instructions.add(instructions);    }
    public void removeInstructions(Instruction instructions){    this.instructions.remove(instructions); }

    /**
     * These are getters and setters
     */

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public ArrayList<Instruction> getInstruction() {return instructions;}
    public void setInstruction(ArrayList<Instruction> instruction) {this.instructions = instruction;}

    public String getTime() {return time;}
    public void setTime(String time) {this.time = time;}

    public ArrayList<Item> getIngredients() {return ingredients;}
    public void setIngredients(ArrayList<Item> ingrediants) {this.ingredients = ingrediants;}

    public boolean getHasAllIngredients() {
        return hasAllIngredients;
    }
    public void setHasAllIngredients(boolean hasAllIngredients) {
        this.hasAllIngredients = hasAllIngredients;
    }

}
