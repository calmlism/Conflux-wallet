package pro.conflux.wallet.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.conflux.wallet.R;

/**
 * Created by Conflux on
 */

//自定义适配器
public class PictureAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Picture> pictures;

    public PictureAdapter(String[] titles, int[] images, Context context)
    {
        super();
        pictures = new ArrayList<Picture>();
        inflater = LayoutInflater.from(context);
        for (int i = 0; i < images.length; i++)
        {
            Picture picture = new Picture(titles[i], images[i]);
            pictures.add(picture);
        }
    }

    @Override
    public int getCount()
    {
        if (null != pictures)
        {
            return pictures.size();
        } else
        {
            return 0;
        }
    }

    @Override
    public Object getItem(int position)
    {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.picture_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(pictures.get(position).getTitle());
        viewHolder.image.setImageResource(pictures.get(position).getImageId());
        return convertView;
    }

}

class ViewHolder
{
    public TextView title;
    public ImageView image;
}

class Picture
{
    private String title;
    private int imageId;

    public Picture()
    {
        super();
    }

    public Picture(String title, int imageId)
    {
        super();
        this.title = title;
        this.imageId = imageId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getImageId()
    {
        return imageId;
    }

    public void setImageId(int imageId)
    {
        this.imageId = imageId;
    }
}
