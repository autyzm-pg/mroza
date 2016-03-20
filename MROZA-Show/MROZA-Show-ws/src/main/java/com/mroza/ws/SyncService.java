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

package com.mroza.ws;

import com.mroza.models.transfermodels.ReceiveSyncModel;
import com.mroza.models.transfermodels.SendSyncModel;
import com.mroza.qualifiers.SyncServiceImpl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequestScoped
@Path("/")
public class SyncService {

    @Inject
    @SyncServiceImpl
    private com.mroza.interfaces.SyncService syncService;


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getAllData")
    public Response getAllData() {
        try {
            SendSyncModel syncModel = syncService.getAllData();
            return Response.status(Response.Status.OK).entity(syncModel).build();
        }
        catch (Exception exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/syncData")
    public Response syncData(ReceiveSyncModel receiveSyncModel) {
        try {
            String str = "10-10-2014";
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf.parse(str);
            syncService.updateModelWithDataFromAndroid(receiveSyncModel);
            SendSyncModel syncModel = syncService.getDataAfterLastSync(date);
            return Response.status(Response.Status.OK).entity(syncModel).build();
        }
        catch (Exception exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
