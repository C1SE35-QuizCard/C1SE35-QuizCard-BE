package com.example.quizcards.aspect.actuator;

import java.util.List;

public record SecuredMappingsSearchResult(
        List<PreAuthorizeMappingAspect.SecuredEndpoint> endpoints,
        String summary
) {}