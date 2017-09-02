package edu.csulb.mediease;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class ShoutAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Message tempValues = null;
    private Activity activity;
    private ArrayList data;

    ShoutAdapter(Activity a, ArrayList d) {

        activity = a;
        data = d;

        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {

        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            vi = inflater.inflate(R.layout.row_shout, parent, false);


            holder = new ViewHolder();
            holder.uname = (TextView) vi.findViewById(R.id.uname);
            holder.hname = (TextView) vi.findViewById(R.id.hname);
            holder.message = (TextView) vi.findViewById(R.id.message);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            // holder.text.setText("No Data");

        } else {
            tempValues = null;
            tempValues = (Message) data.get(position);

            holder.uname.setText(tempValues.getUname());
            holder.hname.setText(tempValues.gethname());
            holder.message.setText(tempValues.getMessage());

        }
        return vi;
    }

    private static class ViewHolder {

        TextView uname;
        TextView hname;
        TextView message;

    }

}
