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

package database.test;

import database.*;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.test.AbstractDaoSessionTest;

import java.util.Date;
import java.util.List;


public class PreparedTablesTests extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    public PreparedTablesTests() {
        super(DaoMaster.class);
    }

    public void testAddingTablesSchemasToProgram() {

        Child child = new Child(null, "AK123", false);
        daoSession.insert(child);

        Program program = new Program(null, "JKE234", "Powtarzanie wyrazów", "Zadania dotyczące powtarzania wyrazów", new Date(), false, child.getId());
        daoSession.insert(program);

        TableTemplate table = new TableTemplate(null, "Wypowiadanie owoców", "Każde słowo wypowiadane płynnie", new Date(), false, program.getId());
        daoSession.insert(table);

        TableRow tableRow1 = new TableRow(null, "Wypowiadanie śliwki", 1, table.getId());
        daoSession.insert(tableRow1);
        TableField tableField11 = new TableField(null, "Uczenie", 1, tableRow1.getId());
        daoSession.insert(tableField11);
        TableField tableField12 = new TableField(null, "Uczenie", 2, tableRow1.getId());
        daoSession.insert(tableField12);
        TableField tableField13 = new TableField(null, "Generalizacja", 3, tableRow1.getId());
        daoSession.insert(tableField13);

        TableRow tableRow2 = new TableRow(null, "Wypowiadanie gruszki", 2, table.getId());
        daoSession.insert(tableRow2);
        TableField tableField21 = new TableField(null, "Uczenie", 1, tableRow2.getId());
        daoSession.insert(tableField21);
        TableField tableField22 = new TableField(null, "Uczenie", 2, tableRow2.getId());
        daoSession.insert(tableField22);
        TableField tableField23 = new TableField(null, "Generalizacja", 3, tableRow2.getId());
        daoSession.insert(tableField23);

        TableRow tableRow3 = new TableRow(null, "Wypowiadanie arbuza", 3, table.getId());
        daoSession.insert(tableRow3);
        TableField tableField31 = new TableField(null, "Uczenie", 1, tableRow3.getId());
        daoSession.insert(tableField31);
        TableField tableField32 = new TableField(null, "Uczenie", 2, tableRow3.getId());
        daoSession.insert(tableField32);
        TableField tableField33 = new TableField(null, "Generalizacja", 3, tableRow3.getId());
        daoSession.insert(tableField33);

        TableTemplate databaseTableToProgram = program.getTableTemplateList().get(0);
        TableRow databaseTableRow1 = databaseTableToProgram.getTableRowList().get(0);
        TableField databaseTableField11 = databaseTableRow1.getTableFieldList().get(0);

        assertEquals(databaseTableField11.getType(), tableField11.getType());

    }

}