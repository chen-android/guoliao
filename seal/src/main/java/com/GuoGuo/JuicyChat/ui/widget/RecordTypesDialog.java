package com.GuoGuo.JuicyChat.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.response.TransferRecordTypesData;

import java.util.List;

/**
 * Created by cs on 2017/5/25.
 */

public class RecordTypesDialog extends Dialog {
    private List<TransferRecordTypesData> datas;
    private GridView gv;
    private int selectedPosition;
    
    public RecordTypesDialog(@NonNull Context context, List<TransferRecordTypesData> datas, int selectedPosition) {
        super(context, R.style.WinDialog);
        this.datas = datas;
        this.selectedPosition = selectedPosition;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_transfer_record_types);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.ShareDialogAnimation);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getContext().getResources().getDisplayMetrics());
        window.setAttributes(attributes);
        gv = (GridView) findViewById(R.id.dialog_transfer_record_gv);
        if (datas != null) {
            
        }
    }
    
    private class MyAdapter extends BaseAdapter {
        
        @Override
        public int getCount() {
            return datas.size();
        }
        
        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(parent.getContext());
            tv.setText(datas.get(position).getTypename());
            int tb = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, parent.getContext().getResources().getDisplayMetrics());
            int lr = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, parent.getContext().getResources().getDisplayMetrics());
            tv.setPadding(lr, tb, lr, tb);
            return null;
        }
    }
    
}
