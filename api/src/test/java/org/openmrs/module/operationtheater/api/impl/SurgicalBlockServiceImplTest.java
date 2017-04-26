package org.openmrs.module.operationtheater.api.impl;

import org.mockito.InjectMocks;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.SurgicalBlock;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class SurgicalBlockServiceImplTest {

    private SimpleDateFormat simpleDateFormat;

    @Mock
    SurgicalBlockDAO surgicalBlockDAO;

    @InjectMocks
    SurgicalBlockServiceImpl surgicalBlockService;

    @Before
    public void run() {
        initMocks(this);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenSurgicalBlockWhenStartDatetimeIsAfterEndDatetime() throws ParseException {
        SurgicalBlock surgicalBlock = new SurgicalBlock();
        surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
        surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 11:45:00"));

        exception.expect(IllegalPropertyException.class);
        exception.expectMessage("Surgical Block start date after end date");
        surgicalBlockService.save(surgicalBlock);
    }

    @Test
    public void shouldThrowExceptionWhenTheNewSurgicalBlockOverlapsWithExistingOnes() throws ParseException {
        SurgicalBlock surgicalBlock = new SurgicalBlock();
        surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
        surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));

        ArrayList<SurgicalBlock> surgicalBlocks = new ArrayList<SurgicalBlock>();
        surgicalBlocks.add(surgicalBlock);

        when(surgicalBlockDAO.getOverlappingSurgicalBlocks(surgicalBlock)).thenReturn(surgicalBlocks);


        exception.expect(IllegalPropertyException.class);
        exception.expectMessage("Surgical Block has conflicting time with existing block(s)");
        surgicalBlockService.save(surgicalBlock);
    }

    @Test
    public void shouldSaveAValidSurgicalBlock() throws ParseException {
        SurgicalBlock surgicalBlock = new SurgicalBlock();
        surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
        surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));
        ArrayList<SurgicalBlock> surgicalBlocks = new ArrayList<>();
        when(surgicalBlockDAO.getOverlappingSurgicalBlocks(surgicalBlock)).thenReturn(surgicalBlocks);
        when(surgicalBlockDAO.save(surgicalBlock)).thenReturn(surgicalBlock);

        surgicalBlockService.save(surgicalBlock);

        verify(surgicalBlockDAO, times(1)).save(surgicalBlock);
    }
}