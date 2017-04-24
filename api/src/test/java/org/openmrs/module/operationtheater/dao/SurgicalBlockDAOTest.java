package org.openmrs.module.operationtheater.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
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

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class SurgicalBlockDAOTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    SurgicalBlockDAO surgicalBlockDAO;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("SurgicalBlock.xml");
    }

    @Test
    public void shouldGetAllSurgicalBlocksBetweenStartDatetimeAndEndDatetime() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        Date startDatetime = simpleDateFormat.parse("2017-04-23 11:00:00");
        Date endDatetime = simpleDateFormat.parse("2017-04-24 12:00:00");
        Location location = new Location();
        ArrayList<SurgicalBlock> surgicalBlocks = surgicalBlockDAO.getSurgicalBlocksBetweenStartDatetimeAndEndDatetimes(startDatetime, endDatetime, location);
        assertEquals(surgicalBlocks.size(), 1);
    }

}