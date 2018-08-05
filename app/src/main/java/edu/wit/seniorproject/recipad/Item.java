package edu.wit.seniorproject.recipad;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by kempm on 6/11/2016.
 */
public class Item implements Serializable, Cloneable{

    private String id;
    private String name;
    private String qty;
    private String expiration;
    private boolean selected = false;
    private int iconID;
    private Calendar myExpiration;



    public Item (String name) {
        this.name = name;
        qty = "";
        expiration = "";
    }

    public Item(String id, String name) {
        this.id = id;
        this.name = name;
        qty = "Quantity";
        expiration = "Expiration Date";
        iconID = R.drawable.cat_other;
    }


    public Item(String id, String name, String qty, String expiration) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.expiration = expiration;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getQty() {
        return qty;
    }

    public int getIconID() {
        return iconID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Calendar getMyExpiration() {
        return myExpiration;
    }

    public void setMyExpiration(Calendar myExpiration) {
        this.myExpiration = myExpiration;
    }


    @Override
    public String toString() {
        return "Item id = " + id + " Item name = " + name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }


}
