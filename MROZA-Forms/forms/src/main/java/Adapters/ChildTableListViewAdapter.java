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

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import database.*;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.R;
import repositories.ProgramRepository;

import java.util.*;


public class ChildTableListViewAdapter extends ArrayAdapter<ChildTable> {
    private final ChooseProgramActivity.Term term;
    public List<ChildTable> childTableList = null;
    Context context;
    private int symbolWidth;

    public ChildTableListViewAdapter(Context context, List<ChildTable> resource, ChooseProgramActivity.Term term) {
        super(context, R.layout.program_list_row,resource);
        this.context = context;
        this.childTableList = resource;
        this.term = term;

        symbolWidth = maxSymbolWidth();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.program_list_row, parent, false);
        TextView symbol = (TextView) convertView.findViewById(R.id.textViewSymbol);
        TextView name = (TextView) convertView.findViewById(R.id.textViewName);
        symbol.setWidth(symbolWidth);

        ChildTable childTable = childTableList.get(position);
        Program programForChildTable = ProgramRepository.getProgramForChildTable(context, childTable);

        if(programForChildTable.getSymbol() != null){
            symbol.setText(programForChildTable.getSymbol());
            name.setText(getProgramsName(programForChildTable, childTable));
            convertView = setChildTableColor(childTable, convertView);
        }
        else{
            symbol.setText(R.string.empty);
            name.setText(R.string.empty);
        }
        return convertView;
    }

    private String getProgramsName(Program program, ChildTable childTable) {

        if(childTable.getIsPretest())
            return program.getName() + context.getResources().getString(R.string.pretest_label);
        return program.getName();
    }

    private View setChildTableColor(ChildTable childTable, View convertView) {

        if(childTable != null) {

            if (childTable.getIsTeachingCollected() && childTable.getIsGeneralizationCollected()) {
                convertView = colorViewWhereTeachingAndGeneralizationIsCollected(convertView, childTable);
            } else if (childTable.getIsTeachingCollected()) {
                convertView = colorViewWhereOnlyTeachingIsCollected(convertView, childTable);
            } else if (childTable.getIsGeneralizationCollected()) {
                convertView = colorViewWhereOnlyGeneralizationIsCollected(convertView, childTable);
            } else {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorNotStarted));
            }
        }
        return convertView;
    }

    private View colorViewWhereOnlyGeneralizationIsCollected(View convertView, ChildTable childTable) {
        if (childTable.getGeneralizationFillOutDate() == null)
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorNotStarted));
        else {
            if (childTable.getIsGeneralizationFinished())
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorFinished));
            else
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorStarted));
        }
        return convertView;
    }

    private View colorViewWhereOnlyTeachingIsCollected(View convertView, ChildTable childTable) {
        if (childTable.getTeachingFillOutDate() == null)
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorNotStarted));
        else {
            if (childTable.getIsTeachingFinished())
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorFinished));
            else
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorStarted));
        }
        return convertView;
    }

    private View colorViewWhereTeachingAndGeneralizationIsCollected(View convertView, ChildTable childTable) {
        if (childTable.getTeachingFillOutDate() == null && childTable.getGeneralizationFillOutDate() == null)
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorNotStarted));
        else {
            if (childTable.getIsTeachingFinished() && childTable.getIsGeneralizationFinished())
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorFinished));
            else if(childTable.getIsTeachingFinished())
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorOneFinished));
            else if(childTable.getIsGeneralizationFinished())
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorOneFinished));
            else
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorStarted));
        }
        return convertView;
    }

    private int maxSymbolWidth(){
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View convertView = inflater.inflate(R.layout.program_list_row, null, false);
        TextView symbol = (TextView) convertView.findViewById(R.id.textViewSymbol);
        Paint textPaint = symbol.getPaint();

        float maxWidth = 0.0f;
        List<Program> programsForChildTables = ProgramRepository.getProgramsForChildTables(context, childTableList);

        for(Program program : programsForChildTables) {
            float width = textPaint.measureText(program.getSymbol());
            if(width > maxWidth)
                maxWidth = width;
        }

        maxWidth += (symbol.getPaddingLeft() + symbol.getPaddingRight());

        //check if it's not wider than half of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        if((int)maxWidth > screenWidth / 2)
            maxWidth = screenWidth / 2;

        return (int)maxWidth;
    }
}
