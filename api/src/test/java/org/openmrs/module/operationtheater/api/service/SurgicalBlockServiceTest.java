package org.openmrs.module.operationtheater.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.OrderType;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {
        "classpath:TestingApplicationContext.xml" }, inheritLocations = true)
public class SurgicalBlockServiceTest extends BaseModuleWebContextSensitiveTest {
	
	private String superUser;
	
	private String superUserPassword;
	
	private String normalUser;
	
	private String normalUserPassword;
	
	private String userWithoutPrivilege;
	
	private String userWithoutPrivilegePassword;
	
	@Autowired
	SurgicalBlockService surgicalBlockService;
	
	SimpleDateFormat simpleDateFormat;
	
	@Before
	public void setUp() throws Exception {
		superUser = "test-user";
		superUserPassword = "test";
		normalUser = "normal-user";
		normalUserPassword = "normal-password";
		userWithoutPrivilege = "user-without-privilege";
		userWithoutPrivilegePassword = "test";
		executeDataSet("SurgicalBlock.xml");
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	@Test
	public void shouldPassGetSurgicalAppointmentsIfTheUserHasManageOTSchedulesPrivileges() {
		Context.authenticate(superUser, superUserPassword);
		assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
	}
	
	@Test
	public void shouldPassGetSurgicalAppointmentsIfTheUserHasViewOTSchedulesPrivileges() {
		Context.authenticate(normalUser, normalUserPassword);
		assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveSufficientPrivileges() {
		Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
		assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveManageOTSchedulesPrivileges() {
		Context.authenticate(normalUser, normalUserPassword);
		surgicalBlockService.save(new SurgicalBlock());
	}
	
	@Test
	public void shouldPassGetSurgicalBlocksWithinDateRangeIfTheUserHasManageOTSchedulesPrivileges() {
		Context.authenticate(superUser, superUserPassword);
		assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
	}
	
	@Test
	public void shouldPassGetSurgicalBlocksWithinDateRangeIfTheUserHasViewOTSchedulesPrivileges() {
		Context.authenticate(normalUser, normalUserPassword);
		assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetSurgicalBlocksPrivileges() {
		Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
		assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
	}
	
	@Test
	public void shouldPassGetSurgicalBlocksBetweenStartDatetimeAndEndDatetimeIfTheUserHasManageOTSchedulesPrivileges()
	        throws ParseException {
		Date startDatetime = simpleDateFormat.parse("2017-04-25 09:00:00.0");
		Date endDatetime = simpleDateFormat.parse("2017-04-25 14:45:00");
		Context.authenticate(superUser, superUserPassword);
		assertNotNull(surgicalBlockService.getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(startDatetime, endDatetime,
		    false, false));
	}
	
	@Test
	public void shouldPassGetSurgicalBlocksBetweenStartDatetimeAndEndDatetimeIfTheUserHasViewOTSchedulesPrivileges()
	        throws ParseException {
		Date startDatetime = simpleDateFormat.parse("2017-04-25 09:00:00.0");
		Date endDatetime = simpleDateFormat.parse("2017-04-25 14:45:00");
		Context.authenticate(normalUser, normalUserPassword);
		assertNotNull(surgicalBlockService.getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(startDatetime, endDatetime,
		    false, false));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetSurgicalBlocksBetweenStartDatetimeAndEndDatetimePrivileges()
	        throws ParseException {
		Date startDatetime = simpleDateFormat.parse("2017-04-25 09:00:00.0");
		Date endDatetime = simpleDateFormat.parse("2017-04-25 14:45:00");
		Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
		assertNotNull(surgicalBlockService.getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(startDatetime, endDatetime,
		    false, false));
	}
	
	@Test
	public void shouldCreateSurgeryOrderForNewAppointmentWithoutRequiringClinicalPrivilegesOnOTRole() throws ParseException {
		Context.authenticate("admin", "test");
		setupSurgeryOrderMetadata();
		org.openmrs.Patient patient = Context.getPatientService().getPatient(10);
		
		Context.authenticate(superUser, superUserPassword);
		
		SurgicalBlock block = surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d10921");
		SurgicalAppointment newAppointment = new SurgicalAppointment();
		newAppointment.setPatient(patient);
		newAppointment.setSurgicalBlock(block);
		block.getSurgicalAppointments().add(newAppointment);
		
		SurgicalBlock saved = surgicalBlockService.save(block);
		
		SurgicalAppointment savedAppointment = saved.getSurgicalAppointments().stream()
		        .filter(a -> a.getPatient().getId().equals(10) && !a.getVoided()).findFirst().orElse(null);
		assertNotNull("New appointment should have a linked Surgery Order", savedAppointment.getOrder());
		assertEquals("Surgery Order", savedAppointment.getOrder().getOrderType().getName());
	}
	
	private void setupSurgeryOrderMetadata() {
		EncounterType encounterType = new EncounterType("SURGERY_SCHEDULING",
		        "Encounter created during surgery scheduling to anchor the Surgery Order");
		encounterType.setUuid("a2d9f6b3-1c4e-4a7f-b8d2-e5f3c1a6d9b4");
		encounterType = Context.getEncounterService().saveEncounterType(encounterType);
		
		OrderType orderType = new OrderType("Surgery Order",
		        "Order created automatically when a surgical appointment is scheduled", "org.openmrs.Order");
		orderType.setUuid("c1e3d8a2-4f7b-4a9e-b5c6-d2f8e3a1b4c7");
		orderType = Context.getOrderService().saveOrderType(orderType);
		
		ConceptDatatype naDatatype = Context.getConceptService().getConceptDatatypeByName("N/A");
		ConceptClass miscClass = Context.getConceptService().getConceptClassByName("Misc");
		Concept concept = new Concept();
		concept.setDatatype(naDatatype);
		concept.setConceptClass(miscClass);
		concept.setUuid("c8a89784-e16e-4929-ab5c-be2f3d47f2de");
		concept.addName(new org.openmrs.ConceptName("General Surgical Procedure", java.util.Locale.ENGLISH));
		concept = Context.getConceptService().saveConcept(concept);
		
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty("operationtheater.surgerySchedulingEncounterTypeUuid", encounterType.getUuid()));
		Context.getAdministrationService()
		        .saveGlobalProperty(new GlobalProperty("operationtheater.surgeryOrderTypeUuid", orderType.getUuid()));
		Context.getAdministrationService()
		        .saveGlobalProperty(new GlobalProperty("operationtheater.surgicalOrderConceptUuid", concept.getUuid()));
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty("visits.assignmentHandler", "org.openmrs.api.handler.ExistingOrNewVisitAssignmentHandler"));
	}
	
}
