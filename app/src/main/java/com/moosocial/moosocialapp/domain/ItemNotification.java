package com.moosocial.moosocialapp.domain;

import android.text.Html;

import com.google.gson.internal.LinkedTreeMap;
import com.moosocial.moosocialapp.util.UtilsMoo;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class ItemNotification {
   private String id;
   private Object from;
   private String to;
   private String created_time;
   private String updated_time;
   private String title;
   private String link;
   private Boolean unread;
   private Object object;

   public String getFromName()
   {
      return (String)((LinkedTreeMap)from).get("name");
   }

   public String getTitle()
   {
      return Html.fromHtml(title).toString();
   }

   public String getCreatedTime()
   {
      return created_time;
   }

   public Boolean getUnread()
   {
      return this.unread;
   }

   public String getFromAvatar()
   {
      return (String)((LinkedTreeMap)from).get("avatar");
   }

   public String getLink()
   {
      return this.link;
   }

   public String getId()
   {
      return this.id;
   }

   public void setUnread(Boolean unread)
   {
      this.unread = unread;
   }
}
