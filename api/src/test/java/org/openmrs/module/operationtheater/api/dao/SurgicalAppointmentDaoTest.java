package org.openmrs.module.operationtheater.api.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class SurgicalAppointmentDaoTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    SurgicalAppointmentDao surgicalAppointmentDao;

    private SimpleDateFormat simpleDateFormat;
    private SurgicalBlock surgicalBlock;
    private SurgicalAppointment surgicalAppointment;

    @Before
    public void setUp() throws Exception {
        executeDataSet("surgicalAppointments.xml");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        surgicalBlock = new SurgicalBlock();
        surgicalAppointment = new SurgicalAppointment();
    }

    @Test
    public void shouldSaveSurgicalAppointment() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:30:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 13:00:00");
        Location location = Context.getLocationService().getLocation(1);
        Provider provider = Context.getProviderService().getProvider(1);
        Patient patient = Context.getPatientService().getPatient(1);
        surgicalBlock.setId(1);
        surgicalBlock.setStartDatetime(startDatetime);
        surgicalBlock.setEndDatetime(endDatetime);
        surgicalBlock.setLocation(location);
        surgicalBlock.setProvider(provider);

        surgicalAppointment.setSurgicalBlock(surgicalBlock);
        surgicalAppointment.setPatient(patient);
        surgicalAppointment.setStatus("Completed");
        surgicalAppointment.setNotes("need more assistants");

        SurgicalAppointment savedSurgicalAppointment = surgicalAppointmentDao.save(surgicalAppointment);

        assertNotNull(savedSurgicalAppointment);
        assertEquals(patient, savedSurgicalAppointment.getPatient());
        assertEquals(null, savedSurgicalAppointment.getActualStartDatetime());
        assertEquals(null, savedSurgicalAppointment.getActualEndDatetime());
        assertEquals("Completed", savedSurgicalAppointment.getStatus());
        assertEquals("need more assistants", savedSurgicalAppointment.getNotes());
    }

    @Test
    public void shouldReturnTheListOfSurgicalAppointmentsWithOverlappingActualTime() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-06-06 9:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-06-06 10:30:00");
        Location location = Context.getLocationService().getLocation(1);
        Provider provider = Context.getProviderService().getProvider(1);
        Patient patient = Context.getPatientService().getPatient(1);
        surgicalBlock.setId(1);
        surgicalBlock.setStartDatetime(startDatetime);
        surgicalBlock.setEndDatetime(endDatetime);
        surgicalBlock.setLocation(location);
        surgicalBlock.setProvider(provider);

        surgicalAppointment.setSurgicalBlock(surgicalBlock);
        surgicalAppointment.setPatient(patient);
        surgicalAppointment.setStatus("Completed");
        surgicalAppointment.setNotes("need more assistants");
        surgicalAppointment.setActualStartDatetime(startDatetime);
        surgicalAppointment.setActualEndDatetime(endDatetime);
        List<SurgicalAppointment> overlappingSurgicalAppointments = surgicalAppointmentDao.getOverlappingActualTimeEntriesForAppointment(surgicalAppointment);

        assertEquals(1,overlappingSurgicalAppointments.size());
        assertEquals( simpleDateFormat.parse("2017-06-06 10:00:00.0"), overlappingSurgicalAppointments.get(0).getActualStartDatetime());
        assertEquals( simpleDateFormat.parse("2017-06-06 11:00:00.0"), overlappingSurgicalAppointments.get(0).getActualEndDatetime());
    }

    @Test
    public void shouldReturnEmptyListForAppointmentsWithNoActualStartDateAndEndDatetime() {
        SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
        surgicalAppointment.setSurgicalBlock(surgicalBlock);
        List<SurgicalAppointment> overlappingSurgicalAppointments = surgicalAppointmentDao.getOverlappingActualTimeEntriesForAppointment(surgicalAppointment);
        assertEquals(0, overlappingSurgicalAppointments.size());
    }
}
