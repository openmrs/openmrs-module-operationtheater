package org.openmrs.module.operationtheater.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.service.SurgicalBlockService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({Context.class, SurgicalBlockResource.class})
@RunWith(PowerMockRunner.class)
public class SurgicalBlockResourceTest {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Mock
    SurgicalBlockService surgicalBlockService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockStatic(Context.class);
        PowerMockito.when(Context.getService(SurgicalBlockService.class)).thenReturn(surgicalBlockService);

    }

    @Test
    public void shouldSaveTheValidSurgicalBlock() throws Exception {
        SurgicalBlock surgicalBlock = new SurgicalBlock();
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:30:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:00:00");
        Location location = new Location(1);
        Provider provider = new Provider(1);
        surgicalBlock.setStartDatetime(startDatetime);
        surgicalBlock.setEndDatetime(endDatetime);
        surgicalBlock.setLocation(location);
        surgicalBlock.setProvider(provider);

        SurgicalBlockResource surgicalBlockResource = new SurgicalBlockResource();
        surgicalBlockResource.save(surgicalBlock);
        verify(surgicalBlockService, times(1)).save(surgicalBlock);
    }
}
