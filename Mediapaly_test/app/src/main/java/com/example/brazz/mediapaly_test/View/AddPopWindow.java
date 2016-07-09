package com.example.brazz.mediapaly_test.View;

/**
 * Created by BrazZ on 2016/6/4.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.brazz.mediapaly_test.R;

/**
 * 自定义popupWindow
 *
 * @author wwj
 *
 *
 */
public class AddPopWindow extends PopupWindow {
    private View conentView;
    private int h1,w1;
    private onPopClicklistener onPopClicklistener;

    public void setOnPopClicklistener(onPopClicklistener onPopClicklistener)
    {
        this.onPopClicklistener = onPopClicklistener;
    }



    public AddPopWindow(final Activity context, View parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.pop_layout, null);
        int h = conentView.getHeight();
        int w = parent.getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        h1 = LayoutParams.WRAP_CONTENT;
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        conentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        h1 = conentView.getMeasuredHeight();
        w1 = conentView.getMeasuredWidth();
        RelativeLayout random = (RelativeLayout) conentView.findViewById(R.id.random);
       random.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onPopClicklistener.onRandomclick();
           }
       });
        RelativeLayout order = (RelativeLayout)conentView.findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPopClicklistener.onOrderclick();
            }
        });

        RelativeLayout single = (RelativeLayout)conentView.findViewById(R.id.single);
        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPopClicklistener.onSingleclick();
            }
        });
    }
    public interface onPopClicklistener
    {
        void onRandomclick();
        void onOrderclick();
        void onSingleclick();
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] - h1);
        } else {
            this.dismiss();
        }
    }
}