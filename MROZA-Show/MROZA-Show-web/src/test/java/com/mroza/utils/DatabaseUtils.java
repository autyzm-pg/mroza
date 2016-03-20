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

package com.mroza.utils;

import com.mroza.ReflectionWrapper;
import com.mroza.dao.*;
import com.mroza.models.Kid;
import com.mroza.models.Program;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;

import java.io.IOException;
import java.util.List;

public class DatabaseUtils {

    private SqlSession utilsSqlSession;
    private static KidsDao kidsDao;
    private static ProgramsDao programsDao;
    private static TablesDao tablesDao;
    private static PeriodsDao periodsDao;
    private static KidTablesDao kidTablesDao;
    private static TableRowsDao tableRowsDao;
    private static TableFieldsDao tableFieldsDao;
    private static ResolvedFieldsDao resolvedFieldsDao;
    private static DeleteHistoryDao deleteHistoryDao;
    public DatabaseUtils()
    {
        utilsSqlSession = getSqlSession();
    }

    public SqlSession getSqlSession() {
        if(utilsSqlSession != null )
            return utilsSqlSession;

        SqlSessionFactoryProvider sqlSessionFactoryProvider =  new SqlSessionFactoryProvider();
        try {
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryProvider.produceFactory();
            utilsSqlSession = sqlSessionFactory.openSession();
        } catch (IOException exception) {
            Assert.fail("Exception was raised: " + exception.getMessage());
            return null;
        }
        return utilsSqlSession;
    }

    public void cleanUpDatabase()
    {
        tablesDao = new TablesDao();
        kidTablesDao = new KidTablesDao();
        programsDao = new ProgramsDao();
        periodsDao = new PeriodsDao();
        kidsDao = new KidsDao();
        tableRowsDao = new TableRowsDao();
        tableFieldsDao = new TableFieldsDao();
        resolvedFieldsDao = new ResolvedFieldsDao();
        deleteHistoryDao = new DeleteHistoryDao();
        ReflectionWrapper.setPrivateField(tablesDao, "sqlSession", utilsSqlSession);
        ReflectionWrapper.setPrivateField(kidTablesDao, "sqlSession", utilsSqlSession);
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", utilsSqlSession);
        ReflectionWrapper.setPrivateField(periodsDao, "sqlSession", utilsSqlSession);
        ReflectionWrapper.setPrivateField(kidsDao, "sqlSession", utilsSqlSession);
        ReflectionWrapper.setPrivateField(tableRowsDao, "sqlSession", utilsSqlSession);
        ReflectionWrapper.setPrivateField(tableFieldsDao, "sqlSession", utilsSqlSession);
        ReflectionWrapper.setPrivateField(resolvedFieldsDao, "sqlSession", utilsSqlSession);
        ReflectionWrapper.setPrivateField(deleteHistoryDao, "sqlSession", utilsSqlSession);
        List<Kid> kidList = kidsDao.selectAllKids();
        kidsDao.deleteKids(kidList);
        List<Program> programList = programsDao.selectAllPrograms();
        programsDao.deletePrograms(programList);
        utilsSqlSession.commit();
    }

    public Kid setUpKid(String symbol)
    {
        Kid kid = new Kid(-1, symbol, false, null, null);
        kidsDao.insertKid(kid);
        utilsSqlSession.commit();
        return kid;

    }
}
