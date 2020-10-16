/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.oauth.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class describes the information obtained from successful token validation: the subject id if authenticated
 * (empty string otherwise), the granted scopes and the identity claims if any.
 */
public class TokenValidationResult {
    private final String subjectId;
    private final List<String> scopes;
    private final Map<String, String> claims;

    public TokenValidationResult(String subjectId, List<String> scopes, Map<String, String> claims) {
        this.subjectId = subjectId;
        this.scopes = new ArrayList<>(scopes);
        this.claims = new HashMap<>(claims);
    }

    public String getSubjectId() {
        return subjectId;
    }

    public List<String> getScopes() {
        return Collections.unmodifiableList(scopes);
    }

    public Map<String, String> getClaims() {
        return Collections.unmodifiableMap(claims);
    }
}
