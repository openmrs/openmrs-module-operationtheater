package org.openmrs.module.operationtheater.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttributeType;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentAttributeTypeService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

@Resource(name = RestConstants.VERSION_1
        + "/surgicalAppointmentAttributeType", supportedClass = SurgicalAppointmentAttributeType.class, supportedOpenmrsVersions = {
                "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*" })
public class SurgicalAppointmentAttributeTypeResource extends MetadataDelegatingCrudResource<SurgicalAppointmentAttributeType> {
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		SurgicalAppointmentAttributeTypeService surgicalAppointmentAttributeTypeService = (SurgicalAppointmentAttributeTypeService) Context
		        .getService(SurgicalAppointmentAttributeTypeService.class);
		List<SurgicalAppointmentAttributeType> attributeTypes = surgicalAppointmentAttributeTypeService
		        .getAllAttributeTypes();
		return new AlreadyPaged<SurgicalAppointmentAttributeType>(context, attributeTypes, false);
	}
	
	@Override
	public SurgicalAppointmentAttributeType getByUniqueId(String uuid) {
		return Context.getService(SurgicalAppointmentAttributeTypeService.class)
		        .getSurgicalAppointmentAttributeTypeByUuid(uuid);
	}
	
	@Override
	public void delete(SurgicalAppointmentAttributeType surgicalAppointmentAttributeType, String s,
	        RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException(
		        "Delete not supported on SurgicalAppointmentAttributeType resource");
	}
	
	@Override
	public SurgicalAppointmentAttributeType newDelegate() {
		return new SurgicalAppointmentAttributeType();
	}
	
	@Override
	public SurgicalAppointmentAttributeType save(SurgicalAppointmentAttributeType surgicalAppointmentAttributeType) {
		throw new ResourceDoesNotSupportOperationException(
		        "Save not supported on SurgicalAppointmentAttributeType resource");
	}
	
	@Override
	public void purge(SurgicalAppointmentAttributeType surgicalAppointmentAttributeType, RequestContext requestContext)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException(
		        "Purge not supported on SurgicalAppointmentAttributeType resource");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		if ((representation instanceof DefaultRepresentation) || (representation instanceof RefRepresentation)) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("format");
			description.addProperty("sortWeight");
			return description;
		} else if ((representation instanceof FullRepresentation)) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("format");
			description.addProperty("sortWeight");
			description.addProperty("description");
			return description;
		}
		return null;
	}
}
