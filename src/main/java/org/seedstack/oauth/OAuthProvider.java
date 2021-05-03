/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.oauth;

import java.net.URI;
import java.util.Optional;

/**
 * This interface is used to query details about the configured OAuth provider.
 */
public interface OAuthProvider {
    URI getAuthorizationEndpoint();

    URI getTokenEndpoint();

    Optional<URI> getRevocationEndpoint();

    Optional<URI> getIssuer();

    Optional<URI> getUserInfoEndpoint();

    Optional<URI> getJwksEndpoint();

    String getIdSigningAlgorithm();
}
