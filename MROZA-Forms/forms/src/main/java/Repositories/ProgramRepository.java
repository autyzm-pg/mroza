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

package repositories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import database.*;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import mroza.forms.ChooseProgramActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProgramRepository {

    public static Program getProgramForChildTable(Context context, ChildTable childTable) {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        ProgramDao programDao = daoSession.getProgramDao();
        QueryBuilder qb = programDao.queryBuilder();
        Join join = qb.join(ProgramDao.Properties.Id, TableTemplate.class, TableTemplateDao.Properties.ProgramId);
        qb.join(join, TableTemplateDao.Properties.Id, ChildTable.class, ChildTableDao.Properties.TableId)
                .where(ChildTableDao.Properties.Id.eq(childTable.getId()));

        return ((List<Program>) qb.list()).get(0);
    }

    public static List<Program> getProgramsForChildTables(Context context, List<ChildTable> childTables) {

        List<Program> allPrograms = new ArrayList<>();

        for(ChildTable childTable : childTables)
        {
            allPrograms.add(getProgramForChildTable(context,childTable));
        }

        return allPrograms;
    }
}
