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

package com.mroza;

import com.mroza.models.Table;
import com.mroza.models.TableRow;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TableTest {

    @Test
    public void getNumberOfLearningColsTest() {
        Table table = new Table();
        List<TableRow> tableRowList = new ArrayList<>();
        for (int rowNumber = 0; rowNumber < 5; rowNumber++)
            tableRowList.add(new TableRow("Akcja", rowNumber, 3, 2));

        table.setTableRows(tableRowList);
        Assert.assertEquals(new Integer(3), table.getNumberOfLearningCols());
    }

    @Test
    public void getNumberOfLearningColsNoRowsTest() {
        Table table = new Table();
        Assert.assertEquals(new Integer(0), table.getNumberOfLearningCols());
    }

    @Test
    public void getNumberOfGeneralizationColsTest() {
        Table table = new Table();
        List<TableRow> tableRowList = new ArrayList<>();
        for (int rowNumber = 0; rowNumber < 5; rowNumber++)
            tableRowList.add(new TableRow("Akcja", rowNumber, 3, 2));

        table.setTableRows(tableRowList);
        Assert.assertEquals(new Integer(2), table.getNumberOfGeneralizationCols());
    }

    @Test
    public void addTableFirstRowTest() {
        Table table = new Table();
        table.addTableRow("Wiersz 1", 2, 1);

        Assert.assertEquals(1, table.getTableRows().size());
        Assert.assertEquals(3, table.getTableRows().get(0).getRowFields().size());
    }

    @Test
    public void addTableRowTest() {
        Table table = new Table();
        table.addTableRow("Wiersz 1", 7, 0);
        table.addTableRow("Wiersz 2");

        Assert.assertEquals(2, table.getTableRows().size());
        Assert.assertEquals(7, table.getTableRows().get(1).getRowFields().size());
    }

}
