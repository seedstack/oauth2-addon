/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.oauth.fixtures;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import org.seedstack.oauth.OAuthConfig;
import org.seedstack.oauth.fixtures.provider.TokenData;
import org.seedstack.seed.SeedException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TokenBuilder {
    public static final String ACCESS_TOKEN_VALUE =
            "ya29.Gl0OBRawZls_r7atLBziIl051NW1xWZTp96JbPyuz8g09Ty0QvavJaQzBMtpclRxDxgq2b3pdQbUFCDaRq-qIJ7Qsw_KQmYMhxxczJsXP7DqMkiQf7CvOsZhwQkqpfE";
    public static final String SUBJECT_ID = "118090614001964330293";
    private static final String TOKEN_TYPE = "Bearer";
    private static final int TOKEN_EXPIRES_IN = 3563;
    private static final String RSA_KEY_ID = "5ef69cb85daeef24c4791e20553af176fd216e68";
    private String clientID = "";
    private boolean testInvalidNonce;
    private boolean testTokenExpiry;
    private boolean testInvalidAudience;
    private boolean buildOnlyAccessToken;
    private OAuthConfig oauthConfig;

    public TokenBuilder(OAuthConfig oauthConfig) {
        this.oauthConfig = oauthConfig;
    }

    public TokenData buildToken(String nonce, List<String> scopes) {
        TokenData td = new TokenData();
        td.setAccess_token(buildAccessToken());
        td.setExpires_in(TOKEN_EXPIRES_IN);
        td.setToken_type(TOKEN_TYPE);
        td.setScope(String.join(" ", scopes));
        if (!buildOnlyAccessToken) {
            if (oauthConfig.algorithms().isPlainTokenAllowed()) {
                td.setId_token(buildPlainJWT(nonce));
            } else {
                td.setId_token(buildSignedJWT(nonce));
            }
        }

        return td;
    }

    private String buildAccessToken() {

        AccessToken accessToken = new BearerAccessToken(ACCESS_TOKEN_VALUE);
        return accessToken.getValue();

    }

    private String buildPlainJWT(String nonce) {

        PlainHeader plainHeader = new PlainHeader(null, null, null, null, null);

        JWTClaimsSet jWTClaimsSet = buildJWTClaimSet(nonce);

        return new PlainJWT(plainHeader, jWTClaimsSet).serialize();

    }

    private String buildSignedJWT(String nonce) {
        SignedJWT signedJWT;
        try {
            JWKSet jwkSet = JWKSet.load(checkNotNull(oauthConfig.provider()
                    .getJwks().toURL(), "Unable to load JWK set"));
            JWK key = jwkSet.getKeyByKeyId(RSA_KEY_ID);

            JWTClaimsSet jWTClaimsSet = buildJWTClaimSet(nonce);
            JWSHeader jswHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(RSA_KEY_ID).build();
            signedJWT = new SignedJWT(jswHeader, jWTClaimsSet);

            JWSSigner signer = new RSASSASigner((RSAKey) key);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw SeedException.wrap(e, TokenErrorCode.UNABLE_TO_FETCH_PRIVATE_KEY);
        } catch (IOException | ParseException e1) {
            throw SeedException.wrap(e1, TokenErrorCode.FAILED_TO_LOAD_JWKS);
        }
    }

    private JWTClaimsSet buildJWTClaimSet(String nonce) {
        Long iat = System.currentTimeMillis();
        Long exp = (iat) + (3600 * 60);

        if (testInvalidAudience) {
            clientID = "2344574985.incorrect.client";
        } else {
            clientID = oauthConfig.getClientId();
        }
        if (testInvalidNonce) {
            nonce = "TL2-yFCanqzoiVwOPxQwVHrf.invalid.nonce";
        }
        if (testTokenExpiry) {
            exp = iat;
        }

        return new JWTClaimsSet.Builder()
                .claim("at_hash", "GlCoaDfQuUvpilxrKRBBdQ")
                .audience(clientID)
                .subject(SUBJECT_ID)
                .claim("email_verified", "true")
                .claim("azp", clientID)
                .issuer("https://mockedserver.com")
                .expirationTime(new Date(exp))
                .claim("nonce", nonce)
                .issueTime(new Date(iat))
                .claim("email", "jr@gmail.com")
                .claim("array", new String[]{"item1", "item2", "item3"})
                .build();
    }

        public void setTestInvalidNonce ( boolean testInvalidNonce){
            this.testInvalidNonce = testInvalidNonce;
        }

        public void setTestTokenExpiry ( boolean testTokenExpiry){
            this.testTokenExpiry = testTokenExpiry;
        }

        public void setTestInvalidAudience ( boolean testInvalidAudience){
            this.testInvalidAudience = testInvalidAudience;
        }

        public void setFlagForAccessToken ( boolean buildOnlyAccessToken){
            this.buildOnlyAccessToken = buildOnlyAccessToken;
        }
    }
