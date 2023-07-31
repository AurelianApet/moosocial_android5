package com.moosocial.moosocialapp.presentation.view.items.search;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class SearchGroup {
        public String title;
        public String url;

        public SearchGroup(String title, String url)
        {
            this.title = title;
            this.url = url;
        }

        public String getTitle()
        {
            return this.title;
        }

        public String getUrl()
        {
            return this.url;
        }
}
