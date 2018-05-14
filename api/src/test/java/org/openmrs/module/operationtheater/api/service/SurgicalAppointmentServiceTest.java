package org.openmrs.module.operationtheater.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {
        "classpath:TestingApplicationContext.xml" }, inheritLocations = true)
public class SurgicalAppointmentServiceTest extends BaseModuleWebContextSensitiveTest {
	
	private String superUser;
	
	private String superUserPassword;
	
	private String normalUser;
	
	private String normalUserPassword;
	
	private String userWithoutPrivilege;
	
	private String userWithoutPrivilegePassword;
	
	@Autowired
	SurgicalAppointmentService surgicalAppointmentService;
	
	@Autowired
	SurgicalBlockService surgicalBlockService;
	
	SurgicalAppointment surgicalAppointment;
	
	SurgicalBlock surgicalBlock;
	
	@Before
	public void setUp() throws Exception {
		superUser = "test-user";
		superUserPassword = "test";
		normalUser = "normal-user";
		normalUserPassword = "normal-password";
		userWithoutPrivilege = "user-without-privilege";
		userWithoutPrivilegePassword = "test";
		executeDataSet("SurgicalBlock.xml");
		surgicalBlock = surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f5");
		surgicalAppointment = new SurgicalAppointment();
		surgicalAppointment.setPatient(Context.getPatientService().getPatient(10));
		surgicalAppointment.setSurgicalBlock(surgicalBlock);
	}
	
	@Test
	public void shouldSaveSurgicalAppointmentIfUserHasManageOTSchedulePrivileges() {
		Context.authenticate(superUser, superUserPassword);
		assertNotNull(surgicalAppointmentService.save(surgicalAppointment));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserHasViewOTSchedulePrivileges() {
		Context.authenticate(normalUser, normalUserPassword);
		assertNotNull(surgicalAppointmentService.save(surgicalAppointment));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserHasNoPrivileges() {
		Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
		assertNotNull(surgicalAppointmentService.save(surgicalAppointment));
	}
	
	@Test
	public void shouldGetSurgicalAppointmentByUUIDIfUserHasManageOTSchedulePrivileges() {
		Context.authenticate(superUser, superUserPassword);
		assertNotNull(surgicalAppointmentService.getSurgicalAppointmentByUuid("5580cddd-1111-66c8-8d3a-96dc33d109f1"));
	}
	
	@Test
	public void shouldGetSurgicalAppointmentByUUIDUserHasViewOTSchedulePrivileges() {
		Context.authenticate(normalUser, normalUserPassword);
		assertNotNull(surgicalAppointmentService.getSurgicalAppointmentByUuid("5580cddd-1111-66c8-8d3a-96dc33d109f1"));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserHasNoPrivilegesForGetSurgicalAppointmentByUUID() {
		Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
		assertNotNull(surgicalAppointmentService.getSurgicalAppointmentByUuid("5580cddd-1111-66c8-8d3a-96dc33d109f1"));
	}
	
	@Test
	public void shouldGetSurgicalAppointmentAttributeByUuidIfUserHasManageOTSchedulePrivileges() {
		Context.authenticate(superUser, superUserPassword);
		assertNotNull(
		    surgicalAppointmentService.getSurgicalAppointmentAttributeByUuid("5580cddd-1111-66c8-8d3a-96dc33d10000"));
	}
	
	@Test
	public void shouldGetSurgicalAppointmentAttributeByUuidUserHasViewOTSchedulePrivileges() {
		Context.authenticate(normalUser, normalUserPassword);
		assertNotNull(
		    surgicalAppointmentService.getSurgicalAppointmentAttributeByUuid("5580cddd-1111-66c8-8d3a-96dc33d10000"));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserHasNoPrivilegesForGetSurgicalAppointmentAttributeByUuid() {
		Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
		assertNotNull(
		    surgicalAppointmentService.getSurgicalAppointmentAttributeByUuid("5580cddd-1111-66c8-8d3a-96dc33d10000"));
	}
}
