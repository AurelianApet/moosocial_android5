package com.moosocial.moosocialapp.domain;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class TokenDeserializer implements JsonDeserializer<Token> {
    @Override
    public Token deserialize(final JsonElement json,  final Type typeOfT, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        final JsonObject jsonObject = json.getAsJsonObject();

        //Log.d("moodebug", "TokenDeserializer " + jsonObject.get("refresh_token").getAsString() + " to String " + jsonObject.get("access_token").toString());
        final Token token = new Token(
                jsonObject.get("access_token").isJsonNull() ? "":jsonObject.get("access_token").getAsString(),
                jsonObject.get("token_type").isJsonNull() ? "":jsonObject.get("token_type").getAsString(),
                jsonObject.get("expires_in").isJsonNull() ? "":jsonObject.get("expires_in").getAsString(),
                jsonObject.get("refresh_token").isJsonNull() ? "":jsonObject.get("refresh_token").getAsString(),
                jsonObject.get("scope").isJsonNull() ? "":jsonObject.get("scope").getAsString(),
                ""
        );
        return token;

    }
}
