package com.moosocial.moosocialapp.presentation.view.activities.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moosocial.moosocialapp.R;


/**
 * Created by welcome on 06/01/2016.
 */
public class SliderAdapter extends PagerAdapter {

    private int[] slider_backgrounds = {
        R.drawable.ic_slider_bg1,
        R.drawable.ic_slider_bg2,
        R.drawable.ic_slider_bg3,
        R.drawable.ic_slider_bg4,
    };

    private int[] slider_icons = {
        R.drawable.ic_slider_icon1,
        R.drawable.ic_slider_icon2,
        R.drawable.ic_slider_icon3,
        R.drawable.ic_slider_icon4,
    };

    private int[] slider_texts = {
        R.string.text_slide1,
        R.string.text_slide2,
        R.string.text_slide3,
        R.string.text_slide4,
    };
    private Context ctx;
    private LayoutInflater layoutInflater;
    private int item_position = 0;

    public SliderAdapter(Context ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return slider_icons.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    public int getPosition()
    {
        return item_position;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        item_position = position;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        //set background
        LinearLayout slider_wrapper = (LinearLayout) item_view.findViewById(R.id.slider_wrapper);
        slider_wrapper.setBackground(ContextCompat.getDrawable(this.ctx, slider_backgrounds[position]));

        //set slider icon
        ImageView slider_icon = (ImageView) item_view.findViewById(R.id.image_slider_icon);
        slider_icon.setImageResource(slider_icons[position]);

        //set slider text
        TextView slider_text = (TextView) item_view.findViewById(R.id.text_slider_icon);
        slider_text.setText(slider_texts[position]);
        if(position == 0) {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 0, 0, 0);
            //slider_text.setLayoutParams(llp);

           /* TextView text_howtoplay = (TextView) item_view.findViewById(R.id.text_how_to_play);
            text_howtoplay.setVisibility(View.VISIBLE);*/
            //text_howtoplay.setLayoutParams(llp);
        }
        else
        {
            slider_text.setText(slider_texts[position]);
        }
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
