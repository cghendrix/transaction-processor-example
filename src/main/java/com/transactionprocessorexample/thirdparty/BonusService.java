package com.transactionprocessorexample.thirdparty;

import com.transactionprocessorexample.model.BonusRequest;
import com.transactionprocessorexample.model.BonusResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BonusService {

    @Inject
    Logger logger;

    @RestClient
    BonusClient bonusClient;

    // number of milliseconds to wait before timing out
    final int timeoutInMilliseconds = 5000;
    // max number of attempts to call flaky bonus API
    final int numberOfRetries = 1;

    /**
     * Calls the flaky bonus api with a set number of failure retries and returns the response
     *
     * @param   merchantId Id of merchant to apply the bonus for
     * @param   points number of points to apply as bonus
     * @return   response from bonus API if success otherwise failure with message
     * @throws   Exception when the bonus api call fails after set number of retries or timeout
     * @see BonusResponse
     */
    public BonusResponse applyBonus(String merchantId, long points) throws Exception {
        BonusRequest request = new BonusRequest();
        request.merchantId = merchantId;
        request.points = points;
        request.requestId = UUID.randomUUID().toString();
        return bonusClient.applyBonus(request);
    }
}
