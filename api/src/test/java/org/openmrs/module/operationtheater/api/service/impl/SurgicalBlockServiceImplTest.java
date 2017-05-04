package org.openmrs.module.operationtheater.api.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class SurgicalBlockServiceImplTest {
    @Mock
    SurgicalBlockDAO surgicalBlockDAO;

    @InjectMocks
    SurgicalBlockServiceImpl surgicalBlockService;

    private SimpleDateFormat simpleDateFormat;
    private SurgicalBlock surgicalBlock;

    @Before
    public void run() {
        initMocks(this);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        surgicalBlock = new SurgicalBlock();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenSurgicalBlockStartDatetimeIsAfterEndDatetime() throws ParseException {
        surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
        surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 11:45:00"));

        exception.expect(ValidationException.class);
        exception.expectMessage("Surgical Block start date after end date");
        surgicalBlockService.save(surgicalBlock);
    }

    @Test
    public void shouldThrowExceptionWhenTheNewSurgicalBlockOverlapsWithExistingOnesAtALocation() throws ParseException {
        Location location = new Location(1);
        surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-24 13:45:00"));
        surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-24 14:45:00"));
        surgicalBlock.setLocation(location);

        ArrayList<SurgicalBlock> surgicalBlocks = new ArrayList<SurgicalBlock>();
        surgicalBlocks.add(surgicalBlock);

        when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()), eq(surgicalBlock.getEndDatetime()), eq(surgicalBlock.getProvider()), eq(null))).thenReturn(new ArrayList<>());
        when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()), eq(surgicalBlock.getEndDatetime()), eq(null), eq(surgicalBlock.getLocation()))).thenReturn(surgicalBlocks);


        exception.expect(ValidationException.class);
        exception.expectMessage("Surgical Block has conflicting time with existing block(s) for this OT");
        surgicalBlockService.save(surgicalBlock);
    }

    @Test
    public void shouldThrowExceptionWhenTheNewSurgicalBlockOverlapsWithExistingOnesForAProvider() throws ParseException {
        surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
        surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));

        ArrayList<SurgicalBlock> surgicalBlocks = new ArrayList<SurgicalBlock>();
        surgicalBlocks.add(surgicalBlock);

        when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()), eq(surgicalBlock.getEndDatetime()), any(Provider.class), eq(null))).thenReturn(surgicalBlocks);


        exception.expect(ValidationException.class);
        exception.expectMessage("Surgical Block has conflicting time with existing block(s) for this provider");
        surgicalBlockService.save(surgicalBlock);
    }

    @Test
    public void shouldSaveAValidSurgicalBlock() throws ParseException {
        surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
        surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));
        surgicalBlock.setLocation(new Location(1));
        ArrayList<SurgicalBlock> surgicalBlocks = new ArrayList<>();

        when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()), eq(surgicalBlock.getEndDatetime()), eq(null), any(Location.class))).thenReturn(surgicalBlocks);
        when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()), eq(surgicalBlock.getEndDatetime()), any(Provider.class), eq(null))).thenReturn(surgicalBlocks);
        when(surgicalBlockDAO.save(surgicalBlock)).thenReturn(surgicalBlock);

        surgicalBlockService.save(surgicalBlock);

        verify(surgicalBlockDAO, times(1)).save(surgicalBlock);
    }
}