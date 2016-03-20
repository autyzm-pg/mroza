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

import adapters.KidsViewListAdapter;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import database.*;

import repositories.ChildRepository;
import syncmodels.ReceiveSyncModel;
import syncmodels.SyncManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ChooseKidActivity extends AppCompatActivity {

    ListView listView;
    private List<Child> children;
    private KidsViewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_kid);
        List<Child> children = ChildRepository.getChildrenList(this);
        setDisplayedChildren(children);

        //Set list view listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                getIntent().setAction(null); //remove search action so when we get back we'll see all children
                Intent intent = new Intent(ChooseKidActivity.this, ChooseProgramActivity.class);
                Child selectedChild = (Child) listView.getAdapter().getItem(position);
                intent.putExtra("CHILD_ID", selectedChild.getId());
                ChooseKidActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.choose_kid_menu, menu);

        //Associate kids_searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //Workaround to get empty query event
        //http://stackoverflow.com/a/32023365
        EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchPlate.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchPlate.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //On search or enter clicked
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL) {
                    if (v.getText().toString().equals("")) {
                        setDisplayedChildren(ChildRepository.getChildrenList(ChooseKidActivity.this));
                        enableShowAllButton(false);
                    }
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sync_now:
                syncData(getLastSyncDate());
                return true;
            case R.id.sync_date:
                showLastSyncDate();
                return true;
            case R.id.sync_all_with_server:
                syncData(null);
                return true;
            case R.id.settings_server_mroza:
                showInputDialogForMrozaServerUrl();
                return true;
            case R.id.settings_server_app_update:
                showInputDialogForUpdateServerUrl();
                return true;
            case R.id.action_update:
                updateApplication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    //Method handling search queries
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            List<Child> searchedChildren = ChildRepository.getChildrenBySearch(this, query);
            setDisplayedChildren(searchedChildren);
            enableShowAllButton(true);
        } else {
            List<Child> children = ChildRepository.getChildrenList(this);
            setDisplayedChildren(children);
            enableShowAllButton(false);
        }
    }

    private void setDisplayedChildren(List<Child> children) {
        this.children = children;
        listView = (ListView) findViewById(R.id.listView);
        adapter = new KidsViewListAdapter(this, this.children);
        listView.setAdapter(adapter);
    }

    private void enableShowAllButton(boolean enable) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.choose_kid_layout);
        Button button = (Button) findViewById(R.id.show_all_kids_button_id);

        if (enable && button == null) {
            button = new Button(this);
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setText(R.string.show_all_kids);
            button.setId(R.id.show_all_kids_button_id);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    refreshChildListView();
                    enableShowAllButton(false);
                }
            });
            layout.addView(button, 0);
        } else if (!enable && button != null) {
            layout.removeView(button);
            getIntent().setAction(null);
        }
    }

    private void syncData(Date lastSyncDate) {
        if(lastSyncDate == null)
            new ReceiveAllSyncTask().execute();
        else
            new ReceiveSyncTask().execute();
    }

    private void showInputDialogForUpdateServerUrl() {

        LayoutInflater li = LayoutInflater.from(ChooseKidActivity.this);
        View inputUrlDialog = li.inflate(R.layout.url_input_dialog_box, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ChooseKidActivity.this);

        alertDialogBuilder.setView(inputUrlDialog);

        final TextView inputUrlTextDialog = (TextView) inputUrlDialog.findViewById(R.id.inputUrlDialog);
        final EditText inputUrlText = (EditText) inputUrlDialog
                .findViewById(R.id.inputUrlToServer);
        final SyncManager syncManager = new SyncManager(ChooseKidActivity.this);
        String urlBase = syncManager.getUrlToServer(SyncManager.SYNC_OPTION.UPDATES_SERVER);
        if(urlBase != null)
            inputUrlText.setText(urlBase.substring(("http://").length()));

        inputUrlTextDialog.setText(R.string.update_server_url);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.save_changes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                syncManager.setUrlToUpdateServer(inputUrlText.getText().toString());
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showInputDialogForMrozaServerUrl() {
        LayoutInflater li = LayoutInflater.from(ChooseKidActivity.this);
        View inputUrlDialog = li.inflate(R.layout.url_input_dialog_box, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ChooseKidActivity.this);

        alertDialogBuilder.setView(inputUrlDialog);

        final EditText inputUrlText = (EditText) inputUrlDialog
                .findViewById(R.id.inputUrlToServer);
        final SyncManager syncManager = new SyncManager(ChooseKidActivity.this);
        inputUrlText.setText(syncManager.getUrlToServer(SyncManager.SYNC_OPTION.SERVER_BASE));

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.save_changes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SyncManager syncManager = new SyncManager(ChooseKidActivity.this);
                                syncManager.setUrlToMrozaServer(inputUrlText.getText().toString());
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private boolean updateApplication() {
        try {
            SyncManager syncManager = new SyncManager(ChooseKidActivity.this);
            String url = syncManager.getUrlToServer(SyncManager.SYNC_OPTION.UPDATES_SERVER);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(ChooseKidActivity.this, getResources().getString(R.string.application_update_error) , Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void showLastSyncDate() {
        Date lastSyncDate = getLastSyncDate();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if(lastSyncDate == null)
            Toast.makeText(ChooseKidActivity.this,getResources().getString(R.string.sync_had_not_been_done_yet), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(ChooseKidActivity.this, getResources().getString(R.string.sync_had_been_done) + dateFormat.format(lastSyncDate), Toast.LENGTH_LONG).show();
    }

    private void refreshChildListView() {
        List<Child> children = ChildRepository.getChildrenList(ChooseKidActivity.this);
        setDisplayedChildren(children);
        adapter.notifyDataSetChanged();
    }

    private Date getLastSyncDate() {
        SyncManager syncManager = new SyncManager(ChooseKidActivity.this);
        return syncManager.getLastSyncDate();
    }

    private class ReceiveSyncTask extends AsyncTask<Void, Void, ReceiveSyncModel> {

        private SyncManager syncManager;
        @Override
        protected ReceiveSyncModel doInBackground(Void... params) {
            ReceiveSyncModel receiveSyncModel = null;
            syncManager = new SyncManager(ChooseKidActivity.this);
            try {
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
                    refreshChildListView();
                    Toast.makeText(ChooseKidActivity.this,getResources().getString(R.string.sync_succeed), Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e)
            {
                Log.e("MainActivity", e.getMessage(), e);
            }

        }

    }
    private class ReceiveAllSyncTask extends AsyncTask<Void, Void, ReceiveSyncModel> {

        private SyncManager syncManager;

        @Override
        protected ReceiveSyncModel doInBackground(Void... params) {
            ReceiveSyncModel receiveSyncModel = null;
            syncManager = new SyncManager(ChooseKidActivity.this);
            try {
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
                    refreshChildListView();
                    Toast.makeText(ChooseKidActivity.this, getResources().getString(R.string.sync_succeed), Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e)
            {
                Log.e("MainActivity", e.getMessage(), e);
            }

        }

    }


}


