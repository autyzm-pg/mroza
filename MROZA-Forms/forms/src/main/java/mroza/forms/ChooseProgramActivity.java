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

import adapters.ChildTableListViewAdapter;
import repositories.ChildRepository;
import repositories.ChildTablesRepository;
import repositories.TermSolutionRepository;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import database.*;
import syncmodels.ReceiveSyncModel;
import syncmodels.SyncManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChooseProgramActivity extends AppCompatActivity {

    public enum Term {HISTORICAL, ACTUAL, FUTURE}

    private Child child;

    private Menu menu;
    private List<MenuItem> selectedFilters = new ArrayList<>();
    private MenuItem letterFilter = null;
    private String searchQuery = null;
    private Bundle savedState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            child = ChildRepository.getChildById(ChooseProgramActivity.this, extras.getLong("CHILD_ID"));
        }
        savedState = savedInstanceState;

        setTitle(child.getCode());
        setContentView(R.layout.choose_program);

        Term term = Term.ACTUAL;
        if ((savedState != null) && (savedState.containsKey("TERM"))) {
            term = Term.values()[savedState.getInt("TERM")];
            savedState.remove("TERM");
        }

        displayTerm(term);
        handleButtonsBehaviour();
    }


    @Override
    public void onResume() {
        super.onResume();

        boolean bundleQueryExists = false;
        if ((savedState != null) && (savedState.containsKey("SEARCH_FILTER")))
            bundleQueryExists = true;

        Intent intent = getIntent();
        Term selectedTerm = getSelectedTerm();
        //if called by search event perform search
        if (Intent.ACTION_SEARCH.equals(intent.getAction()) || bundleQueryExists) {
            clearFilters(true);//remove filter values - cannot use filter and search simultaneously

            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                searchQuery = intent.getStringExtra(SearchManager.QUERY);
            }
            else {
                searchQuery = savedState.getString("SEARCH_FILTER");
                savedState.remove("SEARCH_FILTER");
            }


            List<ChildTable> queriedChildTables = ChildTablesRepository.getChildTablesByTermSelectedBySearch(ChooseProgramActivity.this, searchQuery, selectedTerm, child);
            handleListViewBehavior(queriedChildTables, selectedTerm);
            enableShowAllButton(true);
            intent.setAction(null); //clear search action
            return;
        }

        //if returning from child table view show all childTableList and clear filters
        List<ChildTable> childTables = ChildTablesRepository.getChildTableByTerm(ChooseProgramActivity.this, selectedTerm, child);
        handleListViewBehavior(childTables, selectedTerm);

        clearFilters(true);
        clearSearchField();
        enableShowAllButton(false);

    }

    private void handleButtonsBehaviour() {
        final Button buttonPrevious = (Button) findViewById(R.id.buttonChangePeriodHistorical);
        final Button buttonFuture = (Button) findViewById(R.id.buttonChangePeriodFuture);

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!buttonFuture.isEnabled()) {
                    //Switch to actual
                    displayTerm(Term.ACTUAL);
                } else {
                    //Switch to previous
                    displayTerm(Term.HISTORICAL);
                }

                clearFilters(true);
                clearSearchField();
                enableShowAllButton(false);
            }
        });

        buttonFuture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!buttonPrevious.isEnabled()) {
                    //Switch to actual period
                    displayTerm(Term.ACTUAL);
                } else {
                    //Switch to future period
                    displayTerm(Term.FUTURE);
                }

                clearFilters(true);
                clearSearchField();
                enableShowAllButton(false);
            }
        });
    }

    private void syncData() {

        SyncManager syncManager = new SyncManager(ChooseProgramActivity.this);
        Date lastSyncDate =  syncManager.getLastSyncDate();
        if(lastSyncDate == null)
            new ReceiveAllSyncTask().execute();
        else
            new ReceiveSyncTask().execute();
    }

    private void showLastSyncDate() {
        SyncManager syncManager = new SyncManager(ChooseProgramActivity.this);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date lastSyncDate =  syncManager.getLastSyncDate();
        if(lastSyncDate == null)
            Toast.makeText(ChooseProgramActivity.this, getResources().getString(R.string.sync_had_not_been_done_yet), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(ChooseProgramActivity.this, getResources().getString(R.string.sync_had_been_done) + dateFormat.format(lastSyncDate), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sync_now:
                syncData();
                return true;
            case R.id.sync_date:
                showLastSyncDate();
                return true;
            case R.id.teaching_ended:
            case R.id.teaching_not_started:
            case R.id.teaching_saved:
            case R.id.general_ended:
            case R.id.general_not_started:
            case R.id.general_saved:
                if (item.isChecked()) {
                    item.setChecked(false);
                    selectedFilters.remove(item);
                } else {
                    item.setChecked(true);
                    selectedFilters.add(item);
                    letterFilter = null;
                }
                filterPrograms();
                return true;
            case R.id.choose_letter_A:
            case R.id.choose_letter_B:
            case R.id.choose_letter_C:
            case R.id.choose_letter_D:
            case R.id.choose_letter_E:
            case R.id.choose_letter_F:
            case R.id.choose_letter_G:
            case R.id.choose_letter_H:
            case R.id.choose_letter_I:
            case R.id.choose_letter_J:
            case R.id.choose_letter_K:
            case R.id.choose_letter_L:
            case R.id.choose_letter_M:
            case R.id.choose_letter_N:
            case R.id.choose_letter_O:
            case R.id.choose_letter_P:
            case R.id.choose_letter_Q:
            case R.id.choose_letter_R:
            case R.id.choose_letter_S:
            case R.id.choose_letter_T:
            case R.id.choose_letter_U:
            case R.id.choose_letter_V:
            case R.id.choose_letter_W:
            case R.id.choose_letter_X:
            case R.id.choose_letter_Y:
            case R.id.choose_letter_Z:
                if (item.isChecked()) {
                    item.setChecked(false);
                    letterFilter = null;
                } else {
                    item.setChecked(true);
                    letterFilter = item;
                }
                filterPrograms();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        getMenuInflater().inflate(R.menu.choose_program_menu, menu);

        //Associate programs_searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchPlate.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchPlate.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //On search or enter clicked
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL) {
                    if (v.getText().toString().equals("")) {
                        refreshChildTableList();
                        clearSearchField();
                        clearFilters(true);
                        enableShowAllButton(false);
                    }
                }
                return false;
            }
        });

        restoreSavedFilters();
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.choose_program_menu, menu);

    }

    @Override
    //Method handling search queries
    protected void onNewIntent(Intent intent) {
        intent.putExtra("CHILD_ID", child.getId());
        setIntent(intent);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        //save filters
        if (selectedFilters.size() > 0) {
            ArrayList<Integer> filters = new ArrayList<>();
            for (MenuItem selectedFilter : selectedFilters) {
                filters.add(selectedFilter.getItemId());
            }
            bundle.putIntegerArrayList("FILTERS", filters);
        }

        //save letter filter
        if (letterFilter != null)
            bundle.putInt("LETTER_FILTER", letterFilter.getItemId());

        //save search query
        if (searchQuery != null)
            bundle.putString("SEARCH_FILTER", searchQuery);

        //save selected term
        Term term = getSelectedTerm();
        bundle.putInt("TERM", term.ordinal());
    }

    //Has to be called when menu is created
    private void restoreSavedFilters() {
        if (savedState == null)
            return;

        boolean addedFilters = false;
        //restore filters
        if (savedState.containsKey("FILTERS")) {
            ArrayList<Integer> filters = savedState.getIntegerArrayList("FILTERS");
            for (Integer filter : filters) {
                MenuItem item = menu.findItem(filter);
                selectedFilters.add(item);
                item.setChecked(true);
            }
            savedState.remove("FILTERS");
            addedFilters = true;
        }

        //restore letter filter
        if (savedState.containsKey("LETTER_FILTER")) {
            letterFilter = menu.findItem(savedState.getInt("LETTER_FILTER"));
            savedState.remove("LETTER_FILTER");
            addedFilters = true;
        }

        if (addedFilters)
            filterPrograms();
    }

    private void handleListViewBehavior(final List<ChildTable> childTables, Term term) {
        final ListView childTableList = (ListView) findViewById(R.id.childTable_list);
        ChildTableListViewAdapter adapter = new ChildTableListViewAdapter(this, childTables, term);
        childTableList.setAdapter(adapter);


        childTableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChooseProgramActivity.this, ProgramActivity.class);
                ChildTable childTable = childTables.get(position);
                intent.putExtra("CHILD_TABLE_ID", childTable.getId());
                intent.putExtra("CHILD_ID", child.getId());
                ChooseProgramActivity.this.startActivity(intent);
            }
        });

    }

    private void setDateTextFields(List<ChildTable> childTables, Term term) {
        final TextView periodDateStart = (TextView) findViewById(R.id.termSolutionStartDate);
        final TextView periodDateEnd = (TextView) findViewById(R.id.termSolutionEndDate);
        final TextView between = (TextView) findViewById(R.id.textBetween);

        //print dates info
        if (!childTables.isEmpty()) {
            TermSolution termSolution = TermSolutionRepository.getTermSolutionByTerm(ChooseProgramActivity.this, term, child);
            periodDateStart.setText(getDateToDisplay(termSolution.getStartDate()));
            periodDateEnd.setText(getDateToDisplay(termSolution.getEndDate()));
            between.setText(R.string.between_dates);
        } else {
            periodDateStart.setText(R.string.empty_term_solution);
            between.setText(R.string.empty);
            periodDateEnd.setText(R.string.empty);
        }
    }

    private String getDateToDisplay(Date date) {
        DateFormat dataFormat = new SimpleDateFormat("dd-MMM-yyyy");
        return dataFormat.format(date);
    }

    private void filterPrograms() {
        clearSearchField(); //remove search query - cannot use filter and search simultaneously

        List<ChildTable> childTables = new ArrayList<>();
        Term term = getSelectedTerm();
        if (letterFilter != null) {
            clearFilters(false);
            String letter = getLetterFromId(letterFilter.getItemId());
            childTables = ChildTablesRepository.getChildTablesWhereProgramsSymbolStartingWithLetter(ChooseProgramActivity.this, letter, term, child);
            handleListViewBehavior(childTables, term);
            enableShowAllButton(true);
            return;
        } else
            letterFilter = null;

        if (selectedFilters.size() == 0) {
            childTables = ChildTablesRepository.getChildTableByTerm(ChooseProgramActivity.this, term, child);

            handleListViewBehavior(childTables, term);
            enableShowAllButton(false);
            return;
        }

        for (MenuItem filter : selectedFilters) {
            if (filter.getItemId() == R.id.teaching_not_started)
                addChildTablesIfNotOnList(childTables, ChildTablesRepository.getTeachingNotStartedChildTables(ChooseProgramActivity.this, term, child));
            else if (filter.getItemId() == R.id.teaching_saved)
                addChildTablesIfNotOnList(childTables, ChildTablesRepository.getTeachingSavedChildTables(ChooseProgramActivity.this, term, child));
            else if (filter.getItemId() == R.id.teaching_ended)
                addChildTablesIfNotOnList(childTables, ChildTablesRepository.getTeachingFinishedChildTables(ChooseProgramActivity.this, term, child));
            else if (filter.getItemId() == R.id.general_not_started)
                addChildTablesIfNotOnList(childTables, ChildTablesRepository.getGeneralizationNotStartedChildTables(ChooseProgramActivity.this, term, child));
            else if (filter.getItemId() == R.id.general_saved)
                addChildTablesIfNotOnList(childTables, ChildTablesRepository.getGeneralizationSavedChildTables(ChooseProgramActivity.this, term, child));
            else if (filter.getItemId() == R.id.general_ended)
                addChildTablesIfNotOnList(childTables, ChildTablesRepository.getGeneralizationFinishedChildTables(ChooseProgramActivity.this, term, child));

            handleListViewBehavior(childTables, term);
            enableShowAllButton(true);
        }
    }

    private void addChildTablesIfNotOnList(List<ChildTable> childTables, List<ChildTable> newChildTables) {
        for (ChildTable newChildTable : newChildTables) {
            boolean addChildTable = true;
            for (ChildTable childTable : childTables) {
                if (childTable.getId().equals(newChildTable.getId())) {
                    addChildTable = false;
                    break;
                }
            }

            if (addChildTable)
                childTables.add(newChildTable);
        }
    }

    private void enableShowAllButton(boolean enable) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.LayoutGrid);
        Button button = (Button) findViewById(R.id.show_all_programs_button_id);

        if (enable && button == null) {
            button = new Button(this);
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setText(R.string.show_all_programs);
            button.setId(R.id.show_all_programs_button_id);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    List<ChildTable> childTables;
                    clearFilters(true);
                    clearSearchField();

                    Term selectedTerm = getSelectedTerm();
                    childTables = ChildTablesRepository.getChildTableByTerm(ChooseProgramActivity.this, selectedTerm, child);
                    handleListViewBehavior(childTables, selectedTerm);

                    enableShowAllButton(false);
                }
            });
            layout.addView(button, 1);
        } else if (!enable && button != null) {
            layout.removeView(button);
        }
    }

    private void clearSearchField() {
        if (menu != null) {
            android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setQuery("", false);
            MenuItemCompat.collapseActionView(menu.findItem(R.id.action_search));
        }

        searchQuery = null;
    }

    private void clearFilters(boolean clearLetterFilter) {
        if (selectedFilters.size() > 0) {
            selectedFilters.clear();
            menu.findItem(R.id.teaching_not_started).setChecked(false);
            menu.findItem(R.id.teaching_saved).setChecked(false);
            menu.findItem(R.id.teaching_ended).setChecked(false);
            menu.findItem(R.id.general_not_started).setChecked(false);
            menu.findItem(R.id.general_saved).setChecked(false);
            menu.findItem(R.id.general_ended).setChecked(false);
        }

        if (clearLetterFilter)
            letterFilter = null;
    }

    private String getLetterFromId(int id) {
        String letter = null;
        if (id == R.id.choose_letter_A) letter = "A";
        else if (id == R.id.choose_letter_B) letter = "B";
        else if (id == R.id.choose_letter_C) letter = "C";
        else if (id == R.id.choose_letter_D) letter = "D";
        else if (id == R.id.choose_letter_E) letter = "E";
        else if (id == R.id.choose_letter_F) letter = "F";
        else if (id == R.id.choose_letter_G) letter = "G";
        else if (id == R.id.choose_letter_H) letter = "H";
        else if (id == R.id.choose_letter_I) letter = "I";
        else if (id == R.id.choose_letter_J) letter = "J";
        else if (id == R.id.choose_letter_K) letter = "K";
        else if (id == R.id.choose_letter_L) letter = "L";
        else if (id == R.id.choose_letter_M) letter = "M";
        else if (id == R.id.choose_letter_N) letter = "N";
        else if (id == R.id.choose_letter_O) letter = "O";
        else if (id == R.id.choose_letter_P) letter = "P";
        else if (id == R.id.choose_letter_Q) letter = "Q";
        else if (id == R.id.choose_letter_R) letter = "R";
        else if (id == R.id.choose_letter_S) letter = "S";
        else if (id == R.id.choose_letter_T) letter = "T";
        else if (id == R.id.choose_letter_U) letter = "U";
        else if (id == R.id.choose_letter_V) letter = "V";
        else if (id == R.id.choose_letter_W) letter = "W";
        else if (id == R.id.choose_letter_X) letter = "X";
        else if (id == R.id.choose_letter_Y) letter = "Y";
        else if (id == R.id.choose_letter_Z) letter = "Z";
        return letter;
    }

    private Term getSelectedTerm() {
        final Button buttonPrevious = (Button) findViewById(R.id.buttonChangePeriodHistorical);
        final Button buttonFuture = (Button) findViewById(R.id.buttonChangePeriodFuture);

        if (buttonFuture.isEnabled() && buttonPrevious.isEnabled())
            return Term.ACTUAL;
        else if (!buttonFuture.isEnabled())
            return Term.FUTURE;
        else
            return Term.HISTORICAL;
    }

    private void displayTerm(Term term) {
        final Button buttonPrevious = (Button) findViewById(R.id.buttonChangePeriodHistorical);
        final Button buttonFuture = (Button) findViewById(R.id.buttonChangePeriodFuture);

        List<ChildTable> childTables;
        childTables = ChildTablesRepository.getChildTableByTerm(ChooseProgramActivity.this, term, child);
        if (term == Term.ACTUAL) {
            buttonPrevious.setEnabled(true);
            buttonPrevious.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fast_rewind_white_24dp, 0, 0, 0);
            buttonFuture.setEnabled(true);
            buttonFuture.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fast_forward_white_24dp, 0, 0, 0);
        } else if (term == Term.HISTORICAL) {
            buttonPrevious.setEnabled(false);
            buttonPrevious.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fast_rewind_black_24dp, 0, 0, 0);
            buttonFuture.setEnabled(true);
            buttonFuture.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fast_forward_white_24dp, 0, 0, 0);
        } else {
            buttonPrevious.setEnabled(true);
            buttonPrevious.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fast_rewind_white_24dp, 0, 0, 0);
            buttonFuture.setEnabled(false);
            buttonFuture.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fast_forward_black_24dp, 0, 0, 0);
        }

        setDateTextFields(childTables, term);
        handleListViewBehavior(childTables, term);
    }

    private class ReceiveSyncTask extends AsyncTask<Void, Void, ReceiveSyncModel> {
        private SyncManager syncManager;

        @Override
        protected ReceiveSyncModel doInBackground(Void... params) {
            ReceiveSyncModel receiveSyncModel = null;
            try {
                syncManager = new SyncManager(ChooseProgramActivity.this);
                receiveSyncModel = syncManager.exchangeModelsWithSever();
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return receiveSyncModel;
        }


        @Override
        protected void onPostExecute(ReceiveSyncModel receiveSyncModel) {
            try {
                if(receiveSyncModel != null) {
                    syncManager.updateDatabaseAfterSync(receiveSyncModel);
                    refreshChildTableList();
                    Toast.makeText(ChooseProgramActivity.this, getResources().getString(R.string.sync_succeed), Toast.LENGTH_LONG).show();
                }
            } catch (Exception c) {
                Log.e("MainActivity", c.getMessage(), c);
            }

        }
    }

    private class ReceiveAllSyncTask extends AsyncTask<Void, Void, ReceiveSyncModel> {

        private SyncManager syncManager;
        @Override
        protected ReceiveSyncModel doInBackground(Void... params) {
            ReceiveSyncModel receiveSyncModel = null;
            try {
                syncManager = new SyncManager(ChooseProgramActivity.this);
                receiveSyncModel = syncManager.receiveSyncModelFromServer();
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return receiveSyncModel;
        }

        @Override
        protected void onPostExecute(ReceiveSyncModel receiveSyncModel) {
            try {

                if(receiveSyncModel != null) {
                    syncManager.updateDatabaseAfterReceiveAllData(receiveSyncModel);
                    refreshChildTableList();
                    Toast.makeText(ChooseProgramActivity.this, getResources().getString(R.string.sync_succeed) , Toast.LENGTH_LONG).show();
                }
            } catch (Exception c) {
                Log.e("MainActivity", c.getMessage(), c);
            }

        }
    }

    private void refreshChildTableList() {
        Term selectedTerm = getSelectedTerm();
        List<ChildTable> childTables = ChildTablesRepository.getChildTableByTerm(ChooseProgramActivity.this, selectedTerm, child);
        handleListViewBehavior(childTables, selectedTerm);
    }


}
