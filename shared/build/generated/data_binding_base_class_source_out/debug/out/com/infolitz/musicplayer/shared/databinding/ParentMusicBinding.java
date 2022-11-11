// Generated by view binder compiler. Do not edit!
package com.infolitz.musicplayer.shared.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.infolitz.musicplayer.shared.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ParentMusicBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ImageView ivBack;

  @NonNull
  public final ImageView ivLogo;

  @NonNull
  public final ImageView ivNext;

  @NonNull
  public final ImageView ivPause;

  @NonNull
  public final ImageView ivPlay;

  @NonNull
  public final ImageView ivPlayList;

  @NonNull
  public final ImageView ivPrevious;

  @NonNull
  public final LinearLayout ll1;

  @NonNull
  public final LinearLayout ll2;

  @NonNull
  public final RelativeLayout llPrent;

  @NonNull
  public final LinearLayout llTool;

  @NonNull
  public final RelativeLayout rlMain;

  @NonNull
  public final SeekBar sbMusic;

  @NonNull
  public final TextView tvETime;

  @NonNull
  public final TextView tvSTime;

  @NonNull
  public final TextView tvTitle;

  private ParentMusicBinding(@NonNull RelativeLayout rootView, @NonNull ImageView ivBack,
      @NonNull ImageView ivLogo, @NonNull ImageView ivNext, @NonNull ImageView ivPause,
      @NonNull ImageView ivPlay, @NonNull ImageView ivPlayList, @NonNull ImageView ivPrevious,
      @NonNull LinearLayout ll1, @NonNull LinearLayout ll2, @NonNull RelativeLayout llPrent,
      @NonNull LinearLayout llTool, @NonNull RelativeLayout rlMain, @NonNull SeekBar sbMusic,
      @NonNull TextView tvETime, @NonNull TextView tvSTime, @NonNull TextView tvTitle) {
    this.rootView = rootView;
    this.ivBack = ivBack;
    this.ivLogo = ivLogo;
    this.ivNext = ivNext;
    this.ivPause = ivPause;
    this.ivPlay = ivPlay;
    this.ivPlayList = ivPlayList;
    this.ivPrevious = ivPrevious;
    this.ll1 = ll1;
    this.ll2 = ll2;
    this.llPrent = llPrent;
    this.llTool = llTool;
    this.rlMain = rlMain;
    this.sbMusic = sbMusic;
    this.tvETime = tvETime;
    this.tvSTime = tvSTime;
    this.tvTitle = tvTitle;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ParentMusicBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ParentMusicBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.parent_music, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ParentMusicBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iv_Back;
      ImageView ivBack = ViewBindings.findChildViewById(rootView, id);
      if (ivBack == null) {
        break missingId;
      }

      id = R.id.iv_logo;
      ImageView ivLogo = ViewBindings.findChildViewById(rootView, id);
      if (ivLogo == null) {
        break missingId;
      }

      id = R.id.iv_Next;
      ImageView ivNext = ViewBindings.findChildViewById(rootView, id);
      if (ivNext == null) {
        break missingId;
      }

      id = R.id.iv_Pause;
      ImageView ivPause = ViewBindings.findChildViewById(rootView, id);
      if (ivPause == null) {
        break missingId;
      }

      id = R.id.iv_Play;
      ImageView ivPlay = ViewBindings.findChildViewById(rootView, id);
      if (ivPlay == null) {
        break missingId;
      }

      id = R.id.ivPlayList;
      ImageView ivPlayList = ViewBindings.findChildViewById(rootView, id);
      if (ivPlayList == null) {
        break missingId;
      }

      id = R.id.iv_previous;
      ImageView ivPrevious = ViewBindings.findChildViewById(rootView, id);
      if (ivPrevious == null) {
        break missingId;
      }

      id = R.id.ll_1;
      LinearLayout ll1 = ViewBindings.findChildViewById(rootView, id);
      if (ll1 == null) {
        break missingId;
      }

      id = R.id.ll_2;
      LinearLayout ll2 = ViewBindings.findChildViewById(rootView, id);
      if (ll2 == null) {
        break missingId;
      }

      RelativeLayout llPrent = (RelativeLayout) rootView;

      id = R.id.llTool;
      LinearLayout llTool = ViewBindings.findChildViewById(rootView, id);
      if (llTool == null) {
        break missingId;
      }

      id = R.id.rl_Main;
      RelativeLayout rlMain = ViewBindings.findChildViewById(rootView, id);
      if (rlMain == null) {
        break missingId;
      }

      id = R.id.sb_Music;
      SeekBar sbMusic = ViewBindings.findChildViewById(rootView, id);
      if (sbMusic == null) {
        break missingId;
      }

      id = R.id.tvETime;
      TextView tvETime = ViewBindings.findChildViewById(rootView, id);
      if (tvETime == null) {
        break missingId;
      }

      id = R.id.tvSTime;
      TextView tvSTime = ViewBindings.findChildViewById(rootView, id);
      if (tvSTime == null) {
        break missingId;
      }

      id = R.id.tv_Title;
      TextView tvTitle = ViewBindings.findChildViewById(rootView, id);
      if (tvTitle == null) {
        break missingId;
      }

      return new ParentMusicBinding((RelativeLayout) rootView, ivBack, ivLogo, ivNext, ivPause,
          ivPlay, ivPlayList, ivPrevious, ll1, ll2, llPrent, llTool, rlMain, sbMusic, tvETime,
          tvSTime, tvTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}