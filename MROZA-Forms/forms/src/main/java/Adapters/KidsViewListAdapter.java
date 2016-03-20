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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import database.Child;
import mroza.forms.R;

import java.util.List;


public class KidsViewListAdapter extends ArrayAdapter<Child>{
    List<Child> children = null;
    Context context;

    public KidsViewListAdapter(Context context, List<Child> resource) {
        super(context, R.layout.kids_list_row,resource);
        this.context = context;
        this.children = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.kids_list_row, parent, false);
        TextView code = (TextView) convertView.findViewById(R.id.code);
        if(children.get(position).getCode() != null) {
            code.setText(children.get(position).getCode());
        }
        else{
            code.setText(R.string.empty_kids_list);
        }
        return convertView;
    }
}
