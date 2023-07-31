package com.moosocial.moosocialapp.domain;

import android.text.Html;

import com.moosocial.moosocialapp.util.UtilsMoo;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class ItemSearch extends Item {
   private  String type;
   public String getType()
   {
      return this.type;
   }
   public String getId()
   {
      return this.id;
   }

   public String getUrl()
   {
      return this.url;
   }

   public String getAvatar()
   {
      return this.avatar;
   }

   public String getOwnerId()
   {
      return this.owner_id;
   }

   public String getTitle()
   {
      return Html.fromHtml(this.title_1).toString();
   }

   public String getDescription()
   {
      return UtilsMoo.truncate(Html.fromHtml(this.title_2).toString(),100,"");
   }

   public String getCreated()
   {
      return this.created;
   }
}
