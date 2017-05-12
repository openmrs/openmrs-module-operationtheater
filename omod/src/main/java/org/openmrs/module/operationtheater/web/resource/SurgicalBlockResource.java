package org.openmrs.module.operationtheater.web.resource;


import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.service.SurgicalBlockService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/surgicalBlock", supportedClass = SurgicalBlock.class, supportedOpenmrsVersions = {"2.0.*", "2.1.*"})
public class SurgicalBlockResource extends DataDelegatingCrudResource<SurgicalBlock> {


    @Override
    public SurgicalBlock getByUniqueId(String s) {
        return null;
    }

    @Override
    protected void delete(SurgicalBlock surgicalBlock, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Delete not supported on SurgicalBlock resource");
    }

    @Override
    public SurgicalBlock newDelegate() {
        return new SurgicalBlock();
    }

    @Override
    public SurgicalBlock save(SurgicalBlock surgicalBlock) {
        return Context.getService(SurgicalBlockService.class).save(surgicalBlock);
    }

    @Override
    public void purge(SurgicalBlock surgicalBlock, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Purge not supported on SurgicalBlock resource");
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        if ((representation instanceof DefaultRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("id");
            description.addProperty("provider", Representation.DEFAULT);
            description.addProperty("location", Representation.DEFAULT);
            description.addProperty("startDatetime");
            description.addProperty("endDatetime");
            description.addProperty("surgicalAppointments", Representation.REF);
            return description;
        }
        if ((representation instanceof FullRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("id");
            description.addProperty("provider", Representation.FULL);
            description.addProperty("location", Representation.FULL);
            description.addProperty("startDatetime");
            description.addProperty("endDatetime");
            description.addProperty("surgicalAppointments", Representation.FULL);
            return description;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
        delegatingResourceDescription.addProperty("id");
        delegatingResourceDescription.addRequiredProperty("provider");
        delegatingResourceDescription.addRequiredProperty("location");
        delegatingResourceDescription.addRequiredProperty("startDatetime");
        delegatingResourceDescription.addRequiredProperty("endDatetime");
        delegatingResourceDescription.addProperty("surgicalAppointments");
        return delegatingResourceDescription;
    }
}
