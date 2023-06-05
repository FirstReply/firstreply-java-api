package ai.firstreply.api;

import ai.firstreply.api.model.*;
import io.reactivex.Single;
import retrofit2.http.*;

public interface FirstReplyApi {

    @GET("/v1/organizations/{organizationId}/contacts/{contactId}")
    Single<Contact> getContact(
            @Path("organizationId") String organizationId,
            @Path("contactId") String contactId);

    @PUT("/v1/organizations/{organizationId}/contacts/{contactId}")
    Single<Contact> updateContact(
            @Path("organizationId") String organizationId,
            @Path("contactId") String contactId,
            @Body ContactUpdate request);

}
