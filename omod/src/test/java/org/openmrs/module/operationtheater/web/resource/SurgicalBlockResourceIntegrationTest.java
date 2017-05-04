package org.openmrs.module.operationtheater.web.resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.module.webservices.rest.SimpleObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class SurgicalBlockResourceIntegrationTest extends MainResourceControllerTest{

    @Override
    public String getURI() {
        return "surgicalBlock";
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public long getAllCount() {
        return 0;
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void init() throws Exception {
        executeDataSet("SurgicalBlock.xml");
    }


    @Test
    public void shouldSaveTheValidSurgicalBlockWithTheGivenStartTimeEndTimeLocationAndProvider() throws Exception {
        String json = "{\"startDatetime\": \"2017-04-25T10:00:00.000+0530\", \"endDatetime\": \"2017-04-25T12:00:00.000+0530\", \"provider\":{\"id\": \"1\"}, \"location\": {\"id\": \"1\"}}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        SimpleObject surgicalBlock = deserialize(handle(newPostRequest(getURI(), post)));

        assertNotNull(surgicalBlock);
        assertNotNull(surgicalBlock.get("provider"));
        assertNotNull(surgicalBlock.get("location"));
        assertEquals("2017-04-25T10:00:00.000+0530", surgicalBlock.get("startDatetime"));
        assertEquals("2017-04-25T12:00:00.000+0530", surgicalBlock.get("endDatetime"));
    }

    @Test
    public void shouldThrowARuntimeExceptionExceptionWhenThereAreAnyOverlappingSergicalBlocksForTheGivenProvider() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Surgical Block has conflicting time with existing block(s) for this provider");

        String json = "{\"startDatetime\": \"2017-04-24T10:00:00.000+0530\", \"endDatetime\": \"2017-04-24T11:00:00.000+0530\", \"provider\":{\"id\": \"1\"}, \"location\": {\"id\": \"2\"}}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        deserialize(handle(newPostRequest(getURI(), post)));
    }

    @Test
    public void shouldThrowARuntimeExceptionExceptionWhenThereAreAnyOverlappingSergicalBlocksForTheGivenLocation() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Surgical Block has conflicting time with existing block(s) for this OT");

        String json = "{\"startDatetime\": \"2017-04-24T10:00:00.000+0530\", \"endDatetime\": \"2017-04-24T11:00:00.000+0530\", \"provider\":{\"id\": \"2\"}, \"location\": {\"id\": \"1\"}}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        deserialize(handle(newPostRequest(getURI(), post)));
    }

    @Test
    public void shouldThrowARuntimeExceptionExceptionWhenSurgicalBlockStartTimeGreaterThanEndTime() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Surgical Block start date after end date");

        String json = "{\"startDatetime\": \"2017-04-24T12:00:00.000+0530\", \"endDatetime\": \"2017-04-24T10:00:00.000+0530\", \"provider\":{\"id\": \"2\"}, \"location\": {\"id\": \"1\"}}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        deserialize(handle(newPostRequest(getURI(), post)));
    }

}
