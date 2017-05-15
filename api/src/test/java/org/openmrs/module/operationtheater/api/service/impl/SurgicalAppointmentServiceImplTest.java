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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
}