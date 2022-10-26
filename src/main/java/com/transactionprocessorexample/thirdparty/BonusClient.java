package com.transactionprocessorexample.thirdparty;

import com.transactionprocessorexample.model.BonusRequest;
import com.transactionprocessorexample.model.BonusResponse;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/bonus")
@RegisterRestClient(configKey = "bonus-api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface BonusClient {

    @POST
    @Retry(maxRetries = 1)
    BonusResponse applyBonus(BonusRequest request) throws Exception;
}