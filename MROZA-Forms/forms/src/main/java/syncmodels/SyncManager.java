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

package syncmodels;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import database.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import de.greenrobot.dao.query.QueryBuilder;

public class SyncManager {

    public enum SYNC_OPTION{
        RECEIVE_ALL_DATA, RECEIVE_LAST_UPDATES, UPDATES_SERVER, SERVER_BASE, SEND_UPDATES;
    }
    private final Context activityContext;

    public SyncManager(Context activityContext)
    {
        this.activityContext = activityContext;
    }

    public void setUrlToMrozaServer(String url) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activityContext);
        SharedPreferences.Editor editor = preferences.edit();

        String url_to_send_updates = "http://" + url + "/rest/syncData";
        String url_to_get_all_data = "http://" + url + "/rest/getAllData";

        editor.putString("URL_TO_SERVER", url);
        editor.putString("URL_TO_SEND_UPDATE", url_to_send_updates);
        editor.putString("URL_TO_RECEIVE_ALL", url_to_get_all_data);
        editor.commit();

    }

    public void setUrlToUpdateServer(String urlToUpdateApp) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activityContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("URL_TO_UPDATES_SERVER", "http://" + urlToUpdateApp);
        editor.commit();

    }

    public String getUrlToServer(SYNC_OPTION sync_option) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activityContext);
        String urlToServer = "";
        switch (sync_option)
        {
            case RECEIVE_ALL_DATA:
                urlToServer = preferences.getString("URL_TO_RECEIVE_ALL", "http://10.0.2.2:8080");
                break;
            case SEND_UPDATES:
                urlToServer = preferences.getString("URL_TO_SEND_UPDATE", "http://10.0.2.2:8080");
                break;
            case RECEIVE_LAST_UPDATES:
                urlToServer = preferences.getString("URL_TO_RECEIVE_LAST_UPDATE", "http://10.0.2.2:8080");
                break;
            case UPDATES_SERVER:
                urlToServer = preferences.getString("URL_TO_UPDATES_SERVER", "http://10.0.2.2:8080");
                break;
            case SERVER_BASE:
                urlToServer = preferences.getString("URL_TO_SERVER", "10.0.2.2:8080");
                break;
        }
        return urlToServer;

    }

    public void setSyncDate(Date date){
        DaoSession daoSession = getDaoSession();
        SyncDateDao syncDateDao = daoSession.getSyncDateDao();
        if(syncDateDao.loadAll().size() > 0)
            syncDateDao.deleteAll();

        SyncDate syncDate = new SyncDate();
        syncDate.setDate(date);

        syncDateDao.insertOrReplace(syncDate);
    }

    public Date getLastSyncDate(){
        DaoSession daoSession = getDaoSession();
        SyncDateDao syncDateDao = daoSession.getSyncDateDao();
        List<SyncDate> listSyncDate =  syncDateDao.loadAll();

        if(listSyncDate.size() < 1)
            return null;

        SyncDate lastDate = listSyncDate.get(0);
        return lastDate.getDate();
    }

    public ReceiveSyncModel exchangeModelsWithSever() {
        SendSyncModel sendSyncModel = getSendSyncModel();
        String url = getUrlToServer(SyncManager.SYNC_OPTION.SEND_UPDATES);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "json"));
        HttpEntity<SendSyncModel> requestEntity = new HttpEntity<SendSyncModel>(sendSyncModel, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ResponseEntity<ReceiveSyncModel> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ReceiveSyncModel.class);
        ReceiveSyncModel receiveSyncModel = responseEntity.getBody();
        return receiveSyncModel;
    }

    public ReceiveSyncModel receiveSyncModelFromServer() {
        String url = getUrlToServer(SyncManager.SYNC_OPTION.RECEIVE_ALL_DATA);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ReceiveSyncModel receiveSyncModel = restTemplate.getForObject(url, ReceiveSyncModel.class);
        return receiveSyncModel;
    }

    public void updateDatabaseAfterReceiveAllData(ReceiveSyncModel receiveSyncModel){
        cleanDatabaseBeforeSync();
        updateDatabaseAfterSync(receiveSyncModel);
    }

    public void updateDatabaseAfterSync(ReceiveSyncModel receiveSyncModel) {
        DatabaseUpdater databaseUpdater = new DatabaseUpdater(activityContext);
        databaseUpdater.updateDatabase(receiveSyncModel);
        setSyncDate(new Date());
    }

    public List<ChildTable> getPendingChanges() {

        DaoSession daoSession = getDaoSession();
        Date lastSyncDate = getLastSyncDate();

        ChildTableDao childTableDao =  daoSession.getChildTableDao();
        QueryBuilder qb = childTableDao.queryBuilder();
        qb.where(ChildTableDao.Properties.LastEditDate.ge(lastSyncDate));

        List<ChildTable> pendingChangesChildTables = qb.list();

        return pendingChangesChildTables;
    }

    public SendSyncModel getSendSyncModel() {
        SendSyncModel sendSyncModel = new SendSyncModel();
        List<ChildTable> childTables =  getPendingChanges();
        List<TransferChildTable> transferChildTableList = new ArrayList<>();
        for(ChildTable childTable : childTables)
        {
            transferChildTableList.add(TransferChildTable.transferObjectFromServerModel(childTable));
        }
        sendSyncModel.setTransferChildTableList(transferChildTableList);
        List<TransferTableFieldFilling> transferTableFieldFillingsList = new ArrayList<>();
        for(ChildTable childTable : childTables)
        {
            for(TableFieldFilling tableFieldFilling : childTable.getTableFieldFilling()) {
                transferTableFieldFillingsList.add(TransferTableFieldFilling.transferObjectFromServerModel(tableFieldFilling));
            }
        }
        sendSyncModel.setTransferTableFieldFillingList(transferTableFieldFillingsList);
        return sendSyncModel;
    }

    private void cleanDatabaseBeforeSync() {
        DatabaseUpdater databaseUpdater =  new DatabaseUpdater(activityContext);
        databaseUpdater.clearUpDb();
    }

    private DaoSession getDaoSession() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(activityContext, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

}
