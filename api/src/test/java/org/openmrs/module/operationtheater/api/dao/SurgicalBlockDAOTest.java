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
import java.util.*;

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

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location, null);

        assertEquals(2, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
        assertEquals(9, surgicalBlocks.get(1).getId(), 0.0);
    }

    @Test
    public void shouldNotGetOverlappingSurgicalBlocksWhichStartedAndEndedBeforeTheDateRangeForAGivenLocation() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 09:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Location location = Context.getLocationService().getLocation(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, null, location, null);

        assertEquals(0, surgicalBlocks.size());
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

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null, null);

        assertEquals(2, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
        assertEquals(8, surgicalBlocks.get(1).getId(), 0.0);
    }

    @Test
    public void shouldNotGetSurgicalBlocksWhichStartedAndEndedBeforeTheDateRangeForAProvider() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 09:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Provider provider = Context.getProviderService().getProvider(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, null, null);

        assertEquals(0, surgicalBlocks.size());
    }

    @Test
    public void shouldGetAllOverlappingSurgicalBlocksForDifferentDateRangeForTheSameProviderAndLocationForAnExistingSurgicalBlock() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-25 10:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-25 11:30:00");
        Provider provider = Context.getProviderService().getProvider(1);
        Location location = Context.getLocationService().getLocation(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, location, 1);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(5, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetAllOverlappingSurgicalBlocksForDifferentDateRangeForADifferentProviderForAnExistingSurgicalBlock() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-25 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-25 13:30:00");
        Provider provider = Context.getProviderService().getProvider(2);
        Location location = Context.getLocationService().getLocation(1);


        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, location, 1);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(6, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetAllOverlappingSurgicalBlocksForDifferentDateRangeLocationAndProviderForAnExistingSurgicalBlock() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-25 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-25 14:00:00");
        Provider provider = Context.getProviderService().getProvider(2);
        Location location = Context.getLocationService().getLocation(2);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, location, 1);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(7, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetAllOverlappingSurgicalBlocksForSameDateRangeAndProviderForDifferentLocationForAnExistingSurgicalBlock() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 11:30:00");
        Provider provider = Context.getProviderService().getProvider(1);
        Location location = Context.getLocationService().getLocation(2);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, location, 1);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(8, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetAllOverlappingSurgicalBlocksForSameDateRangeAndLocationForDifferentProviderForAnExistingSurgicalBlock() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 11:30:00");
        Provider provider = Context.getProviderService().getProvider(2);
        Location location = Context.getLocationService().getLocation(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalBlocksFor(startDatetime, endDatetime, provider, location, 1);

        assertEquals(1, surgicalBlocks.size());
        assertEquals(9, surgicalBlocks.get(0).getId(), 0.0);
    }

    @Test
    public void shouldGetAllSurgicalBlocksForGivenDateRangeForAGivenProvider() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 16:00:00");
        Provider provider = Context.getProviderService().getProvider(1);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getSurgicalBlocksFor(startDatetime, endDatetime, provider, null);

        assertEquals(3, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
        assertEquals(2, surgicalBlocks.get(1).getId(), 0.0);
        assertEquals(8, surgicalBlocks.get(2).getId(), 0.0);
    }

    @Test
    public void shouldGetAllSurgicalBlocksForGivenDateRangeForAGivenLocation() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 16:00:00");
        Location location = Context.getLocationService().getLocation(2);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getSurgicalBlocksFor(startDatetime, endDatetime, null, location);

        assertEquals(2, surgicalBlocks.size());
        assertEquals(2, surgicalBlocks.get(0).getId(), 0.0);
        assertEquals(8, surgicalBlocks.get(1).getId(), 0.0);
    }

    @Test
    public void shouldGetAllSurgicalBlocksForGivenDateRangeForAGivenLocationAndProvider() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 16:00:00");
        Provider provider = Context.getProviderService().getProvider(1);
        Location location = Context.getLocationService().getLocation(2);

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getSurgicalBlocksFor(startDatetime, endDatetime, provider, location);

        assertEquals(2, surgicalBlocks.size());
        assertEquals(2, surgicalBlocks.get(0).getId(), 0.0);
        assertEquals(8, surgicalBlocks.get(1).getId(), 0.0);
    }

    @Test
    public void shouldGetAllSurgicalBlocksForGivenDateRange() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 16:00:00");

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getSurgicalBlocksFor(startDatetime, endDatetime, null, null);

        assertEquals(5, surgicalBlocks.size());
        assertEquals(1, surgicalBlocks.get(0).getId(), 0.0);
        assertEquals(2, surgicalBlocks.get(1).getId(), 0.0);
        assertEquals(3, surgicalBlocks.get(2).getId(), 0.0);
        assertEquals(8, surgicalBlocks.get(3).getId(), 0.0);
        assertEquals(9, surgicalBlocks.get(4).getId(), 0.0);
    }

    @Test
    public void shouldReturnEmptySurgicalBlocksForGivenDateRangeWhenThereAreNoSurgicalBlocksExist() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-26 10:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-29 16:00:00");

        List<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getSurgicalBlocksFor(startDatetime, endDatetime, null, null);

        assertEquals(0, surgicalBlocks.size());
    }


    @Test
    public void shouldReturnOverlappingSurgicalSurgicalAppointmentsFromTheOtherSurgicalBlocksForAPatient() throws Exception {
        Date startDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 16:00:00");
        Patient patient = Context.getPatientService().getPatient(1);

        List<SurgicalAppointment> surgicalBlocks = surgicalBlockDAO.getOverlappingSurgicalAppointmentsForPatient(startDatetime, endDatetime, patient);

        assertEquals(1, surgicalBlocks.size());
    }
}