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
import de.greenrobot.dao.query.QueryBuilder;
import mroza.forms.ChooseProgramActivity;

import java.util.*;

public class TermSolutionRepository {


    public static TermSolution getTermSolutionByTerm(Context context, ChooseProgramActivity.Term term, Child child)
    {
        TermSolution termSolution = null;
        if (term == ChooseProgramActivity.Term.HISTORICAL) {
            termSolution = getHistoricalTermSolution(context, child);
        } else if (term == ChooseProgramActivity.Term.ACTUAL) {
            termSolution = getActualTermSolution(context, child);
        } else if (term == ChooseProgramActivity.Term.FUTURE) {
            termSolution = getFutureTermSolution(context, child);
        }
        return termSolution;
    }


    private static TermSolution getHistoricalTermSolution(Context context, Child child) {
        DaoSession daoSession = getDaoSession(context);
        Date dateNow = getActualDate();

        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();

        QueryBuilder qbTermsPast = termSolutionDao.queryBuilder();
        qbTermsPast.where(TermSolutionDao.Properties.EndDate.lt(dateNow));
        qbTermsPast.join(TermSolutionDao.Properties.ChildId, Child.class)
                .where(ChildDao.Properties.Id.eq(child.getId()));

        List<TermSolution> termSolutionsList = (List<TermSolution>) qbTermsPast.list();

        if (termSolutionsList.size() == 0)
            return null;

        Collections.sort(termSolutionsList, new Comparator<TermSolution>() {
            public int compare(TermSolution term1, TermSolution term2) {
                return term1.getEndDate().compareTo(term2.getEndDate());
            }
        });

        return termSolutionsList.get(termSolutionsList.size() - 1);
    }

    private static TermSolution getFutureTermSolution(Context context, Child child) {
        DaoSession daoSession = getDaoSession(context);
        Date dateNow = getActualDate();

        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();

        QueryBuilder qbTermsFuture = termSolutionDao.queryBuilder();
        qbTermsFuture.where(TermSolutionDao.Properties.StartDate.gt(dateNow));
        qbTermsFuture.join(TermSolutionDao.Properties.ChildId, Child.class)
                .where(ChildDao.Properties.Id.eq(child.getId()));

        List<TermSolution> termSolutionsList = (List<TermSolution>) qbTermsFuture.list();

        if (termSolutionsList.size() == 0)
            return null;

        Collections.sort(termSolutionsList, new Comparator<TermSolution>() {
            public int compare(TermSolution term1, TermSolution term2) {
                return term1.getEndDate().compareTo(term2.getEndDate());
            }
        });
        return termSolutionsList.get(0);
    }

    private static TermSolution getActualTermSolution(Context context, Child child) {
        DaoSession daoSession = getDaoSession(context);
        Date dateNow = getActualDate();

        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();

        QueryBuilder qbTermsActual = termSolutionDao.queryBuilder();
        qbTermsActual.where(TermSolutionDao.Properties.StartDate.le(dateNow), TermSolutionDao.Properties.EndDate.ge(dateNow));
        qbTermsActual.join(TermSolutionDao.Properties.ChildId, Child.class)
                .where(ChildDao.Properties.Id.eq(child.getId()));
        List<TermSolution> termSolutionsActualList = (List<TermSolution>) qbTermsActual.list();

        if (termSolutionsActualList.size() == 0)
            return null;

        return termSolutionsActualList.get(0);
    }

    private static DaoSession getDaoSession(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    private static Date getActualDate() {
        Date dateNow = new Date();
        return dateNow;
    }
}
