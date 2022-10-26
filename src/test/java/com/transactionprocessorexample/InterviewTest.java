package com.transactionprocessorexample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import javax.inject.Inject;

import com.transactionprocessorexample.internal.WireMockTestResource;
import com.transactionprocessorexample.model.BonusResponse;
import com.transactionprocessorexample.points.PointService;
import com.transactionprocessorexample.thirdparty.BonusService;

import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(WireMockTestResource.class)
public class InterviewTest {
    @Inject
    PointService pointService;
    @Inject
    BonusService bonusService;

    @Test
    @TestTransaction
    public void transactions_inserted_to_db() throws Exception {
        pointService.calculatePoints();
        assertThat(pointService.getPointBalance(6), equalTo(1968L));
    }

    @Test
    @TestTransaction
    public void transactions_inserted_to_db_missing_user() throws Exception {
        pointService.calculatePoints();
        assertThat(pointService.getPointBalance(123), equalTo(0L));
    }

    @Test
    @TestTransaction
    public void dining_multipliers_applied() throws Exception {
        pointService.calculatePoints();
        assertThat(pointService.getPointBalance(1), equalTo(903L));
        assertThat(pointService.getPointBalance(39), equalTo(268L));
    }

    @Test
    public void external_api_resilience_implemented() throws Exception {
        String merchantId = "921496015009488";
        long points = 50L;
        BonusResponse response = bonusService.applyBonus(merchantId, points);
        assertThat(response.result, equalTo("success"));
    }


}
