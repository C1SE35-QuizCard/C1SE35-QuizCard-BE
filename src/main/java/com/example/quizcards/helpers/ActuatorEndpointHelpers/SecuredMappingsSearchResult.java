package com.example.quizcards.helpers.ActuatorEndpointHelpers;

import java.util.List;

public record SecuredMappingsSearchResult(
        List<PreAuthorizeMappingAspect.SecuredEndpoint> endpoints,
        String summary
) {}