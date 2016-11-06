package sg.ntu.dataminers.singbiker.control;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.entity.History;

/**
 * Created by yvesl on 31.10.2016.
 */

public class HistoryAdapter extends BaseAdapter {

    private List<History> liste;
    Activity context;
    boolean[] itemChecked;
    boolean deletionMode;
    boolean allChecked;

    public HistoryAdapter(Activity context, List<History> l) {
        super();
        this.context = context;
        this.liste = l;
        this.itemChecked = new boolean[liste.size()];
        this.deletionMode = false;
        this.allChecked = false;
    }

    private class ViewHolder {
        ImageView im1;
        CheckBox cb1;
        TextView tx1;
        TextView tx2;
    }

    public void setDeletionMode() {
        this.deletionMode = true;
        this.notifyDataSetChanged();

    }

    public void unsetDeletionMode() {
        this.deletionMode = false;
        this.allChecked = false;
        this.itemChecked = new boolean[liste.size()];
        this.notifyDataSetChanged();
    }

    public boolean[] getItemChecked() {
        return itemChecked;
    }

    public void selectAllItems() {
        for (int i=0; i<itemChecked.length; i++) {
            if (!allChecked)
                itemChecked[i] = true;
            else
                itemChecked[i] = false;
        }

        allChecked = !allChecked;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return liste.size();
    }

    @Override
    public Object getItem(int position) {
        return liste.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_history, null);
            holder = new ViewHolder();

            holder.im1 = (ImageView) convertView.findViewById(R.id.image_listview_history);
            holder.cb1 = (CheckBox) convertView.findViewById(R.id.checkbox_listview_history);
            holder.tx1 = (TextView) convertView.findViewById(R.id.text1_listview_history);
            holder.tx2 = (TextView) convertView.findViewById(R.id.text2_listview_history);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        History h = (History) getItem(position);

        SimpleDateFormat dformat = new SimpleDateFormat("EEEE, dd MMM yyyy");
        String text1 = dformat.format(h.getDate().getTime());
        String text2 = h.getTrip().getTotalDistanceCycled()+" km";

        if (deletionMode) {
            holder.im1.setVisibility(View.GONE);
            holder.cb1.setVisibility(View.VISIBLE);
        } else {
            holder.im1.setVisibility(View.VISIBLE);
            holder.cb1.setVisibility(View.GONE);
        }

        holder.cb1.setChecked(itemChecked[position]);
        holder.tx1.setText(text1);
        holder.tx2.setText(text2);

        holder.cb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemChecked[position] = holder.cb1.isChecked();
            }
        });

        holder.cb1.setClickable(false);

        return convertView;

    }
}
