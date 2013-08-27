package com.moment.classes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.activities.MomentInfosActivity;
import com.moment.models.Moment;
import com.moment.util.ImageFetcher;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by swann on 23/08/13.
 */
public class SearchAdapter extends ArrayAdapter<Moment> {

    private int layoutResourceId;
    private Context context;
    private ArrayList<Moment> data = new ArrayList<Moment>();
    private ImageFetcher mImageFetcher;
    private Transformation roundTrans = new RoundTransformation();
    private int positionTmp;

    public SearchAdapter(Context context, int layoutResourceId, ArrayList<Moment> data, ImageFetcher imagefetcher) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.mImageFetcher = imagefetcher;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        MomentHolder holder = null;
        positionTmp = position;

        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = vi.inflate(R.layout.moment_search, parent, false);

        holder = new MomentHolder();

        holder.photo = (ImageView) row.findViewById(R.id.moment_photo);
        holder.name = (TextView) row.findViewById(R.id.moment_name);

        if(data.get(position).getIsOpenInvit() != null && data.get(position).getIsOpenInvit())
        {
            holder.isPublic = (ImageView) row.findViewById(R.id.is_public);
            if(AppMoment.getInstance().user.getMomentById(data.get(position).getId()) != null)
            {
                holder.isPublic.setImageResource(R.drawable.btn_follow_moment_down);
            } else {
                holder.isPublic.setImageResource(R.drawable.btn_follow_moment_up);
                final View finalRow = row;
                final MomentHolder finalHolder = holder;
                holder.isPublic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringEntity entity = null;
                        finalHolder.isPublic.setImageResource(R.drawable.btn_follow_moment_down);
                        try {
                            JSONArray users = new JSONArray();
                            users.put(AppMoment.getInstance().user.getUserToJSON());
                            JSONObject tab = new JSONObject();
                            tab.put("users", users);
                            entity = new StringEntity(tab.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        MomentApi.postJSON(getContext(), "newguests/"+ data.get(positionTmp).getId(), entity, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                Intent intent = new Intent(getContext(), MomentInfosActivity.class);
                                intent.putExtra("id", data.get(position).getId());
                                intent.putExtra("precedente", "search");
                                getContext().startActivity(intent);
                            }

                            @Override
                            public void onFailure(Throwable e, JSONObject response) {
                                e.printStackTrace();
                                response.toString();
                            }
                        });
                    }
                });
            }
        }

        row.setTag(holder);
        holder.name.setText(data.get(position).getName());
        if(data.get(position).getUrlCover()!=null) Picasso.with(context).load(data.get(position).getUrlCover()).resize(200, 200).into(holder.photo);
        else holder.photo.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_profilpic_up));
        return row;
    }

    static class MomentHolder
    {
        ImageView photo;
        TextView name;
        ImageView isPublic;
    }

}
