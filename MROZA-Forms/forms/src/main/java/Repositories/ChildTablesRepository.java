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
import java.util.List;


public class ChildTablesRepository {

    public static List<ChildTable> getChildTableByTerm(Context context, ChooseProgramActivity.Term term, Child child) {
        TermSolution termSolution = TermSolutionRepository.getTermSolutionByTerm(context, term, child);
        List<ChildTable> childTables = new ArrayList<>();
        if(termSolution != null)
            childTables = getChildTablesForTermSolution(context,termSolution);
        return childTables;
    }

    private static List<ChildTable> getChildTablesForTermSolution(Context context, TermSolution termSolution) {
        DaoSession daoSession = getDaoSession(context);

        ChildTableDao childTableDao = daoSession.getChildTableDao();
        QueryBuilder qb = childTableDao.queryBuilder();
        qb.join(ChildTableDao.Properties.TermSolutionId, TermSolution.class)
                .where(TermSolutionDao.Properties.Id.eq(termSolution.getId()));

        return (List<ChildTable>) qb.list();
    }

    private static DaoSession getDaoSession(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public static List<ChildTable> getChildTablesWhereProgramsSymbolStartingWithLetter(Context context, String letter, ChooseProgramActivity.Term term, Child child) {
        DaoSession daoSession = getDaoSession(context);

        TermSolution termSolution =  TermSolutionRepository.getTermSolutionByTerm(context, term, child);

        if (termSolution != null) {
            ChildTableDao childTableDao = daoSession.getChildTableDao();
            QueryBuilder qb = childTableDao.queryBuilder();
            qb.join(ChildTableDao.Properties.TermSolutionId, TermSolution.class)
                    .where(TermSolutionDao.Properties.Id.eq(termSolution.getId()));
            Join join2 = qb.join(ChildTableDao.Properties.TableId, TableTemplate.class, TableTemplateDao.Properties.Id);
            qb.join(join2, TableTemplateDao.Properties.ProgramId, Program.class, ProgramDao.Properties.Id)
                    .where(ProgramDao.Properties.Symbol.like(letter + "%"));
            return (List<ChildTable>) qb.list();
        }

        return new ArrayList<ChildTable>();
    }

    public static List<ChildTable> getTeachingNotStartedChildTables(Context context, ChooseProgramActivity.Term term, Child child) {
        DaoSession daoSession = getDaoSession(context);

        TermSolution termSolution =  TermSolutionRepository.getTermSolutionByTerm(context,term, child);

        if (termSolution != null) {
            ChildTableDao childTableDao = daoSession.getChildTableDao();
            QueryBuilder qb = childTableDao.queryBuilder();
            qb.where(ChildTableDao.Properties.IsTeachingCollected.eq(true), ChildTableDao.Properties.TeachingFillOutDate.isNull());
            qb.join(ChildTableDao.Properties.TermSolutionId, TermSolution.class, TermSolutionDao.Properties.Id)
                    .where(TermSolutionDao.Properties.Id.eq(termSolution.getId()));
            return (List<ChildTable>) qb.list();
        }

        return new ArrayList<ChildTable>();
    }

    public static List<ChildTable> getTeachingSavedChildTables(Context context, ChooseProgramActivity.Term term, Child child) {
        DaoSession daoSession = getDaoSession(context);

        TermSolution termSolution =  TermSolutionRepository.getTermSolutionByTerm(context,term, child);

        if (termSolution != null) {
            ChildTableDao childTableDao = daoSession.getChildTableDao();
            QueryBuilder qb = childTableDao.queryBuilder();
            qb.where(ChildTableDao.Properties.IsTeachingCollected.eq(true), ChildTableDao.Properties.TeachingFillOutDate.isNotNull(), ChildTableDao.Properties.IsTeachingFinished.eq(false));
            qb.join(ChildTableDao.Properties.TermSolutionId, TermSolution.class, TermSolutionDao.Properties.Id)
                    .where(TermSolutionDao.Properties.Id.eq(termSolution.getId()));
            return (List<ChildTable>) qb.list();
        }

        return new ArrayList<ChildTable>();
    }

    public static List<ChildTable> getTeachingFinishedChildTables(Context context, ChooseProgramActivity.Term term, Child child) {
        DaoSession daoSession = getDaoSession(context);

        TermSolution termSolution =  TermSolutionRepository.getTermSolutionByTerm(context,term, child);

        if (termSolution != null) {
            ChildTableDao childTableDao = daoSession.getChildTableDao();
            QueryBuilder qb = childTableDao.queryBuilder();
            qb.where(ChildTableDao.Properties.IsTeachingCollected.eq(true), ChildTableDao.Properties.IsTeachingFinished.eq(true));
            qb.join(ChildTableDao.Properties.TermSolutionId, TermSolution.class, TermSolutionDao.Properties.Id)
                    .where(TermSolutionDao.Properties.Id.eq(termSolution.getId()));
            return (List<ChildTable>) qb.list();
        }

        return new ArrayList<ChildTable>();
    }

    public static List<ChildTable> getGeneralizationNotStartedChildTables(Context context, ChooseProgramActivity.Term term, Child child) {
        DaoSession daoSession = getDaoSession(context);

        TermSolution termSolution =  TermSolutionRepository.getTermSolutionByTerm(context,term, child);

        if (termSolution != null) {
            ChildTableDao childTableDao = daoSession.getChildTableDao();
            QueryBuilder qb = childTableDao.queryBuilder();
            qb.where(ChildTableDao.Properties.IsGeneralizationCollected.eq(true), ChildTableDao.Properties.GeneralizationFillOutDate.isNull());
            qb.join(ChildTableDao.Properties.TermSolutionId, TermSolution.class, TermSolutionDao.Properties.Id)
                    .where(TermSolutionDao.Properties.Id.eq(termSolution.getId()));
            return (List<ChildTable>) qb.list();
        }

        return new ArrayList<ChildTable>();
    }

    public static List<ChildTable> getGeneralizationSavedChildTables(Context context,ChooseProgramActivity.Term term, Child child) {
        DaoSession daoSession = getDaoSession(context);

        TermSolution termSolution =  TermSolutionRepository.getTermSolutionByTerm(context,term, child);

        if (termSolution != null) {
            ChildTableDao childTableDao = daoSession.getChildTableDao();
            QueryBuilder qb = childTableDao.queryBuilder();
            qb.where(ChildTableDao.Properties.IsGeneralizationCollected.eq(true), ChildTableDao.Properties.GeneralizationFillOutDate.isNotNull(), ChildTableDao.Properties.IsGeneralizationFinished.eq(false));
            qb.join(ChildTableDao.Properties.TermSolutionId, TermSolution.class, TermSolutionDao.Properties.Id)
                    .where(TermSolutionDao.Properties.Id.eq(termSolution.getId()));
            return (List<ChildTable>) qb.list();
        }

        return new ArrayList<ChildTable>();

    }

    public static List<ChildTable> getGeneralizationFinishedChildTables(Context context, ChooseProgramActivity.Term term, Child child) {
        DaoSession daoSession = getDaoSession(context);

        TermSolution termSolution =  TermSolutionRepository.getTermSolutionByTerm(context,term, child);

        if (termSolution != null) {
            ChildTableDao childTableDao = daoSession.getChildTableDao();
            QueryBuilder qb = childTableDao.queryBuilder();
            qb.where(ChildTableDao.Properties.IsGeneralizationCollected.eq(true), ChildTableDao.Properties.IsGeneralizationFinished.eq(true));
            qb.join(ChildTableDao.Properties.TermSolutionId, TermSolution.class, TermSolutionDao.Properties.Id)
                    .where(TermSolutionDao.Properties.Id.eq(termSolution.getId()));
            return (List<ChildTable>) qb.list();
        }

        return new ArrayList<ChildTable>();
    }

    public static List<ChildTable> getChildTablesByTermSelectedBySearch(Context context, String searchQuery, ChooseProgramActivity.Term term, Child child) {

        List<ChildTable> childTables = getChildTableByTerm(context, term, child);
        //regex (?i:query) means query is case insensitive
        List<ChildTable> queriedChildTables = new ArrayList<>();
        for (ChildTable childTable : childTables) {

            Program program = ProgramRepository.getProgramForChildTable(context,childTable);
            if (program.getSymbol().matches("(?i:" + searchQuery + ".*)") || //symbol starting with query
                    program.getName().matches("(?i:.*" + searchQuery + ".*)")) //name containing query
                queriedChildTables.add(childTable);
        }

        return queriedChildTables;
    }
}
