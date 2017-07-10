package org.openmrs.module.operationtheater.api.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class SurgicalAppointmentAttributeTypeServiceTest extends BaseModuleWebContextSensitiveTest {
    private String superUser;
    private String superUserPassword;
    private String normalUser;
    private String normalUserPassword;
    private String userWithoutPrivilege;
    private String userWithoutPrivilegePassword;

    @Autowired
    SurgicalAppointmentAttributeTypeService surgicalAppointmentAttributeTypeService;

    @Before
    public void setUp() throws Exception {
        superUser = "test-user";
        superUserPassword = "test";
        normalUser = "normal-user";
        normalUserPassword = "normal-password";
        userWithoutPrivilege = "user-without-privilege";
        userWithoutPrivilegePassword = "test";
        executeDataSet("SurgicalBlock.xml");
    }

    @Test
    public void shouldGetSurgicalAppointmentByUUIDIfUserHasManageOTSchedulePrivileges() {
        Context.authenticate(superUser, superUserPassword);
        assertNotNull(surgicalAppointmentAttributeTypeService.getAllAttributeTypes());
    }

    @Test
    public void shouldGetSurgicalAppointmentByUUIDUserHasViewOTSchedulePrivileges() {
        Context.authenticate(normalUser, normalUserPassword);
        assertNotNull(surgicalAppointmentAttributeTypeService.getAllAttributeTypes());
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserHasNoPrivilegesForGetSurgicalAppointmentByUUID() {
        Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
        assertNotNull(surgicalAppointmentAttributeTypeService.getAllAttributeTypes());
    }

    @Test
    public void shouldGetSurgicalAppointmentAttributeTypeByUUIDIfUserHasManageOTSchedulePrivileges() {
        Context.authenticate(superUser, superUserPassword);
        assertNotNull(surgicalAppointmentAttributeTypeService.getSurgicalAppointmentAttributeTypeByUuid("0f1f7d08-076b-4fc6-acac-4bb915151sdd"));
    }

    @Test
    public void shouldGetSurgicalAppointmentAttributeTypeByUUIDUserHasViewOTSchedulePrivileges() {
        Context.authenticate(normalUser, normalUserPassword);
        assertNotNull(surgicalAppointmentAttributeTypeService.getSurgicalAppointmentAttributeTypeByUuid("0f1f7d08-076b-4fc6-acac-4bb915151sdd"));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserHasNoPrivilegesForGetSurgicalAppointmentAttributeByUUID() {
        Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
        assertNotNull(surgicalAppointmentAttributeTypeService.getSurgicalAppointmentAttributeTypeByUuid("0f1f7d08-076b-4fc6-acac-4bb915151sdd"));
    }
}
