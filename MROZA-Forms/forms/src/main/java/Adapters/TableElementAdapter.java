/*
 * MROZA - supporting system of behavioral therapy of people with autism
 *     Copyright (C) 2015-2016 autyzm-pg
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package adapters;


import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import mroza.forms.R;

public class TableElementAdapter extends ArrayAdapter<TableElement> {
    Context context;
    TableElement[] tableElementsItems = null;
    int[] images = {R.drawable.ic_question_mark, R.drawable.ic_plus, R.drawable.ic_minus};
    int teachingFieldsNumber;

    int generalizationFieldsNumber;
    boolean isTeachingFinished;
    boolean isGeneralizationFinished;
    boolean isPretest;

    final int TEXT_SIZE = 20;
    final int BUTTON_WIDTH = 50;
    final int BUTTON_HEIGHT = 30;
    final int LAYOUT_PADDING_VERTICAL = 5;
    final int LAYOUT_PADDING_HORIZONTAL = 10;
    int screenWidth;

    public TableElementAdapter(Context context, TableElement[] resource, int teachingFieldsNumber, int generalizationFieldsNumber) {
        super(context, -1, resource); //solution for dynamically created layout from: http://stackoverflow.com/a/12795518
        this.context = context;
        this.tableElementsItems = resource;
        this.teachingFieldsNumber = teachingFieldsNumber;
        this.generalizationFieldsNumber = generalizationFieldsNumber;
        this.isTeachingFinished = false;
        this.isGeneralizationFinished = false;
        this.isPretest = false;

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        this.screenWidth = metrics.widthPixels;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = createRowLayout(position);
        ((TextView) layout.getChildAt(0)).setText(tableElementsItems[position].getFieldName());

        return layout;
    }

    public LinearLayout createHeaderLayout(String tableName){
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.WRAP_CONTENT,
                AbsListView.LayoutParams.WRAP_CONTENT));
        layout.setPadding(LAYOUT_PADDING_HORIZONTAL, LAYOUT_PADDING_VERTICAL, LAYOUT_PADDING_HORIZONTAL, LAYOUT_PADDING_VERTICAL);


        int nameTextViewWidth = screenWidth - 2* LAYOUT_PADDING_HORIZONTAL -
                (teachingFieldsNumber + generalizationFieldsNumber) * BUTTON_WIDTH;
        TextView name = new TextView(context);
        name.setWidth(nameTextViewWidth);
        name.setTextSize(TEXT_SIZE);
        name.setText(prepareTableName(tableName));
        layout.addView(name);

        for(int i = 0; i < teachingFieldsNumber; i++){
            TextView teaching = new TextView(context);
            teaching.setGravity(Gravity.CENTER);
            teaching.setTextSize(TEXT_SIZE);
            teaching.setWidth(BUTTON_WIDTH);
            teaching.setText("U");
            layout.addView(teaching);
        }

        for(int i = 0; i < generalizationFieldsNumber; i++) {
            TextView generalization = new TextView(context);
            generalization.setGravity(Gravity.CENTER);
            generalization.setTextSize(TEXT_SIZE);
            generalization.setWidth(BUTTON_WIDTH);
            generalization.setText("G");
            layout.addView(generalization);
        }

        return layout;
    }

    private String prepareTableName(String tableName) {
        if(isPretest)
            return tableName + " - PRETEST";
        return tableName;
    }

    private LinearLayout createRowLayout(int position){
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.WRAP_CONTENT,
                AbsListView.LayoutParams.WRAP_CONTENT));
        layout.setPadding(LAYOUT_PADDING_HORIZONTAL, LAYOUT_PADDING_VERTICAL, LAYOUT_PADDING_HORIZONTAL, LAYOUT_PADDING_VERTICAL);

        int nameTextViewWidth = screenWidth - 2* LAYOUT_PADDING_HORIZONTAL -
                (teachingFieldsNumber + generalizationFieldsNumber) * BUTTON_WIDTH;
        TextView fieldName = new TextView(context);
        fieldName.setWidth(nameTextViewWidth);
        fieldName.setTextSize(TEXT_SIZE);
        layout.addView(fieldName);

        for(int i = 0; i < teachingFieldsNumber + generalizationFieldsNumber; i++) {
            ImageButton imageButton = new ImageButton(context);
            imageButton.setImageResource(getImage(tableElementsItems[position].getValueAtIndex(i)));
            imageButton.setBackgroundColor(Color.TRANSPARENT);
            imageButton.setId(position * 100 + i); //first two numbers determine row, next two determine field
            imageButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            imageButton.setMinimumWidth(BUTTON_WIDTH);
            imageButton.setMaxWidth(BUTTON_WIDTH);
            imageButton.setMinimumHeight(BUTTON_HEIGHT);
            imageButton.setMaxHeight(BUTTON_HEIGHT);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton imageButton = (ImageButton) v;
                    int field = imageButton.getId() % 100;
                    int row = (imageButton.getId() - field) / 100;

                    String nextValue = getNextValue(tableElementsItems[row].getValueAtIndex(field));
                    tableElementsItems[row].setValueAtIndex(field, nextValue);
                    imageButton.setImageResource(getImage(nextValue));
                }
            });

            //disable button if teaching / generalization is finished or if it;s not actual term
            if((i < teachingFieldsNumber && isTeachingFinished) ||
                    (i >= teachingFieldsNumber && isGeneralizationFinished))
                imageButton.setEnabled(false);

            layout.addView(imageButton);
        }

        return layout;
    }

    public void setTeachingFinished(boolean isTeachingFinished){
        this.isTeachingFinished = isTeachingFinished;
    }

    public void setGeneralizationFinished(boolean isGeneralizationFinished){
        this.isGeneralizationFinished = isGeneralizationFinished;
    }

    public void setPretest(boolean isPretest){
        this.isPretest = isPretest;
    }

    private String getNextValue(String value){
        String nextValue;
        if(value.equals("empty"))
            nextValue = "passed";
        else if(value.equals("passed"))
            nextValue = "failed";
        else
            nextValue = "empty";

        return nextValue;
    }

    private int getImage(String value){
        int index;
        if(value.equals("empty"))
            index = 0;
        else if(value.equals("passed"))
            index = 1;
        else
            index = 2;

        return images[index];
    }

}

