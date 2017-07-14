package org.openmrs.module.operationtheater.web.resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class SurgicalAppointmentResourceIntegrationTest extends MainResourceControllerTest{

    @Override
    public String getURI() {
        return "surgicalAppointment";
    }

    @Override
    public String getUuid() {
        return "5580cddd-1111-66c8-8d3a-96dc33d109f1";
    }

    @Override
    public long getAllCount() {
        return 0;
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void init() throws Exception {
        executeDataSet("SurgicalAppointments.xml");
    }

    @Test
    public void shouldSaveTheValidSurgicalAppointment() throws Exception {
        String json = "{\"patient\": {\"id\": 1}, \"surgicalBlock\": { \"id\": 1, \"uuid\": \"5580cddd-c290-66c8-8d3a-96dc33d109f1\"}," +
                " \"status\": \"Scheduled\", \"sortWeight\": 0, \"notes\": \"need more assistants\"}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        SimpleObject surgicalAppointment = deserialize(handle(newPostRequest(getURI(), post)));

        assertNotNull(surgicalAppointment);
        assertNotNull(surgicalAppointment.get("id"));
        assertEquals("Scheduled", surgicalAppointment.get("status"));
        assertEquals("need more assistants", surgicalAppointment.get("notes"));
    }

    @Test
    public void shouldSaveTheValidSurgicalAppointmentWithAttributes() throws Exception {
        String json = "{\"patient\": {\"id\": 1}, \"surgicalBlock\": { \"id\": 1, \"uuid\": \"5580cddd-c290-66c8-8d3a-96dc33d109f1\" }," +
                " \"status\": \"Scheduled\", \"sortWeight\": 0, \"notes\": \"need more assistants\"" +
                ", \"surgicalAppointmentAttributes\": [{\"value\": \"Surgery on left leg\", \"surgicalAppointmentAttributeType\": {\"id\": 1}}] }";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        SimpleObject surgicalAppointment = deserialize(handle(newPostRequest(getURI(), post)));

        assertNotNull(surgicalAppointment);
        assertNotNull(surgicalAppointment.get("id"));
        assertEquals("Scheduled", surgicalAppointment.get("status"));
        assertEquals("need more assistants", surgicalAppointment.get("notes"));

        java.util.List surgicalAppointmentAttributes = surgicalAppointment.get("surgicalAppointmentAttributes");
        LinkedHashMap<String, Object> surgicalAppointmentAttribute = (LinkedHashMap<String, Object>) surgicalAppointmentAttributes.get(0);
        assertNotNull(surgicalAppointmentAttribute.get("id"));
        assertEquals("Surgery on left leg", surgicalAppointmentAttribute.get("value"));
    }

    @Test
    public void shouldUpdateTheSurgicalAppointment() throws Exception {
        String json = "{\"id\": \"1\", \"uuid\": \"5580cddd-1111-66c8-8d3a-96dc33d109f1\", \"patient\": {\"id\": 1}," +
                " \"surgicalBlock\": { \"id\": 1, \"uuid\": \"5580cddd-c290-66c8-8d3a-96dc33d109f1\"}, " +
                "\"actualStartDatetime\": \"2017-05-11T10:20:00.000\", \"actualEndDatetime\": \"2017-05-11T11:30:00.000\"," +
                " \"status\": \"Completed\", \"sortWeight\": 0, \"notes\": \"need more assistants\"}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        SimpleObject surgicalAppointment = deserialize(handle(newPostRequest(getURI() + "/" + getUuid(), post)));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        assertNotNull(surgicalAppointment);
        assertNotNull(surgicalAppointment.get("id"));
        assertEquals("1", surgicalAppointment.get("id").toString());
        assertEquals("5580cddd-1111-66c8-8d3a-96dc33d109f1", surgicalAppointment.get("uuid"));

        assertEquals(simpleDateFormat.parse("2017-05-11T10:20:00.000"), simpleDateFormat.parse(surgicalAppointment.get("actualStartDatetime")));
        assertEquals(simpleDateFormat.parse("2017-05-11T11:30:00.000"), simpleDateFormat.parse(surgicalAppointment.get("actualEndDatetime")));
        assertEquals("Completed", surgicalAppointment.get("status"));
        assertEquals("need more assistants", surgicalAppointment.get("notes"));
    }

    @Test
    public void shouldAddNewAttributesToExistingSurgicalAppointment() throws Exception {
        String json = "{\"id\": \"1\", \"uuid\": \"5580cddd-1111-66c8-8d3a-96dc33d109f1\", \"patient\": {\"id\": 1}," +
                " \"surgicalBlock\": { \"id\": 1, \"uuid\": \"5580cddd-c290-66c8-8d3a-96dc33d109f1\"}, " +
                "\"actualStartDatetime\": \"2017-05-11T10:20:00.000\", \"actualEndDatetime\": \"2017-05-11T11:30:00.000\"," +
                " \"status\": \"Completed\", \"notes\": \"need more assistants\"" +
                ", \"surgicalAppointmentAttributes\": [{\"value\": \"Surgery on left leg\", \"surgicalAppointmentAttributeType\": {\"id\": 1}}]" +
                "}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        SimpleObject surgicalAppointment = deserialize(handle(newPostRequest(getURI() + "/" + getUuid(), post)));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        assertNotNull(surgicalAppointment);
        assertNotNull(surgicalAppointment.get("id"));
        assertEquals("1", surgicalAppointment.get("id").toString());
        assertEquals("5580cddd-1111-66c8-8d3a-96dc33d109f1", surgicalAppointment.get("uuid"));
        assertEquals(simpleDateFormat.parse("2017-05-11T10:20:00.000"), simpleDateFormat.parse(surgicalAppointment.get("actualStartDatetime")));
        assertEquals(simpleDateFormat.parse("2017-05-11T11:30:00.000"), simpleDateFormat.parse(surgicalAppointment.get("actualEndDatetime")));
        assertEquals("Completed", surgicalAppointment.get("status"));
        assertEquals("need more assistants", surgicalAppointment.get("notes"));

        java.util.List surgicalAppointmentAttributes = surgicalAppointment.get("surgicalAppointmentAttributes");
        LinkedHashMap<String, Object> surgicalAppointmentAttribute = (LinkedHashMap<String, Object>) surgicalAppointmentAttributes.get(0);
        assertNotNull(surgicalAppointmentAttribute.get("id"));
        assertEquals(2, surgicalAppointmentAttribute.get("id"));
        assertEquals("Surgery on left leg", surgicalAppointmentAttribute.get("value"));
    }

    @Test
    public void shouldUpdateTheSurgicalAppointmentWithTheAttributes() throws Exception {
        String json = "{\"id\": \"1\", \"uuid\": \"5580cddd-1111-66c8-8d3a-96dc33d109f1\", \"patient\": {\"id\": 1}, " +
                "\"surgicalBlock\": { \"id\": 1, \"uuid\": \"5580cddd-c290-66c8-8d3a-96dc33d109f1\"}, " +
                "\"actualStartDatetime\": \"2017-05-11T10:20:00.000\", \"actualEndDatetime\": \"2017-05-11T11:30:00.000\"," +
                " \"status\": \"Scheduled\", \"notes\": \"need more assistants\"" +
                ", \"surgicalAppointmentAttributes\": [{\"id\": 1, \"uuid\": \"5580cddd-1111-66c8-8d3a-96dc33d10000\", \"value\": \"Surgery on left leg\", \"surgicalAppointmentAttributeType\": {\"id\": 1}}]" +
                "}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        SimpleObject surgicalAppointment = deserialize(handle(newPostRequest(getURI() + "/" + getUuid(), post)));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        assertNotNull(surgicalAppointment);
        assertNotNull(surgicalAppointment.get("id"));
        assertEquals("1", surgicalAppointment.get("id").toString());
        assertEquals("5580cddd-1111-66c8-8d3a-96dc33d109f1", surgicalAppointment.get("uuid"));
        assertEquals(simpleDateFormat.parse("2017-05-11T10:20:00.000"), simpleDateFormat.parse(surgicalAppointment.get("actualStartDatetime")));
        assertEquals(simpleDateFormat.parse("2017-05-11T11:30:00.000"), simpleDateFormat.parse(surgicalAppointment.get("actualEndDatetime")));
        assertEquals("Scheduled", surgicalAppointment.get("status"));
        assertEquals("need more assistants", surgicalAppointment.get("notes"));

        java.util.List surgicalAppointmentAttributes = surgicalAppointment.get("surgicalAppointmentAttributes");
        LinkedHashMap<String, Object> surgicalAppointmentAttribute = (LinkedHashMap<String, Object>) surgicalAppointmentAttributes.get(0);
        assertNotNull(surgicalAppointmentAttribute.get("id"));
        assertEquals(1, surgicalAppointmentAttribute.get("id"));
        assertEquals("Surgery on left leg", surgicalAppointmentAttribute.get("value"));
    }
}
