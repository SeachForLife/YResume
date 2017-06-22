package com.carl_yang.resume;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.carl_yang.data.EuclidState;
import com.carl_yang.fragments.EducationalBgFragment;
import com.carl_yang.fragments.EvaluationFragment;
import com.carl_yang.fragments.ProfessionalSkillFragment;
import com.carl_yang.fragments.QRCodeFragment;
import com.carl_yang.fragments.WorkExperienceFragment;
import com.hanks.htextview.fall.FallTextView;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class MainActivity extends AppCompatActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener {

    private static final int REVEAL_ANIMATION_DURATION = 1000;
    private static final int CIRCLE_RADIUS_DP = 50;
    private static final int MAX_DELAY_SHOW_DETAILS_ANIMATION = 500;
    private static final int ANIMATION_DURATION_SHOW_PROFILE_DETAILS = 500;
    private static final int STEP_DELAY_HIDE_DETAILS_ANIMATION = 80;
    private static final int ANIMATION_DURATION_CLOSE_PROFILE_DETAILS = 500;

    @BindView(R.id.main_title_framelayout)
    FrameLayout mainTitleFramelayout;
    @BindView(R.id.image_view_avatar)
    ImageView imageViewAvatar;
    @BindView(R.id.view_avatar_overlay)
    View viewAvatarOverlay;
    @BindView(R.id.wrapper_list_item)
    RelativeLayout wrapperListItem;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.toolbar_profile)
    RelativeLayout toolbarProfile;
    @BindView(R.id.main_linerlayout)
    LinearLayout mainLinerlayout;
    @BindView(R.id.main_title_name)
    FallTextView mainTitleName;
    @BindView(R.id.wrapper_profile_details)
    FrameLayout wrapperProfileDetails;
    @BindView(R.id.toolbar_profile_back)
    TextView toolbarProfileBack;
    @BindView(R.id.main_message)
    TextView mainMessage;

    private ShapeDrawable sOverlayShape;
    static int sScreenWidth;
    static int sProfileImageHeight;

    private EuclidState mState = EuclidState.Closed;
    private View mOverlayListItemView;

    private AnimatorSet mOpenProfileAnimatorSet;
    private AnimatorSet mCloseProfileAnimatorSet;
    private AnimatorSet manimatewrapperProfileDetailsSet;
    private AnimatorSet mMoveDownWrapperProfileDetailsSet;

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    private EvaluationFragment mEvaluationFra;
    private QRCodeFragment mQRCodeFra;
    private EducationalBgFragment mEduBgFra;
    private WorkExperienceFragment mWorkExpFra;
    private ProfessionalSkillFragment mProSkillFra;

    private int wrapperProfileDetails_old_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();
        initMenuFragment();

        sScreenWidth = getResources().getDisplayMetrics().widthPixels;
        sProfileImageHeight = getResources().getDimensionPixelSize(R.dimen.height_profile_image);
        System.out.println("-----------" + sScreenWidth + "::" + sProfileImageHeight);
        sOverlayShape = buildAvatarCircleOverlay();
        viewAvatarOverlay.setBackground(sOverlayShape);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewAvatar.setEnabled(false);
                mState = EuclidState.Opening;
                int profileDetailsAnimationDelay = getMaxDelayShowDetailsAnimation() * Math.abs(view.getTop())
                        / sScreenWidth;
                if(mOverlayListItemView!=null) {
                    mOverlayListItemView.setAlpha(1f);
                }
                addOverlayListItem(view);
                mainLinerlayout.setVisibility(View.INVISIBLE);
                initBottomFramlayout();
                startRevealAnimation(profileDetailsAnimationDelay);
                animateOpenProfileDetails(profileDetailsAnimationDelay);
            }
        });
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        toolbarProfileBack.setTypeface(iconfont);
        toolbarProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateCloseProfileDetails();
            }
        });
        mainMessage.setTypeface(iconfont);
        mainMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
            }
        });
    }

    private void initBottomFramlayout() {
        mainTitleName.animateText(getResources().getStringArray(R.array.menu_name)[1]);
        mEvaluationFra = EvaluationFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.wrapper_profile_details, mEvaluationFra)
                .commit();
    }

    /**
     * 将视图加载进来
     *
     * @param view
     */
    private void addOverlayListItem(View view) {
        if (mOverlayListItemView == null) {
            mOverlayListItemView = getLayoutInflater().inflate(R.layout.overlay_list_item, activityMain, false);
        } else {
            activityMain.removeView(mOverlayListItemView);
        }
        mOverlayListItemView.findViewById(R.id.view_avatar_overlay).setBackground(sOverlayShape);
        Glide
                .with(this)
                .load(R.drawable.carl)
                .override(sScreenWidth, sProfileImageHeight)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .dontAnimate()
                .into((ImageView) mOverlayListItemView.findViewById(R.id.image_view_reveal_avatar));

        Glide
                .with(this)
                .load(R.drawable.carl)
                .override(sScreenWidth, sProfileImageHeight)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .dontAnimate()
                .into((ImageView) mOverlayListItemView.findViewById(R.id.image_view_avatar));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = view.getTop() + mainTitleFramelayout.getHeight();
        params.bottomMargin = -(view.getBottom() - mainLinerlayout.getHeight());
        activityMain.addView(mOverlayListItemView, params);
        ViewGroup.LayoutParams paramsF = wrapperProfileDetails.getLayoutParams();
        wrapperProfileDetails_old_height = -(view.getBottom() - mainLinerlayout.getHeight());
        paramsF.height = wrapperProfileDetails_old_height;
        paramsF.width = view.getWidth();
        wrapperProfileDetails.setLayoutParams(paramsF);
        mainTitleFramelayout.bringToFront();
    }

    /**
     * 将视图移除,同时将下面的framelayout重新计算高度到标题之下
     *
     * @param view
     */
    private void removeOverlayListItem(View view) {
        if (mOverlayListItemView != null) {
            activityMain.removeView(mOverlayListItemView);
        }
        ViewGroup.LayoutParams paramsF = wrapperProfileDetails.getLayoutParams();
        paramsF.height = view.getTop() + mainTitleFramelayout.getHeight();
        paramsF.width = view.getWidth();
        wrapperProfileDetails.setLayoutParams(paramsF);
        mainTitleFramelayout.bringToFront();
    }

    /**
     * 将内容部分从现在位置移动到标题之下
     *
     * @param
     */
    private void animatewrapperProfileDetails() {
        //移动内容部分
        Animator mWrapperProfileDetailsAnimator = ObjectAnimator.ofFloat(wrapperProfileDetails, View.Y,
                getResources().getDisplayMetrics().heightPixels - wrapperProfileDetails.getHeight(),
                mainTitleFramelayout.getHeight());
        Animator mOverlayListItemViewAnimator=ObjectAnimator.ofFloat(mOverlayListItemView,"alpha",1f,0f);
        //重新计算内容高度
        ViewGroup.LayoutParams paramsF = wrapperProfileDetails.getLayoutParams();
        paramsF.height = getResources().getDisplayMetrics().heightPixels - mainTitleFramelayout.getHeight();
        paramsF.width = getResources().getDisplayMetrics().widthPixels;
        wrapperProfileDetails.setLayoutParams(paramsF);
        if (manimatewrapperProfileDetailsSet == null) {
            List<Animator> profileAnimators = new ArrayList<>();
            profileAnimators.add(mWrapperProfileDetailsAnimator);
            profileAnimators.add(mOverlayListItemViewAnimator);
            manimatewrapperProfileDetailsSet = new AnimatorSet();
            manimatewrapperProfileDetailsSet.playTogether(profileAnimators);
            manimatewrapperProfileDetailsSet.setDuration(3000);
        }
        manimatewrapperProfileDetailsSet.setInterpolator(new DecelerateInterpolator());
        manimatewrapperProfileDetailsSet.start();
    }

    /**
     * 将内容部分位置还原
     */
    private void recycleWrapperProfileDetails() {
        //移动内容部分
        Animator mWrapperProfileDetailsAnimator = ObjectAnimator.ofFloat(wrapperProfileDetails, View.Y,
                mainTitleFramelayout.getHeight(),
                getResources().getDimensionPixelSize(R.dimen.height_profile_picture_with_toolbar));
        Animator mOverlayListItemViewAnimator=ObjectAnimator.ofFloat(mOverlayListItemView,"alpha",0f,1f);
        //重新计算内容高度
        ViewGroup.LayoutParams paramsF = wrapperProfileDetails.getLayoutParams();
        paramsF.height = wrapperProfileDetails_old_height;
        paramsF.width = getResources().getDisplayMetrics().widthPixels;
        wrapperProfileDetails.setLayoutParams(paramsF);
        if (mMoveDownWrapperProfileDetailsSet == null) {
            List<Animator> profileAnimators = new ArrayList<>();
            profileAnimators.add(mWrapperProfileDetailsAnimator);
            profileAnimators.add(mOverlayListItemViewAnimator);
            mMoveDownWrapperProfileDetailsSet = new AnimatorSet();
            mMoveDownWrapperProfileDetailsSet.playTogether(profileAnimators);
            mMoveDownWrapperProfileDetailsSet.setDuration(3000);
        }
//        mMoveDownWrapperProfileDetailsSet.setStartDelay(profileDetailsAnimationDelay);
        mMoveDownWrapperProfileDetailsSet.setInterpolator(new DecelerateInterpolator());
        mMoveDownWrapperProfileDetailsSet.start();
    }

    private void animateOpenProfileDetails(int profileDetailsAnimationDelay) {
        getOpenProfileAnimatorSet(profileDetailsAnimationDelay).start();
    }

    private AnimatorSet getOpenProfileAnimatorSet(int profileDetailsAnimationDelay) {
        if (mOpenProfileAnimatorSet == null) {
            List<Animator> profileAnimators = new ArrayList<>();
            profileAnimators.add(getOpenProfileToolbarAnimator());
            profileAnimators.add(getOpenProfileDetailsAnimator());

            mOpenProfileAnimatorSet = new AnimatorSet();
            mOpenProfileAnimatorSet.playTogether(profileAnimators);
            mOpenProfileAnimatorSet.setDuration(getAnimationDurationShowProfileDetails());
        }
        mOpenProfileAnimatorSet.setStartDelay(profileDetailsAnimationDelay);
        mOpenProfileAnimatorSet.setInterpolator(new DecelerateInterpolator());
        return mOpenProfileAnimatorSet;
    }

    private Animator getOpenProfileDetailsAnimator() {
        Animator mOpenProfileDetailsAnimator = ObjectAnimator.ofFloat(wrapperProfileDetails, View.Y,
                getResources().getDisplayMetrics().heightPixels,
                getResources().getDimensionPixelSize(R.dimen.height_profile_picture_with_toolbar));
        return mOpenProfileDetailsAnimator;
    }

    private ShapeDrawable buildAvatarCircleOverlay() {
        int radius = 666;
        ShapeDrawable overlay = new ShapeDrawable(new RoundRectShape(null,
                new RectF(
                        sScreenWidth / 2 - dpToPx(getCircleRadiusDp() * 2),
                        sProfileImageHeight / 2 - dpToPx(getCircleRadiusDp() * 2),
                        sScreenWidth / 2 - dpToPx(getCircleRadiusDp() * 2),
                        sProfileImageHeight / 2 - dpToPx(getCircleRadiusDp() * 2)),
                new float[]{radius, radius, radius, radius, radius, radius, radius, radius}));
        overlay.getPaint().setColor(getResources().getColor(R.color.gray));

        return overlay;
    }

    private void startRevealAnimation(final int profileDetailsAnimationDelay) {
        mOverlayListItemView.post(new Runnable() {
            @Override
            public void run() {
                getAvatarRevealAnimator().start();
                getAvatarShowAnimator(profileDetailsAnimationDelay).start();
            }
        });
    }

    private SupportAnimator getAvatarRevealAnimator() {
        final LinearLayout mWrapperListItemReveal = (LinearLayout) mOverlayListItemView.findViewById(R.id.wrapper_list_item_reveal);

        int finalRadius = Math.max(mOverlayListItemView.getWidth(), mOverlayListItemView.getHeight());

        final SupportAnimator mRevealAnimator = ViewAnimationUtils.createCircularReveal(
                mWrapperListItemReveal,
                sScreenWidth / 2,
                sProfileImageHeight / 2,
                dpToPx(getCircleRadiusDp() * 2),
                finalRadius);
        mRevealAnimator.setDuration(getRevealAnimationDuration());
        mRevealAnimator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
                mWrapperListItemReveal.setVisibility(View.VISIBLE);
                mOverlayListItemView.setX(0);
            }

            @Override
            public void onAnimationEnd() {

            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        return mRevealAnimator;
    }

    private Animator getAvatarShowAnimator(int profileDetailsAnimationDelay) {
        final Animator mAvatarShowAnimator = ObjectAnimator.ofFloat(mOverlayListItemView, View.Y, mOverlayListItemView.getTop(), toolbarProfile.getBottom());
        mAvatarShowAnimator.setDuration(profileDetailsAnimationDelay + getAnimationDurationShowProfileDetails());
        mAvatarShowAnimator.setInterpolator(new DecelerateInterpolator());
        return mAvatarShowAnimator;
    }

    private Animator getOpenProfileToolbarAnimator() {
        Animator mOpenProfileToolbarAnimator = ObjectAnimator.ofFloat(toolbarProfile, View.Y, -toolbarProfile.getHeight(), 0);
        mOpenProfileToolbarAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                toolbarProfile.setX(0);
                toolbarProfile.bringToFront();
                toolbarProfile.setVisibility(View.VISIBLE);
                wrapperProfileDetails.setX(0);
                wrapperProfileDetails.bringToFront();
                wrapperProfileDetails.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mState = EuclidState.Opened;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return mOpenProfileToolbarAnimator;
    }

    private AnimatorSet getCloseProfileAnimatorSet() {
        if (mCloseProfileAnimatorSet == null) {
            Animator profileToolbarAnimator = ObjectAnimator.ofFloat(toolbarProfile, View.X,
                    0, toolbarProfile.getWidth());

            Animator profilePhotoAnimator = ObjectAnimator.ofFloat(mOverlayListItemView, View.X,
                    0, mOverlayListItemView.getWidth());
            profilePhotoAnimator.setStartDelay(getStepDelayHideDetailsAnimation());

            Animator profileDetailsAnimator = ObjectAnimator.ofFloat(wrapperProfileDetails, View.X,
                    0, wrapperProfileDetails.getWidth());
            profileDetailsAnimator.setStartDelay(getStepDelayHideDetailsAnimation() * 2);

            List<Animator> profileAnimators = new ArrayList<>();
            profileAnimators.add(profileToolbarAnimator);
            profileAnimators.add(profilePhotoAnimator);
            profileAnimators.add(profileDetailsAnimator);

            mCloseProfileAnimatorSet = new AnimatorSet();
            mCloseProfileAnimatorSet.playTogether(profileAnimators);
            mCloseProfileAnimatorSet.setDuration(getAnimationDurationCloseProfileDetails());
            mCloseProfileAnimatorSet.setInterpolator(new AccelerateInterpolator());
            mCloseProfileAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    toolbarProfile.setVisibility(View.INVISIBLE);
                    wrapperProfileDetails.setVisibility(View.INVISIBLE);

                    imageViewAvatar.setEnabled(true);

                    mState = EuclidState.Closed;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return mCloseProfileAnimatorSet;
    }

    private void animateCloseProfileDetails() {
        mainLinerlayout.setVisibility(View.VISIBLE);
        mState = EuclidState.Closing;
        getCloseProfileAnimatorSet().start();
    }

    public EuclidState getState() {
        return mState;
    }

    public int dpToPx(int dp) {
        return Math.round((float) dp * getResources().getDisplayMetrics().density);
    }

    protected int getCircleRadiusDp() {
        return CIRCLE_RADIUS_DP;
    }

    protected int getMaxDelayShowDetailsAnimation() {
        return MAX_DELAY_SHOW_DETAILS_ANIMATION;
    }

    protected int getAnimationDurationShowProfileDetails() {
        return ANIMATION_DURATION_SHOW_PROFILE_DETAILS;
    }

    protected int getStepDelayHideDetailsAnimation() {
        return STEP_DELAY_HIDE_DETAILS_ANIMATION;
    }

    protected int getAnimationDurationCloseProfileDetails() {
        return ANIMATION_DURATION_CLOSE_PROFILE_DETAILS;
    }

    protected int getRevealAnimationDuration() {
        return REVEAL_ANIMATION_DURATION;
    }

    /**
     * ------------------------------
     *  动画效果的侧面菜单实现
     *  -----------------------------
     */

    /**
     * 配置菜单的图标，命名等
     *
     * @return
     */
    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        String[] items = getResources().getStringArray(R.array.menu_name);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.menu_icon);
        for (int i = 0; i < items.length; i++) {
            MenuObject mo = new MenuObject(items[i]);
            mo.setResource(typedArray.getResourceId(i, 0));
            menuObjects.add(mo);
        }
        return menuObjects;
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.height_toolbar));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        if (position != 0)
            mainTitleName.animateText(getResources().getStringArray(R.array.menu_name)[position]);

        switch (position) {
            case 1:
                if (mEvaluationFra == null)
                    mEvaluationFra = EvaluationFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.wrapper_profile_details, mEvaluationFra)
                        .commit();
                recycleWrapperProfileDetails();
                break;
            case 2:
                if (mEduBgFra == null)
                    mEduBgFra = EducationalBgFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.wrapper_profile_details, mEduBgFra)
                        .commit();
                recycleWrapperProfileDetails();
                break;
            case 3:
                if (mWorkExpFra == null)
                    mWorkExpFra = WorkExperienceFragment.newInstance(MainActivity.this);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.wrapper_profile_details, mWorkExpFra)
                        .commit();
                animatewrapperProfileDetails();
                break;
            case 4:
                if (mProSkillFra == null)
                    mProSkillFra = ProfessionalSkillFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.wrapper_profile_details, mProSkillFra)
                        .commit();
                recycleWrapperProfileDetails();
                break;
            case 5:
                if (mQRCodeFra == null)
                    mQRCodeFra = QRCodeFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.wrapper_profile_details, mQRCodeFra)
                        .commit();
                recycleWrapperProfileDetails();
                break;
        }

    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {

    }

    /**
     * ------------------
     * 公用后退设置
     * ------------------
     */

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else if (getState() == EuclidState.Opened) {
            animateCloseProfileDetails();
        } else if (getState() == EuclidState.Closed) {
            super.onBackPressed();
        }
    }
}
