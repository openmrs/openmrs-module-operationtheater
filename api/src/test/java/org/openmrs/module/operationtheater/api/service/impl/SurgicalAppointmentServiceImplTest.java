package org.openmrs.module.operationtheater.api.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.dao.SurgicalAppointmentDao;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class SurgicalAppointmentServiceImplTest {
    @Mock
    SurgicalAppointmentDao surgicalAppointmentDao;

    @InjectMocks
    SurgicalAppointmentServiceImpl surgicalAppointmentService;

    private SimpleDateFormat simpleDateFormat;
    private SurgicalAppointment surgicalAppointment;

    @Before
    public void run() {
        initMocks(this);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        surgicalAppointment = new SurgicalAppointment();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldSaveTheSurgicalAppointmet() throws ParseException {
        surgicalAppointment.setActualStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
        surgicalAppointment.setActualEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));
        surgicalAppointment.setPatient(new Patient());
        surgicalAppointment.setId(1);

        when(surgicalAppointmentDao.save(surgicalAppointment)).thenReturn(surgicalAppointment);

        surgicalAppointmentService.save(surgicalAppointment);
        verify(surgicalAppointmentDao, times(1)).save(surgicalAppointment);
    }

    @Test
    public void shouldNotSaveWhenActualTimeAreOverlapping() throws ParseException {
        surgicalAppointment.setActualStartDatetime(simpleDateFormat.parse("2017-06-06 09:00:00"));
        surgicalAppointment.setActualEndDatetime(simpleDateFormat.parse("2017-06-06 10:00:00"));
        surgicalAppointment.setPatient(new Patient());
        surgicalAppointment.setId(1);

        when(surgicalAppointmentDao.getOverlappingActualTimeEntriesForAppointment(surgicalAppointment)).thenReturn(Arrays.asList(surgicalAppointment));
        exception.expect(ValidationException.class);
        exception.expectMessage("Surgical Appointment has conflicting actual time with existing appointments in this OT");
        surgicalAppointmentService.save(surgicalAppointment);

    }

    @Test
    public void shouldGetSurgicalAppointmentByUuid() throws ParseException {
        String surgicalAppoitmentUuid = "surgicalAppointmentUuid";
        SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
        surgicalAppointment.setUuid(surgicalAppoitmentUuid);

        when(surgicalAppointmentDao.getSurgicalAppointmentByUuid(surgicalAppoitmentUuid)).thenReturn(surgicalAppointment);

        surgicalAppointmentService.getSurgicalAppointmentByUuid(surgicalAppoitmentUuid);
        verify(surgicalAppointmentDao, times(1)).getSurgicalAppointmentByUuid(surgicalAppoitmentUuid);
    }

    @Test
    public void shouldGetSurgicalAppointmentAttributeByUuid() throws ParseException {
        String attributeUuid = "surgicalAppointmentAttributeUuid";
        SurgicalAppointmentAttribute surgicalAppointmentAttribute = new SurgicalAppointmentAttribute();
        surgicalAppointmentAttribute.setUuid(attributeUuid);

        when(surgicalAppointmentDao.getSurgicalAppointmentAttributeByUuid(attributeUuid)).thenReturn(surgicalAppointmentAttribute);

        surgicalAppointmentService.getSurgicalAppointmentAttributeByUuid(attributeUuid);
        verify(surgicalAppointmentDao, times(1)).getSurgicalAppointmentAttributeByUuid(attributeUuid);
    }
}