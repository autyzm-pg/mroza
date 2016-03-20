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

import java.util.List;

import database.Child;
import database.ChildDao;
import database.DaoMaster;
import database.DaoSession;
import de.greenrobot.dao.query.QueryBuilder;


public class ChildRepository {

    public static Child getChildById(Context context, long id) {
        DaoSession daoSession = getDaoSession(context);
        ChildDao childDao = daoSession.getChildDao();
        return childDao.load(id);
    }

    public static List<Child> getChildrenBySearch(Context context, String query) {
        DaoSession daoSession = getDaoSession(context);
        ChildDao childDao = daoSession.getChildDao();
        QueryBuilder qb = childDao.queryBuilder();
        List<Child> searchedChildren = qb.where(ChildDao.Properties.Code.like(query + "%")).list();
        return searchedChildren;
    }

    public static List<Child> getChildrenList(Context context) {
        DaoSession daoSession = getDaoSession(context);
        ChildDao childDao = daoSession.getChildDao();
        List<Child> childrenList = childDao.loadAll();
        return childrenList;
    }

    private static DaoSession getDaoSession(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }
}



