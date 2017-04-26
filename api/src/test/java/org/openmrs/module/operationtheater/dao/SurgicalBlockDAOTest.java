package org.openmrs.module.operationtheater.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.SurgicalBlock;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class SurgicalBlockDAOTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    SurgicalBlockDAO surgicalBlockDAO;


    private SimpleDateFormat simpleDateFormat;
    private SurgicalBlock surgicalBlock;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("SurgicalBlock.xml");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        surgicalBlock = new SurgicalBlock();
    }

    @Test
    public void shouldGetSurgicalBlocksWhichFallInsideTheDateRangeAtALocation() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-23 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:00:00");
        Location location = Context.getLocationService().getLocation(1);

        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetSurgicalBlocksWhichStartedBeforeTheDateRangeAndEndedInTheDateRangeAtALocation() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:00:00");
        Location location = Context.getLocationService().getLocation(1);

        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetSurgicalBlocksWhichStartedInTheDateRangeAndEndedAfterTheDateRange() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 09:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Location location = Context.getLocationService().getLocation(1);

        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldNotGetSurgicalBlocksWhichStartedAndEndedBeforeTheDateRange() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 09:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 09:59:59");
        Location location = Context.getLocationService().getLocation(1);

        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(0, surgicalBlocks.size());
    }

    @Test
    public void shouldNotGetSurgicalBlocksWhichStartedAndEndedAfterTheDateRange() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:30:01");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:59:59");
        Location location = Context.getLocationService().getLocation(1);

        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertNotNull(surgicalBlocks);
        assertEquals(0, surgicalBlocks.size());

    }

    @Test
    public void shouldSaveSurgicalBlock() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:30:01");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:59:59");
        Location location = Context.getLocationService().getLocation(1);
        Provider provider = Context.getProviderService().getProvider(1);
        surgicalBlock.setStartDatetime(startDatetime);
        surgicalBlock.setEndDatetime(endDatetime);
        surgicalBlock.setLocation(location);
        surgicalBlock.setProvider(provider);

        SurgicalBlock savedSurgicalBlock = surgicalBlockDAO.save(surgicalBlock);

        assertEquals(2, savedSurgicalBlock.getId(), 0.0);
    }

    @Test
    public void shouldGetSurgicalBlocksWhichFallInsideTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-23 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:00:00");
        Provider provider = Context.getProviderService().getProvider(1);

        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetSurgicalBlocksWhichStartedBeforeTheDateRangeAndEndedInTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:00:00");
        Provider provider = Context.getProviderService().getProvider(1);


        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetSurgicalBlocksWhichStartedInTheDateRangeAndEndedAfterTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 09:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Provider provider = Context.getProviderService().getProvider(1);

        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldNotGetSurgicalBlocksWhichStartedAndEndedBeforeTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 09:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 09:59:59");
        Provider provider = Context.getProviderService().getProvider(1);

        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertEquals(0, surgicalBlocks.size());
    }

    @Test
    public void shouldNotGetSurgicalBlocksWhichStartedAndEndedAfterTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:30:01");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:59:59");
        Provider provider = Context.getProviderService().getProvider(1);


        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertNotNull(surgicalBlocks);
        assertEquals(0, surgicalBlocks.size());
    }
}