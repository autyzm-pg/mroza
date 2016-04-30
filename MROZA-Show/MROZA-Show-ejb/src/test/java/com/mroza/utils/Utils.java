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

import com.mroza.dao.*;
import com.mroza.models.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.mybatis.cdi.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    private static KidsDao kidsDao;
    private static ProgramsDao programsDao;
    private static TablesDao tablesDao;
    private static PeriodsDao periodsDao;
    private static KidTablesDao kidTablesDao;
    private static TableRowsDao tableRowsDao;
    private static TableFieldsDao tableFieldsDao;
    private static ResolvedFieldsDao resolvedFieldsDao;
    private static DeleteHistoryDao deleteHistoryDao;

    private static SqlSession utilsSqlSession;

    public static void initWithBasicKids(List<Kid> exampleKids) {
        exampleKids.add(new Kid());
        exampleKids.add(new Kid());
        exampleKids.get(0).setCode("Jacek");
        exampleKids.get(1).setCode("Julia");
        initUtilsDaos();
        SqlSession sqlSession = getSqlSession();
        exampleKids.stream().forEach(kidsDao::insertKid);
        sqlSession.commit();
    }

    public static void initWithBasicKidWithCode(List<Kid> exampleKids, List<String> codes) {
        for(String code : codes) {
            Kid newKid = new Kid();
            newKid.setCode(code);
            exampleKids.add(newKid);
        }
        initUtilsDaos();
        SqlSession sqlSession = getSqlSession();
        exampleKids.stream().forEach(kidsDao::insertKid);
        sqlSession.commit();
    }


    public static void initWithBasicPrograms(List<Program> exampleProgram) {
        exampleProgram.add(new Program("ABC"));
        exampleProgram.add(new Program("QWR"));
        exampleProgram.add(new Program("RFG"));

        initUtilsDaos();
        SqlSession sqlSession = getSqlSession();
        programsDao.insertProgram(exampleProgram.get(0));
        programsDao.insertProgram(exampleProgram.get(1));
        programsDao.insertProgram(exampleProgram.get(2));
        sqlSession.commit();
    }

    public static void initWithMultiPrograms(List<Program> exampleProgram) {
        exampleProgram.add(new Program("ABC"));
        exampleProgram.add(new Program("ABC"));
        exampleProgram.add(new Program("ABC"));
        exampleProgram.add(new Program("QWR"));
        exampleProgram.add(new Program("QWR"));
        exampleProgram.add(new Program("RFG"));

        initUtilsDaos();
        SqlSession sqlSession = getSqlSession();
        programsDao.insertProgram(exampleProgram.get(0));
        programsDao.insertProgram(exampleProgram.get(1));
        programsDao.insertProgram(exampleProgram.get(2));
        sqlSession.commit();
    }

    public static void initKidDeeply(List<Kid> exampleKids, String programsSymbolPrefix) {
        List<Period> kidPeriods = new ArrayList<>();
        kidPeriods.add(new Period(-1, strToDate("5-11-2014"), strToDate("10-11-2014"), null, null, new ArrayList<>()));
        kidPeriods.add(new Period(-1, strToDate("5-11-2014"), strToDate("10-11-2014"), null, null, new ArrayList<>()));

        List<Table> program1Tables = new ArrayList<>();
        program1Tables.add(new Table("Prog 1 - krok 1"));
        program1Tables.get(0).addTableRow("Wiersz z nazwą 1", 3, 2);
        program1Tables.get(0).addTableRow("Wiersz z nazwą 2");
        program1Tables.get(0).addTableRow("Wiersz z nazwą 3");
        program1Tables.add(new Table("Prog 1 - krok 2"));
        program1Tables.get(1).addTableRow("Wiersz dla kroku 2 - nr 1", 3, 0);
        program1Tables.get(1).addTableRow("Wiersz dla kroku 2 - nr 2");
        List<Table> program2Tables = new ArrayList<>();
        program2Tables.add(new Table("Prog 2 - krok 1", LocalDate.of(2014, 11, 15)));
        program2Tables.get(0).addTableRow("Wiersz dla programu 2, krok 1 - nr 1", 2, 5);
        program2Tables.get(0).addTableRow("Wiersz dla programu 2, krok 1 - nr 2");
        program2Tables.get(0).addTableRow("Wiersz dla programu 2, krok 1 - nr 3");
        program2Tables.get(0).addTableRow("Wiersz dla programu 2, krok 1 - nr 4");

        List<Program> kidPrograms = new ArrayList<>();
        kidPrograms.add(new Program(-1, programsSymbolPrefix + ".RT.1", "Program 1", "Opis programu 1",
                false, -1, program1Tables));
        kidPrograms.add(new Program(-1, programsSymbolPrefix + ".RT.2", "Program 2", "Opis programu 2",
                false, -1, program2Tables));
        kidPrograms.add(new Program(-1, programsSymbolPrefix + ".RT.3", "Program 3", "Opis programu 3",
                false, -1, new ArrayList<>()));

        program1Tables.get(0).setProgram(kidPrograms.get(0));
        program1Tables.get(1).setProgram(kidPrograms.get(0));
        program2Tables.get(0).setProgram(kidPrograms.get(1));


        List<KidTable> kidTablesForPeriod_1 = new ArrayList<>();
        kidTablesForPeriod_1.add(new KidTable(true, true, program1Tables.get(0), kidPeriods.get(0)));
        kidTablesForPeriod_1.get(0).setNote("Notatka");
        kidTablesForPeriod_1.get(0).getResolvedFields().get(0).setValue("OK");
        kidTablesForPeriod_1.get(0).getResolvedFields().get(0).setValue("NOK");

        kidTablesForPeriod_1.add(new KidTable(false, true, program2Tables.get(0), kidPeriods.get(0)));
        kidTablesForPeriod_1.get(1).setFinishedLearning(true);
        kidTablesForPeriod_1.get(1).setNote("Notatka 2");
        kidPeriods.get(0).setKidTables(kidTablesForPeriod_1);


        program1Tables.get(0).getKidTables().add(kidTablesForPeriod_1.get(0));
        program1Tables.get(1).getKidTables().add(kidTablesForPeriod_1.get(1));

        Kid exampleKid = new Kid(-1, "Jarek", false, kidPrograms, kidPeriods);
        exampleKids.add(exampleKid);

        SqlSession sqlSession = getSqlSession();
        initUtilsDaos();
        insertKidDeeply(exampleKid);
        sqlSession.commit();
    }

    private static void initUtilsDaos() {
        SqlSession sqlSession = getSqlSession();
        if(tablesDao != null)
            return;
        tablesDao = new TablesDao();
        kidTablesDao = new KidTablesDao();
        programsDao = new ProgramsDao();
        periodsDao = new PeriodsDao();
        kidsDao = new KidsDao();
        tableRowsDao = new TableRowsDao();
        tableFieldsDao = new TableFieldsDao();
        resolvedFieldsDao = new ResolvedFieldsDao();
        deleteHistoryDao = new DeleteHistoryDao();
        ReflectionWrapper.setPrivateField(tablesDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(kidTablesDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(periodsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(kidsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(tableRowsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(tableFieldsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(resolvedFieldsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(deleteHistoryDao, "sqlSession", sqlSession);
    }

    public static void kidsInitializedTearDown(List<Kid> exampleKids) {
        SqlSession sqlSession = getSqlSession();
        kidsDao.deleteKids(exampleKids);
        clearDeleteHistory();
        sqlSession.commit();
    }

    public static void programsInitializedTearDown(List<Program> examplePrograms) {
        SqlSession sqlSession = getSqlSession();
        examplePrograms.stream().forEach(programsDao::deleteProgram);
        clearDeleteHistory();
        sqlSession.commit();
    }

    private static void clearDeleteHistory() {
        SqlSession sqlSession = getSqlSession();
        deleteHistoryDao.clearDeleteHistory();
        sqlSession.commit();
    }

    public static SqlSession getSqlSession() {
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

    public static Date strToDate(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.parse(str);
        } catch (ParseException exception) {
            Assert.fail("Exception was raised: " + exception.getMessage());
            return null;
        }
    }

    @Transactional
    private static void insertKidDeeply(Kid kid) {
        kidsDao.insertKid(kid);

        kid.getPrograms().forEach(program -> {
            program.setKidId(kid.getId());
            programsDao.insertProgram(program);
            program.getTables().forEach(table -> {
                tablesDao.insertTable(table);
                table.getTableRows().forEach(tableRow -> {
                    tableRow.setTableId(table.getId());
                    tableRowsDao.insertTableRow(tableRow);
                    tableRow.getRowFields().forEach(rowField -> {
                        rowField.setRowId(tableRow.getId());
                        tableFieldsDao.insertTableField(rowField);
                    });
                });
            });
        });
        kid.getPeriods().forEach(period -> {
            period.setKidId(kid.getId());
            periodsDao.insertPeriod(period);
            period.getKidTables().forEach(kidTable -> {
                kidTablesDao.insertKidTable(kidTable);
                kidTable.getResolvedFields().forEach(resolvedField -> {
                    resolvedField.setKidTableId(kidTable.getId());
                });
            });
        });
    }

    public static Date getDateFromNow(int deltaDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, deltaDays);
        return calendar.getTime();
    }
}
