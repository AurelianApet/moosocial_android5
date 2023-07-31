package com.moosocial.moosocialapp.util.Mention.mentions;

import com.moosocial.moosocialapp.util.Mention.tokenization.QueryToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 *
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class MentionUsers {
    protected List<UserMention> mData;

    public MentionUsers(List<UserMention> mData)
    {
        this.mData = mData;
    }

    // Returns a subset
    public List<UserMention> getSuggestions(QueryToken queryToken) {
        String prefix = queryToken.getKeywords().toLowerCase();
        List<UserMention> suggestions = new ArrayList<>();
        if (mData != null) {
            for (UserMention suggestion : mData) {
                String name = suggestion.getSuggestiblePrimaryText().toLowerCase();
                if (name.startsWith(prefix)) {
                    suggestions.add(suggestion);
                }
            }
        }
        return suggestions;
    }
}
