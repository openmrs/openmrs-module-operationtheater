package org.openmrs.module.operationtheater.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.openmrs.module.operationtheater.api.service.SurgicalBlockService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({Context.class, SurgicalAppointmentResource.class})
@RunWith(PowerMockRunner.class)
public class SurgicalAppointmentResourceTest {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Mock
    SurgicalAppointmentService surgicalAppointmentService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockStatic(Context.class);
        PowerMockito.when(Context.getService(SurgicalAppointmentService.class)).thenReturn(surgicalAppointmentService);
    }

    @Test
    public void shouldSaveTheValidSurgicalAppointment() throws Exception {
        SurgicalBlock surgicalBlock = new SurgicalBlock();
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 18:00:00");
        Location location = new Location(1);
        Provider provider = new Provider(1);
        surgicalBlock.setStartDatetime(startDatetime);
        surgicalBlock.setEndDatetime(endDatetime);
        surgicalBlock.setLocation(location);
        surgicalBlock.setProvider(provider);

        SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
        surgicalAppointment.setActualStartDatetime(simpleDateFormat.parse("2017-04-24 10:00:00"));
        surgicalAppointment.setActualEndDatetime(simpleDateFormat.parse("2017-04-24 12:00:00"));
        surgicalAppointment.setPatient(new Patient());
        surgicalAppointment.setId(1);

        SurgicalAppointmentResource surgicalAppointmentResource = new SurgicalAppointmentResource();
        surgicalAppointmentService.save(surgicalAppointment);
        verify(surgicalAppointmentService, times(1)).save(surgicalAppointment);
    }
}
