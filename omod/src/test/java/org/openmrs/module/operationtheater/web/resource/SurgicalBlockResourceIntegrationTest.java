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
import static org.junit.Assert.assertTrue;

public class SurgicalBlockResourceIntegrationTest extends MainResourceControllerTest {
	
	@Override
	public String getURI() {
		return "surgicalBlock";
	}
	
	@Override
	public String getUuid() {
		return "5580cddd-c290-66c8-8d3a-96dc33d109f1";
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
		String json = "{\"startDatetime\": \"2017-04-25T10:00:00.000\", \"endDatetime\": \"2017-04-25T12:00:00.000\", \"provider\":{\"id\": \"1\"}, \"location\": {\"id\": \"1\"}}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		SimpleObject surgicalBlock = deserialize(handle(newPostRequest(getURI(), post)));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		assertNotNull(surgicalBlock);
		assertNotNull(surgicalBlock.get("provider"));
		assertNotNull(surgicalBlock.get("location"));
		assertEquals(simpleDateFormat.parse("2017-04-25T10:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("startDatetime")));
		assertEquals(simpleDateFormat.parse("2017-04-25T12:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("endDatetime")));
	}
	
	@Test
	public void shouldThrowARuntimeExceptionExceptionWhenThereAreAnyOverlappingSurgicalBlocksForTheGivenProvider()
	        throws Exception {
		exception.expect(RuntimeException.class);
		exception.expectMessage("Surgical Block has conflicting time with existing block(s) for this surgeon");
		
		String json = "{\"startDatetime\": \"2017-04-24T10:00:00.000\", \"endDatetime\": \"2017-04-24T11:00:00.000\", \"provider\":{\"id\": \"1\"}, \"location\": {\"id\": \"2\"}}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		deserialize(handle(newPostRequest(getURI(), post)));
	}
	
	@Test
	public void shouldThrowARuntimeExceptionExceptionWhenThereAreAnyOverlappingSergicalBlocksForTheGivenLocation()
	        throws Exception {
		exception.expect(RuntimeException.class);
		exception.expectMessage("Surgical Block has conflicting time with existing block(s) for this OT");
		
		String json = "{\"startDatetime\": \"2017-04-24T10:00:00.000\", \"endDatetime\": \"2017-04-24T11:00:00.000\", \"provider\":{\"id\": \"2\"}, \"location\": {\"id\": \"1\"}}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		deserialize(handle(newPostRequest(getURI(), post)));
	}
	
	@Test
	public void shouldThrowARuntimeExceptionExceptionWhenSurgicalBlockStartTimeGreaterThanEndTime() throws Exception {
		exception.expect(RuntimeException.class);
		exception.expectMessage("Surgical Block start date after end date");
		
		String json = "{\"startDatetime\": \"2017-04-24T12:00:00.000\", \"endDatetime\": \"2017-04-24T10:00:00.000\", \"provider\":{\"id\": \"2\"}, \"location\": {\"id\": \"1\"}}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		deserialize(handle(newPostRequest(getURI(), post)));
	}
	
	@Test
	public void shouldSaveTheValidSurgicalBlockWithSurgicalAppointments() throws Exception {
		String json = "{\"startDatetime\": \"2017-04-25T10:00:00.000\", \"endDatetime\": \"2017-04-25T12:00:00.000\", \"provider\":{\"id\": \"1\"}, \"location\": {\"id\": \"1\"},"
		        + " \"surgicalAppointments\":[{\"patient\": {\"uuid\":\"5631b434-78aa-102b-91a0-001e378eb17e\"}, \"actualStartDatetime\": \"2017-04-25T10:00:00.000\", \"actualEndDatetime\": \"2017-04-25T11:00:00.000\","
		        + " \"status\": \"Scheduled\", \"notes\": \"need more assistants\"}]}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		SimpleObject surgicalBlock = deserialize(handle(newPostRequest(getURI(), post)));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		assertNotNull(surgicalBlock);
		assertNotNull(surgicalBlock.get("id"));
		assertNotNull(surgicalBlock.get("provider"));
		assertNotNull(surgicalBlock.get("location"));
		assertEquals(simpleDateFormat.parse("2017-04-25T10:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("startDatetime")));
		assertEquals(simpleDateFormat.parse("2017-04-25T12:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("endDatetime")));
		
		java.util.List surgicalAppointments = surgicalBlock.get("surgicalAppointments");
		LinkedHashMap<String, Object> surgicalAppointment = (LinkedHashMap<String, Object>) surgicalAppointments.get(0);
		assertNotNull(surgicalAppointment.get("id"));
		assertEquals(simpleDateFormat.parse("2017-04-25T10:00:00.000"),
		    simpleDateFormat.parse((surgicalAppointment.get("actualStartDatetime")).toString()));
		assertEquals(simpleDateFormat.parse("2017-04-25T11:00:00.000"),
		    simpleDateFormat.parse((surgicalAppointment.get("actualEndDatetime")).toString()));
		assertEquals("Scheduled", surgicalAppointment.get("status"));
		assertEquals("need more assistants", surgicalAppointment.get("notes"));
		LinkedHashMap<String, Object> patient = (LinkedHashMap<String, Object>) surgicalAppointment.get("patient");
		assertEquals("5631b434-78aa-102b-91a0-001e378eb17e", patient.get("uuid"));
	}
	
	@Test
	public void shouldSaveTheValidSurgicalBlockWithSurgicalAppointmentsAndAttributes() throws Exception {
		String json = "{\"startDatetime\": \"2017-04-25T10:00:00.000\", \"endDatetime\": \"2017-04-25T12:00:00.000\", \"provider\":{\"id\": \"1\"}, \"location\": {\"id\": \"1\"},"
		        + " \"surgicalAppointments\":[{\"patient\": {\"uuid\":\"5631b434-78aa-102b-91a0-001e378eb17e\"}, \"actualStartDatetime\": \"2017-04-25T10:00:00.000\", \"actualEndDatetime\": \"2017-04-25T11:00:00.000\","
		        + " \"status\": \"Scheduled\", \"notes\": \"need more assistants\""
		        + ", \"surgicalAppointmentAttributes\": [{\"value\": \"Surgery on left leg\", \"surgicalAppointmentAttributeType\": {\"id\": 1}}]"
		        + "}]}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		SimpleObject surgicalBlock = deserialize(handle(newPostRequest(getURI(), post)));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		assertNotNull(surgicalBlock);
		assertNotNull(surgicalBlock.get("id"));
		assertNotNull(surgicalBlock.get("provider"));
		assertNotNull(surgicalBlock.get("location"));
		assertEquals(simpleDateFormat.parse("2017-04-25T10:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("startDatetime")));
		assertEquals(simpleDateFormat.parse("2017-04-25T12:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("endDatetime")));
		
		java.util.List surgicalAppointments = surgicalBlock.get("surgicalAppointments");
		LinkedHashMap<String, Object> surgicalAppointment = (LinkedHashMap<String, Object>) surgicalAppointments.get(0);
		assertNotNull(surgicalAppointment.get("id"));
		assertEquals(simpleDateFormat.parse("2017-04-25T10:00:00.000"),
		    simpleDateFormat.parse((surgicalAppointment.get("actualStartDatetime")).toString()));
		assertEquals(simpleDateFormat.parse("2017-04-25T11:00:00.000"),
		    simpleDateFormat.parse((surgicalAppointment.get("actualEndDatetime")).toString()));
		assertEquals("Scheduled", surgicalAppointment.get("status"));
		assertEquals("need more assistants", surgicalAppointment.get("notes"));
		LinkedHashMap<String, Object> patient = (LinkedHashMap<String, Object>) surgicalAppointment.get("patient");
		assertEquals("5631b434-78aa-102b-91a0-001e378eb17e", patient.get("uuid"));
		
		java.util.List surgicalAppointmentAttributes = (java.util.List) surgicalAppointment
		        .get("surgicalAppointmentAttributes");
		LinkedHashMap<String, Object> surgicalAppointmentAttribute = (LinkedHashMap<String, Object>) surgicalAppointmentAttributes
		        .get(0);
		assertNotNull(surgicalAppointmentAttribute.get("id"));
		assertEquals("Surgery on left leg", surgicalAppointmentAttribute.get("value"));
	}
	
	@Test
	public void shouldUpdateTheValidSurgicalBlockWithSurgicalAppointmentsAndAttributes() throws Exception {
		String json = "{\"id\": 1, \"startDatetime\": \"2017-04-25T10:00:00.000\", \"endDatetime\": \"2017-04-25T12:00:00.000\", \"provider\":{\"id\": \"1\"}, \"location\": {\"id\": \"1\"},"
		        + " \"surgicalAppointments\":[{\"id\": 1, \"patient\": {\"uuid\":\"5631b434-78aa-102b-91a0-001e378eb17e\"}, \"actualStartDatetime\": \"2017-04-25T10:00:00.000\", \"actualEndDatetime\": \"2017-04-25T11:00:00.000\","
		        + " \"status\": \"Scheduled\", \"notes\": \"need more assistants\""
		        + ", \"surgicalAppointmentAttributes\": [{\"id\": 1, \"value\": \"Surgery on left leg\", \"surgicalAppointmentAttributeType\": {\"id\": 1}}]"
		        + "}]}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		SimpleObject surgicalBlock = deserialize(handle(newPostRequest(getURI(), post)));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		assertNotNull(surgicalBlock);
		assertNotNull(surgicalBlock.get("id"));
		assertNotNull(surgicalBlock.get("provider"));
		assertNotNull(surgicalBlock.get("location"));
		assertEquals(simpleDateFormat.parse("2017-04-25T10:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("startDatetime")));
		assertEquals(simpleDateFormat.parse("2017-04-25T12:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("endDatetime")));
		
		java.util.List surgicalAppointments = surgicalBlock.get("surgicalAppointments");
		LinkedHashMap<String, Object> surgicalAppointment = (LinkedHashMap<String, Object>) surgicalAppointments.get(0);
		assertNotNull(surgicalAppointment.get("id"));
		assertEquals(1, surgicalAppointment.get("id"));
		assertEquals(simpleDateFormat.parse("2017-04-25T10:00:00.000"),
		    simpleDateFormat.parse((surgicalAppointment.get("actualStartDatetime")).toString()));
		assertEquals(simpleDateFormat.parse("2017-04-25T11:00:00.000"),
		    simpleDateFormat.parse((surgicalAppointment.get("actualEndDatetime")).toString()));
		assertEquals("Scheduled", surgicalAppointment.get("status"));
		assertEquals("need more assistants", surgicalAppointment.get("notes"));
		LinkedHashMap<String, Object> patient = (LinkedHashMap<String, Object>) surgicalAppointment.get("patient");
		assertEquals("5631b434-78aa-102b-91a0-001e378eb17e", patient.get("uuid"));
		
		java.util.List surgicalAppointmentAttributes = (java.util.List) surgicalAppointment
		        .get("surgicalAppointmentAttributes");
		LinkedHashMap<String, Object> surgicalAppointmentAttribute = (LinkedHashMap<String, Object>) surgicalAppointmentAttributes
		        .get(0);
		assertNotNull(surgicalAppointmentAttribute.get("id"));
		assertEquals(1, surgicalAppointmentAttribute.get("id"));
		assertEquals("Surgery on left leg", surgicalAppointmentAttribute.get("value"));
	}
	
	@Test
	public void shouldThowAValidationExceptionIfThereAreAnyConflictingSurgicalAppointmentsForGivenPatientFromNewSurgicalAppointments()
	        throws Exception {
		exception.expect(RuntimeException.class);
		exception.expectMessage("Patient One has conflicting appointment at OT1 with Doctor Strange");
		
		String json = "{\"startDatetime\": \"2017-04-24T10:00:00.000\", \"endDatetime\": \"2017-04-24T12:00:00.000\", \"provider\":{\"id\": \"2\"}, \"location\": {\"id\": \"2\"},"
		        + " \"surgicalAppointments\":[{\"patient\": {\"uuid\":\"5631b434-78aa-102b-91a0-001e378eb17e\"}, \"status\": \"Scheduled\", \"notes\": \"need more assistants\""
		        + ", \"surgicalAppointmentAttributes\": [{\"value\": \"Surgery on left leg\", \"surgicalAppointmentAttributeType\": {\"id\": 1}}]"
		        + "}]}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		deserialize(handle(newPostRequest(getURI(), post)));
		
	}
	
	@Test
	public void shouldGetTheSurgicalBlockWithGivenUuid() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		request.setParameter("v", "full");
		SimpleObject surgicalBlock = deserialize(handle(request));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		assertNotNull(surgicalBlock);
		assertNotNull(surgicalBlock.get("id"));
		assertEquals("1", surgicalBlock.get("id").toString());
		LinkedHashMap<String, Object> provider = (LinkedHashMap<String, Object>) surgicalBlock.get("provider");
		assertNotNull(provider);
		assertEquals("04d96c50-772c-42d5-88ba-5a582957afa8", provider.get("uuid"));
		LinkedHashMap<String, Object> location = (LinkedHashMap<String, Object>) surgicalBlock.get("location");
		assertNotNull(location);
		assertEquals("OT1", location.get("name"));
		assertEquals(simpleDateFormat.parse("2017-04-24T10:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("startDatetime")));
		assertEquals(simpleDateFormat.parse("2017-04-24T11:30:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("endDatetime")));
		
		java.util.List surgicalAppointments = surgicalBlock.get("surgicalAppointments");
		LinkedHashMap<String, Object> surgicalAppointment = (LinkedHashMap<String, Object>) surgicalAppointments.get(0);
		assertNotNull(surgicalAppointment.get("id"));
		assertEquals(1, surgicalAppointment.get("id"));
		assertEquals("Scheduled", surgicalAppointment.get("status"));
		assertEquals("need more assistants", surgicalAppointment.get("notes"));
		LinkedHashMap<String, Object> patient = (LinkedHashMap<String, Object>) surgicalAppointment.get("patient");
		assertEquals("5631b434-78aa-102b-91a0-001e378eb17e", patient.get("uuid"));
		
		java.util.List surgicalAppointmentAttributes = (java.util.List) surgicalAppointment
		        .get("surgicalAppointmentAttributes");
		LinkedHashMap<String, Object> surgicalAppointmentAttribute = (LinkedHashMap<String, Object>) surgicalAppointmentAttributes
		        .get(0);
		assertNotNull(surgicalAppointmentAttribute.get("id"));
		assertEquals(1, surgicalAppointmentAttribute.get("id"));
		assertEquals("Surgery on left hand", surgicalAppointmentAttribute.get("value"));
		
	}
	
	@Test
	public void shouldSetTheVoidReasonAndDateVoidedOfTheSurgicalBlock() throws Exception {
		String json = "{\"startDatetime\": \"2017-04-25T10:00:00.000\", \"endDatetime\": \"2017-04-25T12:00:00.000\", \"provider\":{\"id\": \"1\"}, \"location\": {\"id\": \"1\"}, \"voided\": \"true\", \"voidReason\": \"Public holiday\","
		        + " \"surgicalAppointments\":[{\"patient\": {\"uuid\":\"5631b434-78aa-102b-91a0-001e378eb17e\"}, \"actualStartDatetime\": \"2017-04-25T10:00:00.000\", \"actualEndDatetime\": \"2017-04-25T11:00:00.000\","
		        + " \"status\": \"Scheduled\", \"notes\": \"need more assistants\"}]}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		SimpleObject surgicalBlock = deserialize(handle(newPostRequest(getURI(), post)));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		assertNotNull(surgicalBlock);
		assertNotNull(surgicalBlock.get("id"));
		assertNotNull(surgicalBlock.get("provider"));
		assertNotNull(surgicalBlock.get("location"));
		assertTrue(surgicalBlock.get("voided"));
		assertEquals("Public holiday", surgicalBlock.get("voidReason"));
		assertEquals(simpleDateFormat.parse("2017-04-25T10:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("startDatetime")));
		assertEquals(simpleDateFormat.parse("2017-04-25T12:00:00.000"),
		    simpleDateFormat.parse(surgicalBlock.get("endDatetime")));
	}
}
