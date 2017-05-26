package org.openmrs.module.operationtheater.web.resource;


import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
public class SurgicalBlockSearchHandlerTest extends MainResourceControllerTest{

    @Before
    public void init() throws Exception {
        executeDataSet("SurgicalBlock.xml");
    }

    @Override
    public String getURI() {
        return "surgicalBlock";
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public long getAllCount() {
        return 0;
    }

    @Test
    public void shouldGetTheSurgicalBlocksWhichFallInTheDateRange() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.addParameter("startDatetime", "2017-04-24T10:00:00.000");
        request.addParameter("endDatetime", "2017-04-24T17:59:00.000");
        SimpleObject results = deserialize(handle(request));
        List list= (List) results.get("results");
        LinkedHashMap<String, String> surgicalBlock = (LinkedHashMap<String, String>) list.get(0);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date blockStartDatetime = simpleDateFormat.parse(surgicalBlock.get("startDatetime"));
        Date blockEndDatetime = simpleDateFormat.parse(surgicalBlock.get("endDatetime"));

        assertEquals(simpleDateFormat.parse("2017-04-24T10:00:00.000"), blockStartDatetime);
        assertEquals(simpleDateFormat.parse("2017-04-24T11:30:00.000"), blockEndDatetime);
    };
}
