package nbnl.com.juyeoung.yo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import nbnl.com.juyeoung.yo.Controller.TravelListAdapter;
import nbnl.com.juyeoung.yo.Helper.RecyclerViewPositionHelper;
import nbnl.com.juyeoung.yo.R;

public class MainActivity extends AppCompatActivity {


    private Menu menu;
    private boolean isListView;

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private TravelListAdapter mAdapter;
    private Toolbar mToolbar;
    private RelativeLayout mLayoutInRecycleView;

    private static final String TAG = "MyFirebaseToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        startActivity(new Intent(this, NbnlIntroActivity.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //add Toolbar
        setupToolbar();

        //추가한 라인
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);


        isListView = true;
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);

        mAdapter = new TravelListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnScrollListener(onScrollListener);

        TravelListAdapter.OnItemClickListener onItemClickListener = new TravelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Toast.makeText(MainActivity.this, "Clicked " + position, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
//                intent.putExtra(DetailActivity.EXTRA_PARAM_ID,position);

                // 1
                ImageView placeImage = (ImageView) view.findViewById(R.id.placeImage);
                LinearLayout placeNameHolder = (LinearLayout) view.findViewById(R.id.placeNameHolder);
// 2
                Pair<View, String> imagePair = Pair.create((View) placeImage, "tImage");
                Pair<View, String> holderPair = Pair.create((View) placeNameHolder, "tNameHolder");
// 3
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
//                        imagePair, holderPair);
//                ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
            }
        };

        mAdapter.setOnItemClickListener(onItemClickListener);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    private void setupToolbar() {
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mLayoutInRecycleView = (RelativeLayout) findViewById(R.id.content_main_rl);
            //getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggle) {
            toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (isListView) {
            item.setIcon(R.drawable.ic_action_list);
            item.setTitle("Show as list");
            isListView = false;
            mStaggeredLayoutManager.setSpanCount(2);
        } else {
            item.setIcon(R.drawable.ic_action_grid);
            item.setTitle("Show as grid");
            isListView = true;
            mStaggeredLayoutManager.setSpanCount(1);
        }
    }


    //스크롤뷰
    public RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        private static final int HIDE_THRESHOLD = 20;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;

        RecyclerViewPositionHelper mRecyclerViewHelper;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);


            mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);

//            visibleItemCount = recyclerView.getChildCount();
//            totalItemCount = mRecyclerViewHelper.getItemCount();
            int firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();

            //show views if first item is first visible position and views are hidden
            if (firstVisibleItem == 0) {
                if (!controlsVisible) {
                    onShow();
                    controlsVisible = true;
                }
            } else {
                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    onHide();

                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    onShow();
                    controlsVisible = true;
                    scrolledDistance = 0;
                }
            }

            if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                scrolledDistance += dy;
            }
        }
    };

    private void onHide() {
        //mToolbar.animate().translationY(-mToolbar.getBottom()-100).setInterpolator(new AccelerateInterpolator(2)).start();

        mToolbar.animate()
                .translationY(-mToolbar.getBottom())
                .alpha(0)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator());






//        mToolbar.setVisibility(View.INVISIBLE);
//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
//        int fabBottomMargin = lp.bottomMargin;
//        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();


//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();
//
//        int fabBottomMargin = lp.bottomMargin;
//
//        mRecyclerView.animate().translationY(mToolbar.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void onShow() {
//        mToolbar.setVisibility(View.VISIBLE);
//        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

        mToolbar.animate()
                .translationY(0)
                .alpha(1)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator());



//        ViewGroup.MarginLayoutParams marginsParams = new ViewGroup.MarginLayoutParams(mToolbar.getLayoutParams());
//        marginsParams.setMargins(0,20,0,0);
//
//        mLayoutInRecycleView.setLayoutParams(marginsParams);

//        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)mLayoutInRecycleView.getLayoutParams();
//        relativeParams.setMargins(0, 80, 0, 0);  // left, top, right, bottom
//        mLayoutInRecycleView.setLayoutParams(relativeParams);





//        mRecyclerView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }
}
