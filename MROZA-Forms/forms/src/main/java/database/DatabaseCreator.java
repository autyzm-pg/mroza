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

package database;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/*Skryp tworzacy scheme tylko raz po zmianie i nalezy zmienic numer tej schey wtedy*/
public class DatabaseCreator {


    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1086, "database");
        createSyncDate(schema);
        createTherapist(schema);
        createChildrenTablesConections(schema);
        new DaoGenerator().generateAll(schema, "./forms/src/main/java");

    }

    private static void createChildrenTablesConections(Schema schema) {

        Entity child = createChild(schema);
        Entity termSolution = createTermSolution(schema, child);
        Entity program = createProgram(schema, child);
        Entity table = createTable(schema, program);


        Entity tableRow = createTableRow(schema, table);
        Entity tableField = createTableField(schema, tableRow);

        Entity childTable = createChildTable(schema, table, termSolution);
        Entity tableFieldFilling = createTableFieldFilling(schema, tableField, childTable);

    }

    private static Entity createTableFieldFilling(Schema schema, Entity tableField, Entity childTable) {
        Entity tableFieldFilling = schema.addEntity("TableFieldFilling");
        tableFieldFilling.addIdProperty();
        tableFieldFilling.addStringProperty("content");

        Property tableFieldId = tableFieldFilling.addLongProperty("tableFieldId").notNull().getProperty();
        tableFieldFilling.addToOne(tableField, tableFieldId);
        tableField.addToMany(tableFieldFilling, tableFieldId);

        Property childTableId = tableFieldFilling.addLongProperty("childTableId").notNull().getProperty();
        tableFieldFilling.addToOne(childTable, childTableId);
        childTable.addToMany(tableFieldFilling,childTableId,"tableFieldFilling");

        return tableFieldFilling;
    }

    private static Entity createChildTable(Schema schema, Entity table, Entity termSolution) {

        Entity childTable = schema.addEntity("ChildTable");
        childTable.addIdProperty();
        childTable.addBooleanProperty("isTeachingCollected").notNull();
        childTable.addBooleanProperty("isGeneralizationCollected").notNull();
        childTable.addBooleanProperty("isTeachingFinished").notNull();
        childTable.addBooleanProperty("isGeneralizationFinished").notNull();

        childTable.addStringProperty("note");
        childTable.addBooleanProperty("isIOA").notNull();
        childTable.addBooleanProperty("isPretest").notNull();
        childTable.addDateProperty("teachingFillOutDate");
        childTable.addDateProperty("generalizationFillOutDate");
        childTable.addDateProperty("lastEditDate");

        Property tableId = childTable.addLongProperty("tableId").notNull().getProperty();
        childTable.addToOne(table, tableId);
        table.addToMany(childTable, tableId);

        Property termSolutionId = childTable.addLongProperty("termSolutionId").notNull().getProperty();
        childTable.addToOne(termSolution, termSolutionId);
        termSolution.addToMany(childTable,termSolutionId, "childTable");

        return childTable;
    }

    private static Entity createTableField(Schema schema, Entity tableRow) {
        Entity tableField = schema.addEntity("TableField");
        tableField.addIdProperty();
        tableField.addStringProperty("type");
        tableField.addIntProperty("inOrder");

        Property tableRowId = tableField.addLongProperty("tableRowId").notNull().getProperty();
        tableField.addToOne(tableRow, tableRowId);
        tableRow.addToMany(tableField,tableRowId);

        return tableField;
    }

    private static Entity createTableRow(Schema schema, Entity table) {
        Entity tableRow = schema.addEntity("TableRow");
        tableRow.addIdProperty();
        tableRow.addStringProperty("value");
        tableRow.addIntProperty("inOrder");

        Property tableId = tableRow.addLongProperty("tableId").notNull().getProperty();
        tableRow.addToOne(table, tableId);
        table.addToMany(tableRow,tableId);

        return tableRow;
    }

    private static Entity createTable(Schema schema, Entity program) {
        Entity table = schema.addEntity("TableTemplate");
        table.addIdProperty();
        table.addStringProperty("name").notNull();
        table.addStringProperty("description");
        table.addDateProperty("createDate");
        table.addBooleanProperty("isArchived").notNull();

        Property programId = table.addLongProperty("programId").notNull().getProperty();
        table.addToOne(program, programId);
        program.addToMany(table,programId);

        return table;
    }

    private static Entity createProgram(Schema schema, Entity child) {
        Entity program = schema.addEntity("Program");
        program.addIdProperty();
        program.addStringProperty("symbol").notNull();
        program.addStringProperty("name").notNull();
        program.addStringProperty("description");
        program.addDateProperty("createDate");
        program.addBooleanProperty("isFinished").notNull();


        Property childId = program.addLongProperty("childId").getProperty();
        program.addToOne(child, childId);
        child.addToMany(program,childId);

        return program;
    }

    private static Entity createTermSolution(Schema schema, Entity child) {
        Entity termSolution = schema.addEntity("TermSolution");
        termSolution.addIdProperty();
        termSolution.addDateProperty("startDate");
        termSolution.addDateProperty("endDate");

        Property childId = termSolution.addLongProperty("childId").notNull().getProperty();
        termSolution.addToOne(child, childId);

        child.addToMany(termSolution,childId);
        return termSolution;
    }

    private static Entity createChild(Schema schema) {
        Entity child = schema.addEntity("Child");
        child.addIdProperty();
        child.addStringProperty("code").notNull();
        child.addBooleanProperty("isArchived").notNull();
        return child;
    }

    private static void createTherapist(Schema schema) {
        Entity therapist = schema.addEntity("Therapist");
        therapist.addIdProperty();
        therapist.addStringProperty("login").notNull();
        therapist.addStringProperty("password").notNull();
        therapist.addStringProperty("name");
        therapist.addStringProperty("surname");
    }

    private static void createSyncDate(Schema schema) {
        Entity therapist = schema.addEntity("SyncDate");
        therapist.addIdProperty();
        therapist.addDateProperty("date");
    }


}
