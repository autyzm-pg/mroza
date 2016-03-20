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


public class TableElement {

    private String fieldName;
    private String[] values;

    public TableElement(String fieldName, String[] values){
        this.fieldName = fieldName;
        this.values = values;
    }

    public String getFieldName(){
        return this.fieldName;
    }

    public String getValueAtIndex(int index){
        if(index < values.length)
            return this.values[index];
        else return null;
    }

    public void setValueAtIndex(int index, String value){
        if(index < values.length)
            this.values[index] = value;
    }

    public int getSize(){
        return values.length;
    }
}

