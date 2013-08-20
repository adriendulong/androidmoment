package com.moment.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.models.Chat;
import com.moment.models.Moment;
import com.moment.util.CommonUtilities;
import com.moment.util.ImageFetcher;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by adriendulong on 13/08/13.
 */
public class ChatAdapter extends ArrayAdapter<Chat> {
    Context context;
    int layoutResourceId;
    List<Chat> data = new ArrayList<Chat>();
    private ImageFetcher mImageFetcher;
    private final Transformation roundTrans = new RoundTransformation();



    public ChatAdapter(Context context, int layoutResourceId, ArrayList<Chat> data, ImageFetcher imagefetcher) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.mImageFetcher = imagefetcher;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ChatHolder holder = null;


        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(isUser(data.get(position))) row = vi.inflate(layoutResourceId, parent, false);
        else row = vi.inflate(R.layout.chat_message_gauche , parent, false);

        holder = new ChatHolder();

        holder.userPhoto = (ImageView)row.findViewById(R.id.photo_user);
        holder.userName = (TextView)row.findViewById(R.id.autheur);
        holder.heure = (TextView)row.findViewById(R.id.heure);
        holder.date = (TextView)row.findViewById(R.id.date);
        holder.messageChat = (TextView)row.findViewById(R.id.chat_message_text);

        row.setTag(holder);

        holder.userName.setText(data.get(position).getUser().getFirstName()+" "+data.get(position).getUser().getLastName()+".");
        holder.date.setText(CommonUtilities.getDateTimeFormat(Locale.getDefault().getLanguage()).format(data.get(position).getDate()));
        holder.messageChat.setText(data.get(position).getMessage());
        if(data.get(position).getUser().getPictureProfileUrl()!=null) Picasso.with(context).load(data.get(position).getUser().getPictureProfileUrl()).resize(200, 200).transform(roundTrans).into(holder.userPhoto); //mImageFetcher.loadImage(data.get(position).getUser().getPictureProfileUrl(), holder.userPhoto, true);
        else holder.userPhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_profilpic_up));



        return row;
    }

    static class ChatHolder
    {
        ImageView userPhoto;
        TextView userName;
        TextView heure, date;
        TextView messageChat;

    }


    private boolean isUser(Chat chat){
        if(chat.getUserId() == AppMoment.getInstance().user.getId()) return true;
        else return false;
    }
}
