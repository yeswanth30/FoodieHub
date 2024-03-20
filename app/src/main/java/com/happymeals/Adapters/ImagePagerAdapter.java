package com.happymeals.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.happymeals.ImageFragment;

import java.util.List;

public class ImagePagerAdapter extends FragmentStateAdapter {
    private List<String> imageUrls;

    public ImagePagerAdapter(FragmentActivity fragmentActivity, List<String> imageUrls) {
        super(fragmentActivity);
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String imageUrl = imageUrls.get(position);
        return ImageFragment.newInstance(imageUrl);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}

