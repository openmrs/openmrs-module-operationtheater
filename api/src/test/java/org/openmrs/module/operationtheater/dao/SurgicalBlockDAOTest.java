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
import java.util.List;
import java.util.Date;
import java.util.List;

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
    public void shouldGetOverlappingSurgicalBlocksWhichFallInsideTheGivenDateRangeForAGivenLocation() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-23 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:00:00");
        Location location = Context.getLocationService().getLocation(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetOverlappingSurgicalBlocksWhichStartedBeforeTheDateRangeAndEndedInTheDateRangeForAGivenLocation() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:00:00");
        Location location = Context.getLocationService().getLocation(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetOverlappingSurgicalBlocksWhichStartedInTheDateRangeAndEndedAfterTheDateRangeForAGivenLocation() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 09:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 10:30:00");
        Location location = Context.getLocationService().getLocation(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldNotGetOverlappingSurgicalBlocksWhichStartedAndEndedBeforeTheDateRangeForAGivenLocation() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 09:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Location location = Context.getLocationService().getLocation(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(0, surgicalBlocks.size());
    }

    @Test
    public void shouldNotGetOverlappingSurgicalBlocksWhichStartedAndEndedAfterTheDateRangeForAGivenLocation() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:30:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 13:00:00");
        Location location = Context.getLocationService().getLocation(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertNotNull(surgicalBlocks);
        assertEquals(0, surgicalBlocks.size());

    }

    @Test
    public void shouldGetOverlappingSurgicalBlockWhichStartedOutsideTheDateRangeAndEndedAfterTheDateRangeForAGivenLocation() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:30:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 11:00:00");
        Location location = Context.getLocationService().getLocation(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetAllOverlappingSurgicalBlocksForGivenDateRangeForAGivenLocation() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:30:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 16:00:00");
        Location location = Context.getLocationService().getLocation(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(2, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
        assertEquals(3, surgicalBlocks.get(1).getId(), 0.0);
    }

    @Test
    public void shouldSaveSurgicalBlock() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:30:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 13:00:00");
        Location location = Context.getLocationService().getLocation(1);
        Provider provider = Context.getProviderService().getProvider(1);
        surgicalBlock.setStartDatetime(startDatetime);
        surgicalBlock.setEndDatetime(endDatetime);
        surgicalBlock.setLocation(location);
        surgicalBlock.setProvider(provider);

        SurgicalBlock savedSurgicalBlock = surgicalBlockDAO.save(surgicalBlock);

        assertNotNull(savedSurgicalBlock);
        assertEquals(provider, savedSurgicalBlock.getProvider());
        assertEquals(location, savedSurgicalBlock.getLocation());
        assertEquals(startDatetime, savedSurgicalBlock.getStartDatetime());
        assertEquals(endDatetime, savedSurgicalBlock.getEndDatetime());
    }

    @Test
    public void shouldGetOverlappingSurgicalBlocksWhichFallInsideTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-23 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:00:00");
        Provider provider = Context.getProviderService().getProvider(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetOverlappingSurgicalBlocksWhichStartedBeforeTheDateRangeAndEndedInTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:00:00");
        Provider provider = Context.getProviderService().getProvider(1);


        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetOverlappingSurgicalBlocksWhichStartedInTheDateRangeAndEndedAfterTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 09:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 10:30:00");
        Provider provider = Context.getProviderService().getProvider(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldNotGetSurgicalBlocksWhichStartedAndEndedBeforeTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 09:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Provider provider = Context.getProviderService().getProvider(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertEquals(0, surgicalBlocks.size());
    }

    @Test
    public void shouldNotGetSurgicalBlocksWhichStartedAndEndedAfterTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 11:30:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 13:00:00");
        Provider provider = Context.getProviderService().getProvider(1);


        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertNotNull(surgicalBlocks);
        assertEquals(0, surgicalBlocks.size());
    }

    @Test
    public void shouldGetAllOverlappingSurgicalBlocksForGivenDateRangeForAGivenProvider() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:30:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 16:00:00");
        Provider provider = Context.getProviderService().getProvider(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertEquals(2, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
        assertEquals(2, surgicalBlocks.get(1).getId(), 0.0);
    }
}