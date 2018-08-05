package edu.wit.seniorproject.recipad;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nguyenj11 on 7/12/2016.
 */
public class AdapterExpandableRecipes extends BaseExpandableListAdapter {
    private List<String> header_titles;
    private HashMap<String, List<String>> child_titles;
    private Context context;

    // Put here boolean if color code or not
    private boolean haveAll;

    AdapterExpandableRecipes(Context context, List<String> header_titles, HashMap<String, List<String>> child_titles)
    {
        this.context = context;
        this.child_titles = child_titles;
        this.header_titles = header_titles;
    }

    @Override
    public int getGroupCount() {
        return header_titles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child_titles.get(header_titles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return header_titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child_titles.get(header_titles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) this.getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.view_recipe_header,null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.heading_item);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(title);

        if (FragmentRecipeList.recipeList.get(groupPosition).getHasAllIngredients())
        {
            Log.v("color", "In adapter, recipe " + FragmentRecipeList.recipeList.get(groupPosition).getName() + " " + FragmentRecipeList.recipeList.get(groupPosition).getHasAllIngredients());
            // GREEN TEXT COLOR
            textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
        }
        else {
            // RED BACKGROUND COLOR
            textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorSecondaryText));
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        String title = (String) this.getChild(groupPosition, childPosition);

        if (convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.view_recipe_child,null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.child_item);
        textView.setText(title);

        // GREEN BACKGROUND COLOR
        convertView.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));

        // RED BACKGROUND COLOR
        textView.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
