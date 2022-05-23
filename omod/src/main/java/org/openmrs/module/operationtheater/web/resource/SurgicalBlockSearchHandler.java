package org.openmrs.module.operationtheater.web.resource;

import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.service.SurgicalBlockService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@Component
public class SurgicalBlockSearchHandler implements SearchHandler {
	
	@Autowired
	@Qualifier("surgicalBlockService")
	SurgicalBlockService surgicalBlockService;
	
	private final SearchConfig searchConfig = new SearchConfig("surgicalBlocksInDateRange",
	        RestConstants.VERSION_1 + "/surgicalBlock", Arrays.asList("1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"),
	        new SearchQuery.Builder(
	                "Allows you to find surgical blocks which fall between the startDatetime and endDatetime")
	                        .withRequiredParameters("startDatetime", "endDatetime")
							.withOptionalParameters("includeVoided", "activeBlocks").build());

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}
	
	@Override
	public PageableResult search(RequestContext requestContext) throws ResponseException {
		String startDatetime = requestContext.getRequest().getParameter("startDatetime");
		String endDatetime = requestContext.getRequest().getParameter("endDatetime");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		String includeVoidedString = requestContext.getRequest().getParameter("includeVoided");
		Boolean includeVoided = Boolean.valueOf(includeVoidedString);
		String activeBlocksString = requestContext.getRequest().getParameter("activeBlocks");
		Boolean activeBlocks = Boolean.valueOf(activeBlocksString);

		List<SurgicalBlock> results = null;
		try {
			results = surgicalBlockService.getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(
			    simpleDateFormat.parse(startDatetime), simpleDateFormat.parse(endDatetime), includeVoided, activeBlocks);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return new AlreadyPaged<>(requestContext, results, false);
	}
}
