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

package mroza.forms;

import adapters.TableElement;
import adapters.TableElementAdapter;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import database.*;
import database.TableRow;
import de.greenrobot.dao.query.QueryBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import syncmodels.ReceiveSyncModel;
import syncmodels.SendSyncModel;
import syncmodels.SyncManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class ProgramActivity extends AppCompatActivity {

    private Child child;
    private ChildTable childTable;
    private AlertDialog alertDialog;
    private TableElementAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            child = getChild(extras.getLong("CHILD_ID"));
            childTable = getChildTable(extras.getLong("CHILD_TABLE_ID"));
        }

        Program program = childTable.getTableTemplate().getProgram();
        setTitle(child.getCode() + " - " + program.getSymbol() + ": " + program.getName());
        setContentView(R.layout.program);

        HashMap<String,Integer> fieldsNumber = getFieldsNumber();
        TableElement[] tableElements = getTableElements();
        adapter = new TableElementAdapter(this, tableElements, fieldsNumber.get("U"), fieldsNumber.get("G"));
        adapter.setTeachingFinished(childTable.getIsTeachingFinished());
        adapter.setGeneralizationFinished(childTable.getIsGeneralizationFinished());
        adapter.setPretest(childTable.getIsPretest());
        ListView programElementsListView = (ListView) findViewById(R.id.tableContent);
        programElementsListView.addHeaderView(adapter.createHeaderLayout(childTable.getTableTemplate().getName()));
        programElementsListView.setAdapter(adapter);

        EditText noteText = (EditText) findViewById(R.id.noteText);
        noteText.setText(childTable.getNote());

        handleButtonsBehaviors();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.program_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.program_menu, menu);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        ListView tableListView = (ListView) findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());

        ArrayList<String> values = new ArrayList<>();
        for(int i = 0; i < adapter.getCount(); i++) {
            TableElement tableElement = adapter.getItem(i);
            for (int j = 0; j < tableElement.getSize(); j++)
                values.add(tableElement.getValueAtIndex(j));
        }

        bundle.putStringArrayList("FIELDS_FILLINGS", values);
    }

    protected void onRestoreInstanceState(Bundle bundle){
        super.onRestoreInstanceState(bundle);

        if(bundle == null)
            return;

        ArrayList<String> values = bundle.getStringArrayList("FIELDS_FILLINGS");
        ListView tableListView = (ListView) findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());

        for(int i = 0; i < adapter.getCount(); i++) {
            TableElement tableElement = adapter.getItem(i);
            for (int j = 0; j < tableElement.getSize(); j++)
                tableElement.setValueAtIndex(j, values.get(j + i * tableElement.getSize()));
        }
    }

    private TableElement[] getTableElements(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        TableFieldFillingDao fillingDao = daoSession.getTableFieldFillingDao();

        //get all table rows and sort them
        List<TableRow> rows = childTable.getTableTemplate().getTableRowList();
        Collections.sort(rows, new Comparator<TableRow>() {
            public int compare(TableRow row1, TableRow row2) {
                return row1.getInOrder().compareTo(row2.getInOrder());
            }
        });

        TableElement[] tableElements = new TableElement[rows.size()];

        for(int i = 0; i < rows.size(); i++){
            TableRow row = rows.get(i);

            //get all row's fields and sort them
            List<TableField> fields = row.getTableFieldList();
            Collections.sort(fields, new Comparator<TableField>() {
                public int compare(TableField field1, TableField field2) {
                    return field1.getInOrder().compareTo(field2.getInOrder());
                }
            });

            String[] values = new String[fields.size()];
            //find field's contents

            for(int j = 0; j < fields.size(); j++){
                TableField field = fields.get(j);
                QueryBuilder qb = fillingDao.queryBuilder();
                qb.where(TableFieldFillingDao.Properties.ChildTableId.eq(childTable.getId()),
                        TableFieldFillingDao.Properties.TableFieldId.eq(field.getId()));

                TableFieldFilling fieldFilling = ((List<TableFieldFilling>)qb.list()).get(0);

                values[j] = fieldFilling.getContent();
            }

            tableElements[i] = new TableElement(row.getValue(), values);
        }

        return tableElements;
    }

    private void handleButtonsBehaviors() {
        Button buttonSave = (Button) findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveChangesInTable(true, true);
                Toast.makeText(ProgramActivity.this, "Dane zostały zapisane.", Toast.LENGTH_LONG).show();
                new SendSyncTask().execute();
            }
        });

        Button buttonEndTeaching = (Button) findViewById(R.id.buttonEndTeaching);
        if(childTable.getIsTeachingFinished() || !childTable.getIsTeachingCollected())
            buttonEndTeaching.setEnabled(false);

        buttonEndTeaching.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (canFinishTeaching()) {
                    alertDialog = new AlertDialog.Builder(ProgramActivity.this)
                            .setTitle(R.string.warning)
                            .setMessage(R.string.finish_teaching_warning)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finishTeaching();
                                    new SendSyncTask().execute();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        Button buttonEndGeneral = (Button) findViewById(R.id.buttonEndGeneral);
        if(childTable.getIsGeneralizationFinished() || !childTable.getIsGeneralizationCollected())
            buttonEndGeneral.setEnabled(false);

        buttonEndGeneral.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (canFinishGeneralization()) {
                    alertDialog = new AlertDialog.Builder(ProgramActivity.this)
                            .setTitle(R.string.warning)
                            .setMessage(R.string.finish_generalization_warning)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finishGeneralization();
                                    new SendSyncTask().execute();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        Button buttonRestart = (Button) findViewById(R.id.buttonRestart);
        //disable restart button if it's not actual term, table is IOA or if neither teaching nor generalization is collected
        if(childTable.getIsIOA() ||
                (!childTable.getIsTeachingFinished()) && !childTable.getIsGeneralizationFinished())
            buttonRestart.setEnabled(false);

        buttonRestart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialog = new AlertDialog.Builder(ProgramActivity.this)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.restart_table_warning)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                cleanTable();
                                new SendSyncTask().execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    private HashMap<String, Integer> getFieldsNumber(){
        TableRow tableRow = childTable.getTableTemplate().getTableRowList().get(0);
        List<TableField> tableFields = tableRow.getTableFieldList();

        HashMap<String, Integer> fieldsNumber = new HashMap();
        fieldsNumber.put("U", 0);
        fieldsNumber.put("G", 0);
        for(TableField tableField : tableFields){
            String type = tableField.getType();
            fieldsNumber.put(type, fieldsNumber.get(type) + 1);
        }

        return fieldsNumber;
    }

    private Child getChild(long id){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ChildDao childDao = daoSession.getChildDao();
        return childDao.load(id);
    }

    private ChildTable getChildTable(long id){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ChildTableDao childTableDao = daoSession.getChildTableDao();
        return childTableDao.load(id);
    }

    private void saveChangesInTable(boolean saveTeaching, boolean saveGeneralization){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        TableFieldFillingDao fillingDao = daoSession.getTableFieldFillingDao();

        ListView tableListView = (ListView) findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());

        //get all table rows and sort them
        List<TableRow> rows = childTable.getTableTemplate().getTableRowList();
        Collections.sort(rows, new Comparator<TableRow>() {
            public int compare(TableRow row1, TableRow row2) {
                return row1.getInOrder().compareTo(row2.getInOrder());
            }
        });

        boolean changedTeaching = false, changedGeneralization = false , changedNote = false;
        for(int i = 0; i < rows.size(); i++){
            TableRow row = rows.get(i);

            //get all row's fields and sort them
            List<TableField> fields = row.getTableFieldList();
            Collections.sort(fields, new Comparator<TableField>() {
                public int compare(TableField field1, TableField field2) {
                    return field1.getInOrder().compareTo(field2.getInOrder());
                }
            });

            //get data set by user
            TableElement tableElement = adapter.getItem(i);

            //update fields fillings
            for(int j = 0; j < fields.size(); j++){
                TableField field = fields.get(j);

                QueryBuilder qb = fillingDao.queryBuilder();
                qb.where(TableFieldFillingDao.Properties.ChildTableId.eq(childTable.getId()),
                        TableFieldFillingDao.Properties.TableFieldId.eq(field.getId()));
                TableFieldFilling fieldFilling = ((List<TableFieldFilling>)qb.list()).get(0);

                String newValue = tableElement.getValueAtIndex(j);
                if(!newValue.equals(fieldFilling.getContent())){
                    //determine if data should be updated
                    if(field.getType().equals("U") && saveTeaching){
                        changedTeaching = true;
                        fieldFilling.setContent(newValue);
                        fillingDao.insertOrReplace(fieldFilling);
                    }
                    else if(field.getType().equals("G") && saveGeneralization){
                        changedGeneralization = true;
                        fieldFilling.setContent(newValue);
                        fillingDao.insertOrReplace(fieldFilling);
                    }
                }
            }
        }

        //update note
        EditText noteText = (EditText) findViewById(R.id.noteText);

        if(!childTable.getNote().equals(noteText.getText().toString()))
            changedNote = true;
        childTable.setNote(noteText.getText().toString());

        //set save dates
        Date date = getActualTime();

        if(changedTeaching) {
            childTable.setTeachingFillOutDate(date);
        }
        if(changedGeneralization) {
            childTable.setGeneralizationFillOutDate(date);
        }
        if(changedTeaching || changedGeneralization || changedNote)
            childTable.setLastEditDate(date);

        daoSession.getChildTableDao().insertOrReplace(childTable);
    }

    private void cleanTable(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        //clean all table fields
        ListView tableListView = (ListView) findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());
        for(int i = 0; i < adapter.getCount(); i++) {
            TableElement tableElement = adapter.getItem(i);
            for (int j = 0; j < tableElement.getSize(); j++)
                tableElement.setValueAtIndex(j, "empty");
        }

        //save table to db
        if(childTable.getIsTeachingCollected()){
            adapter.setTeachingFinished(false);
            childTable.setIsTeachingFinished(false);
        }

        if(childTable.getIsGeneralizationCollected()) {
            adapter.setGeneralizationFinished(false);
            childTable.setIsGeneralizationFinished(false);
        }

        saveChangesInTable(childTable.getIsTeachingCollected(), childTable.getIsGeneralizationCollected());

        childTable.setTeachingFillOutDate(null);
        childTable.setGeneralizationFillOutDate(null);
        childTable.setLastEditDate(new Date());

        daoSession.getChildTableDao().insertOrReplace(childTable);
        adapter.notifyDataSetChanged();

        //disable restart button and enable other buttons
        Button buttonRestart = (Button) findViewById(R.id.buttonRestart);
        buttonRestart.setEnabled(false);
        Button buttonEndGeneral = (Button) findViewById(R.id.buttonEndGeneral);
        buttonEndGeneral.setEnabled(childTable.getIsGeneralizationCollected());
        Button buttonEndTeaching = (Button) findViewById(R.id.buttonEndTeaching);
        buttonEndTeaching.setEnabled(childTable.getIsTeachingCollected());
    }

    private boolean checkForChanges(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        TableFieldFillingDao fillingDao = daoSession.getTableFieldFillingDao();

        ListView tableListView = (ListView) findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());

        //get all table rows and sort them
        List<TableRow> rows = childTable.getTableTemplate().getTableRowList();
        Collections.sort(rows, new Comparator<TableRow>() {
            public int compare(TableRow row1, TableRow row2) {
                return row1.getInOrder().compareTo(row2.getInOrder());
            }
        });

        for(int i = 0; i < rows.size(); i++){
            TableRow row = rows.get(i);

            //get all row's fields and sort them
            List<TableField> fields = row.getTableFieldList();
            Collections.sort(fields, new Comparator<TableField>() {
                public int compare(TableField field1, TableField field2) {
                    return field1.getInOrder().compareTo(field2.getInOrder());
                }
            });

            //get data set by user
            TableElement tableElement = adapter.getItem(i);

            //update fields fillings
            for(int j = 0; j < fields.size(); j++){
                TableField field = fields.get(j);

                QueryBuilder qb = fillingDao.queryBuilder();
                qb.where(TableFieldFillingDao.Properties.ChildTableId.eq(childTable.getId()),
                        TableFieldFillingDao.Properties.TableFieldId.eq(field.getId()));
                TableFieldFilling fieldFilling = ((List<TableFieldFilling>)qb.list()).get(0);

                String newValue = tableElement.getValueAtIndex(j);
                if(!newValue.equals(fieldFilling.getContent())){
                    return true;
                }
            }
        }

        EditText noteText = (EditText) findViewById(R.id.noteText);
        if(!childTable.getNote().equals(noteText.getText().toString()))
            return true;

        return false;
    }

    private boolean canFinishTeaching(){
        ListView tableListView = (ListView) findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());
        HashMap<String,Integer> fieldsNumber = getFieldsNumber();

        //check if all fields of given type are filled
        for(int i = 0; i < adapter.getCount(); i++){
            TableElement tableElement = adapter.getItem(i);
            for(int j = 0; j < fieldsNumber.get("U"); j++){
                if(tableElement.getValueAtIndex(j).equals("empty")) {
                    Toast.makeText(this, "Nie wszystkie pola uczenia zostały wypełnione.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }

    private boolean canFinishGeneralization(){
        ListView tableListView = (ListView) findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());
        HashMap<String,Integer> fieldsNumber = getFieldsNumber();

        //check if all generalization fields are filled
        for(int i = 0; i < adapter.getCount(); i++){
            TableElement tableElement = adapter.getItem(i);
            for(int j = fieldsNumber.get("U"); j < fieldsNumber.get("U") + fieldsNumber.get("G"); j++){
                if(tableElement.getValueAtIndex(j).equals("empty")) {
                    Toast.makeText(this, "Nie wszystkie pola generalizacji zostały wypełnione.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        return true;
    }

    private void finishTeaching(){
        //save changes and set teaching to finished
        saveChangesInTable(true, false);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        childTable.setIsTeachingFinished(true);
        childTable.setLastEditDate(new Date());
        daoSession.getChildTableDao().insertOrReplace(childTable);

        //if both teaching and generalization are finished return to program list
        if(isEverythingFinished())
            finish();

        //manage buttons
        Button buttonEndTeaching = (Button) findViewById(R.id.buttonEndTeaching);
        buttonEndTeaching.setEnabled(false);
        Button buttonRestart = (Button) findViewById(R.id.buttonRestart);
        buttonRestart.setEnabled(true);

        ListView tableListView = (ListView) findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());
        adapter.setTeachingFinished(true);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Uczenie zostało zakończone.", Toast.LENGTH_SHORT).show();
    }

    private void finishGeneralization(){
        //save changes and set generalization to finished
        saveChangesInTable(false, true);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        childTable.setIsGeneralizationFinished(true);
        childTable.setLastEditDate(new Date());
        daoSession.getChildTableDao().insertOrReplace(childTable);

        //if both teaching and generalization are finished return to program list
        if(isEverythingFinished())
            finish();

        //manage buttons
        Button buttonEndGeneral = (Button) findViewById(R.id.buttonEndGeneral);
        buttonEndGeneral.setEnabled(false);
        Button buttonRestart = (Button) findViewById(R.id.buttonRestart);
        buttonRestart.setEnabled(true);

        ListView tableListView = (ListView) findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());
        adapter.setGeneralizationFinished(true);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Generalizacja została zakończona.", Toast.LENGTH_LONG).show();
    }

    private Date getActualTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date dateNow = null;
        try {
            dateNow = formatter.parse(c.get(Calendar.YEAR) + "/" + ((c.get(Calendar.MONTH))+1) + "/" + c.get(Calendar.DAY_OF_MONTH) +
                            " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateNow;
    }

    private boolean isEverythingFinished(){
        boolean teachingFinished = false, generalizationFinished = false;
        if((childTable.getIsTeachingCollected() && childTable.getIsTeachingFinished()) ||
                !childTable.getIsTeachingCollected())
            teachingFinished = true;

        if((childTable.getIsGeneralizationCollected() && childTable.getIsGeneralizationFinished()) ||
                !childTable.getIsGeneralizationCollected())
            generalizationFinished = true;

        if(teachingFinished && generalizationFinished)
            return true;

        return false;
    }

    public void onBackPressed() {
        if(checkForChanges()) {
            alertDialog = new AlertDialog.Builder(ProgramActivity.this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.back_button_warning)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else
            finish();
    }

    //method necessary for tests
    public AlertDialog getLastAlertDialog(){
        return alertDialog;
    }

    private class SendSyncTask extends AsyncTask<Void, Void, ReceiveSyncModel> {

        private SyncManager syncManager;
        @Override
        protected ReceiveSyncModel doInBackground(Void... params) {
            ReceiveSyncModel receiveSyncModel = null;
            try {
                syncManager = new SyncManager(ProgramActivity.this);
                receiveSyncModel = syncManager.exchangeModelsWithSever();

            } catch (Exception e) {
                Log.e("ProgramActivity", e.getMessage(), e);
            }

            return receiveSyncModel;
        }

        @Override
        protected void onPostExecute(ReceiveSyncModel receiveSyncModel) {
            try {
                if(receiveSyncModel != null) {
                    syncManager.updateDatabaseAfterSync(receiveSyncModel);
                    Toast.makeText(ProgramActivity.this, getResources().getString(R.string.sync_succeed), Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception c)
            {
                Log.e("ProgramActivity", c.getMessage(), c);
            }
        }
    }
}
