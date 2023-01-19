package com.team5.besthouse.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.team5.besthouse.R;
import com.team5.besthouse.interfaces.GetBitMapCallBack;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends SliderViewAdapter<ImageSliderAdapter.SliderViewHolder> {
    private List<String> imageList = new ArrayList<>();
    private Context context;

    public ImageSliderAdapter(Context context, List<String> imageUrlList)
    {
        this.context = context;
        this.imageList = imageUrlList;
    }

    @Override
    public SliderViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, null);
        return new SliderViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(SliderViewHolder viewHolder, int position) {
        loadImageFromFSUrl(imageList.get(position), new GetBitMapCallBack() {
            @Override
            public void getBitMap(Bitmap bitmap) {
                viewHolder.imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public int getCount() {
        return imageList.size();
    }



    class SliderViewHolder extends SliderViewAdapter.ViewHolder {


        ShapeableImageView imageView;
        View itemView;

        public SliderViewHolder (View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.shapeAbleImageView);
            this.itemView = itemView;
        }
    }

    private void loadImageFromFSUrl(String imageURL, final GetBitMapCallBack callBack)
    {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        try {
            StorageReference httpsReference = firebaseStorage.getReferenceFromUrl(imageURL);
            final long ONE_MEGABYTE = 1024 * 1024;
            httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                    callBack.getBitMap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
