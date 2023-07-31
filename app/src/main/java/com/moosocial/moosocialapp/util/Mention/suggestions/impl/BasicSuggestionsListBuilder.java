/*
* Copyright 2015 LinkedIn Corp. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*/

package com.moosocial.moosocialapp.util.Mention.suggestions.impl;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.util.Mention.mentions.UserMention;
import com.moosocial.moosocialapp.util.Mention.suggestions.SuggestionsResult;
import com.moosocial.moosocialapp.util.Mention.suggestions.interfaces.Suggestible;
import com.moosocial.moosocialapp.util.Mention.suggestions.interfaces.SuggestionsListBuilder;
import com.moosocial.moosocialapp.util.MooGlobals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Basic implementation of the {@link SuggestionsListBuilder} interface.
 */
public class BasicSuggestionsListBuilder implements SuggestionsListBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public List<Suggestible> buildSuggestions(final @NonNull Map<String, SuggestionsResult> latestResults,
                                              final @NonNull String currentTokenString) {
        List<Suggestible> results = new ArrayList<Suggestible>();
        for (Map.Entry<String, SuggestionsResult> entry : latestResults.entrySet()) {
            SuggestionsResult result = entry.getValue();
            if (currentTokenString.equalsIgnoreCase(result.getQueryToken().getTokenString())) {
                results.addAll(result.getSuggestions());
            }
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(final @NonNull Suggestible suggestion, @Nullable View convertView, ViewGroup parent,
                        final @NonNull Context context, final @NonNull LayoutInflater inflater, final @NonNull Resources resources) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.mention_list_user, null);
        } else {
            view = convertView;
        }

        UserMention user = (UserMention)suggestion;
        //text
        TextView tText = (TextView) view.findViewById(R.id.item_text);

        tText.setText(user.getName());

        //avatar
        ImageView mImageView = (ImageView) view
                .findViewById(R.id.imageItem);

        String url = user.getAvatar();
        Glide.with(context).load(url)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);

        return view;
    }

}
