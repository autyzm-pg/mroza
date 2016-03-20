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
import de.greenrobot.dao.test.AbstractDaoSessionTest;

import java.util.Date;

public class FillingTablesTests  extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    public FillingTablesTests() {
        super(DaoMaster.class);
    }

    public void testFillingTableFields(){

        Child child = new Child(null,"AK123", false);
        daoSession.insert(child);

        Program program = new Program(null, "JKE234", "Powtarzanie wyrazów", "Zadania dotyczące powtarzania wyrazów", new Date(), false, child.getId());
        daoSession.insert(program);

        TableTemplate table = new TableTemplate(null, "Wypowiadanie owoców", "Każde słowo wypowiadane płynnie", new Date(), false, program.getId());
        daoSession.insert(table);

        TableRow tableRow = new TableRow(null, "Wypowiadanie śliwki", 1, table.getId());
        daoSession.insert(tableRow);

        TableField tableField = new TableField(null, "Uczenie", 1, tableRow.getId());
        daoSession.insert(tableField);

        TermSolution termSolution = new TermSolution(null,new Date(), new Date(),child.getId());
        daoSession.insert(termSolution);

        ChildTable childTable = new ChildTable(null,false,false,false,false,"ok",false,false,new Date(), new Date(),new Date(),table.getId(), termSolution.getId());
        daoSession.insert(childTable);

        TableFieldFilling tableFieldFilling = new TableFieldFilling(null,"Zal",tableField.getId(),childTable.getId());
        daoSession.insert(tableFieldFilling);


        TermSolution databaseTermSolution = child.getTermSolutionList().get(0);
        ChildTable databaseChildTable = databaseTermSolution.getChildTable().get(0);
        TableFieldFilling databaseTableFieldFilling = databaseChildTable.getTableFieldFilling().get(0);

        assertEquals(databaseTableFieldFilling, tableFieldFilling);
    }
}
