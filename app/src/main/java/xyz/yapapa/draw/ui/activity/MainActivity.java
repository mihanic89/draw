package xyz.yapapa.draw.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yapapa.draw.R;
import xyz.yapapa.draw.manager.FileManager;
import xyz.yapapa.draw.manager.PermissionManager;
import xyz.yapapa.draw.ui.component.DrawingView;
import xyz.yapapa.draw.ui.component.SquareImageView;
import xyz.yapapa.draw.ui.dialog.StrokeSelectorDialog;

import static xyz.yapapa.draw.R.id.adView;


public class MainActivity extends AppCompatActivity implements IPickResult
{
	@Bind(R.id.main_drawing_view)
	DrawingView mDrawingView;
	@Bind(R.id.main_fill_iv)    SquareImageView mFillBackgroundImageView;
	@Bind(R.id.main_color_iv)   SquareImageView mColorImageView;
	@Bind(R.id.main_stroke_iv) 	SquareImageView mStrokeImageView;
	@Bind(R.id.main_undo_iv)    SquareImageView mUndoImageView;
	@Bind(R.id.main_redo_iv)    SquareImageView mRedoImageView;
    @Bind(R.id.share)           SquareImageView mShareImageView;
    @Bind(R.id.delete)          SquareImageView mDeleteImageView;
	@Bind(R.id.image)         	SquareImageView mImageImageView;

	@Bind(R.id.color1)         	SquareImageView mColor1;
	@Bind(R.id.color2)         	SquareImageView mColor2;
	@Bind(R.id.color3)         	SquareImageView mColor3;
	@Bind(R.id.color4)         	SquareImageView mColor4;
	@Bind(R.id.color5)         	SquareImageView mColor5;
	@Bind(R.id.color6)         	SquareImageView mColor6;
	@Bind(R.id.color7)         	SquareImageView mColor7;
	@Bind(R.id.color8)         	SquareImageView mColor8;
	@Bind(R.id.color9)         	SquareImageView mColor9;
	@Bind(R.id.color10)         SquareImageView mColor10;


	private int mCurrentBackgroundColor;
	private int mCurrentColor;
	private int mCurrentStroke;
    private AdView mAdView;
    private FirebaseAnalytics mFirebaseAnalytics;
	private static final int MAX_STROKE_WIDTH = 50;
	final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

	int[] intDrawables ;
    int i=0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAdView = (AdView) findViewById(adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
                .build();
        mAdView.loadAd(adRequest);

		ButterKnife.bind(this);

		initDrawingView();


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_share:
				requestPermissionsAndSaveBitmap();
				break;
			case R.id.action_clear:
				mDrawingView.clearCanvas();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void initDrawingView()
	{
		mCurrentBackgroundColor = ContextCompat.getColor(this, android.R.color.black);
		mCurrentColor = ContextCompat.getColor(this, android.R.color.black);
		mCurrentStroke = 10;

		mDrawingView.setPaintColor(mCurrentColor);
		mDrawingView.setPaintStrokeWidth(mCurrentStroke);

	}

	private void startFillBackgroundDialog()
	{
		int[] colors = getResources().getIntArray(R.array.palette);

		int size=2;

		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			size = 1;
		}
		else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			size = 1;
		}

		ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
				colors,
				mCurrentColor,
				4,
				size);//ColorPickerDialog.SIZE_LARGE);

		dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener()
		{

			@Override
			public void onColorSelected(int color)
			{
				mCurrentBackgroundColor = color;
				mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
			}

		});

		dialog.show(getFragmentManager(), "ColorPickerDialog");
	}

	private void startColorPickerDialog()
	{
		int[] colors = getResources().getIntArray(R.array.palette);
		int size=2;

		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			size = 1;
		}
		else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			size = 1;
		}

		ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
				colors,
				mCurrentColor,
				4,
				size);//ColorPickerDialog.SIZE_LARGE);

		dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener()
		{

			@Override
			public void onColorSelected(int color)
			{
				mCurrentColor = color;
				mDrawingView.setPaintColor(mCurrentColor);
			}

		});

		dialog.show(getFragmentManager(), "ColorPickerDialog");
	}

	private void startStrokeSelectorDialog()
	{
		StrokeSelectorDialog dialog = StrokeSelectorDialog.newInstance(mCurrentStroke, MAX_STROKE_WIDTH);

		dialog.setOnStrokeSelectedListener(new StrokeSelectorDialog.OnStrokeSelectedListener()
		{
			@Override
			public void onStrokeSelected(int stroke)
			{
				mCurrentStroke = stroke;
				mDrawingView.setPaintStrokeWidth(mCurrentStroke);
			}
		});

		dialog.show(getSupportFragmentManager(), "StrokeSelectorDialog");
	}

	private void startShareDialog(Uri uri)
	{
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("image/*");

		intent.putExtra(Intent.EXTRA_SUBJECT, "");
		intent.putExtra(Intent.EXTRA_TEXT, "");
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(intent, "Share Image"));
	}

	private void requestPermissionsAndSaveBitmap()
	{
		if (PermissionManager.checkWriteStoragePermissions(this))
		{
			Uri uri = FileManager.saveBitmap(mDrawingView.getBitmap());
			startShareDialog(uri);
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode)
		{
			case PermissionManager.REQUEST_WRITE_STORAGE:
			{
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					Uri uri = FileManager.saveBitmap(mDrawingView.getBitmap());
					startShareDialog(uri);
				} else
				{

					Toast.makeText(this, R.string.permission_read_write, Toast.LENGTH_LONG).show();
				}
			}
			break;





		}
	}


	protected void onImageViewClick() {
		PickSetup setup = new PickSetup()
				   .setTitle(getString(R.string.choose))
				//.setTitleColor(yourColor)
				//.setBackgroundColor(R.color.colorPrimary)
				  .setProgressText(getString(R.string.ok))
				//.setProgressTextColor(yourColor)
				  .setCancelText(getString(R.string.cancel))
				//.setCancelTextColor(yourColor)
				//.setButtonTextColor(yourColor)
				//.setDimAmount(yourFloat)
				//.setFlip(true)
				   .setMaxSize(400)
				//.setPickTypes(EPickTypes.GALLERY, EPickTypes.CAMERA)
			     .setCameraButtonText(getString(R.string.camera))
				 .setGalleryButtonText(getString(R.string.gallery))
				  //.setIconGravity(48)
				.setButtonOrientation(LinearLayout.HORIZONTAL)
				//.setSystemDialog(true)
				.setGalleryIcon(R.mipmap.gallery_colored)
				.setCameraIcon(R.mipmap.camera_colored);

		//super.customize(setup);

		PickImageDialog.build(setup).show(this);

		//If you don't have an Activity, you can set the FragmentManager
        /*PickImageDialog.build(setup, new IPickResult() {
            @Override
            public void onPickResult(PickResult r) {
                r.getBitmap();
                r.getError();
                r.getUri();
            }
        }).show(getSupportFragmentManager());*/

		//For overriding the click events you can do this
        /*PickImageDialog.build(setup).setOnClick(new IPickClick() {
            @Override
            public void onGalleryClick() {
            }
            @Override
            public void onCameraClick() {
            }
        }).show(this);*/
	}



	public void onPickResult(final PickResult r) {
		if (r.getError() == null) {
			//If you want the Uri.
			//Mandatory to refresh image from Uri.
			//getImageView().setImageURI(null);

			//Setting the real returned image.
			//getImageView().setImageURI(r.getUri());

			//If you want the Bitmap.

			//mDrawingView.DrawCustom(r.getBitmap());
			//mDrawingView.setBackground(new BitmapDrawable(getResources(),r.getBitmap()));

			//Toast.makeText(this, "w="+ r.getBitmap().getWidth() + " h="+ r.getBitmap().getHeight(), Toast.LENGTH_LONG).show();
			mDrawingView.SetCustomBitmap1(r.getBitmap());
			//r.getPath();
		} else {
			//Handle possible errors
			//TODO: do what you have to do with r.getError();
			Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
		}


	}


	@OnClick(R.id.main_fill_iv)
	public void onBackgroundFillOptionClick()
	{
		startFillBackgroundDialog();
	}

	@OnClick(R.id.image)
	public void onImageOptionClick()
	{

		onImageViewClick();
	}

	@OnClick(R.id.main_color_iv)
	public void onColorOptionClick()
	{
		startColorPickerDialog();
	}

	@OnClick(R.id.main_stroke_iv)
	public void onStrokeOptionClick()
	{
		startStrokeSelectorDialog();
	}

	@OnClick(R.id.main_undo_iv)
	public void onUndoOptionClick()
	{
		mDrawingView.undo();
	}

	@OnClick(R.id.share)
	public void onShareOptionClick()
	{
        requestPermissionsAndSaveBitmap();
	}

    @OnClick(R.id.delete)
    public void onDeleteOptionClick()
    {
        mDrawingView.clearCanvas();
    }

    @OnClick(R.id.main_redo_iv)
    public void onRedoOptionClick()
    {
        mDrawingView.redo();
    }




	@OnClick(R.id.color1)
	public void onColor1Click()
	{
		mCurrentColor = ContextCompat.getColor(this, R.color.color1);
		mDrawingView.setPaintColor(mCurrentColor);
	}

	@OnClick(R.id.color2)
	public void onColor2Click()
	{
		mCurrentColor = ContextCompat.getColor(this, R.color.color2);
		mDrawingView.setPaintColor(mCurrentColor);
	}

	@OnClick(R.id.color3)
	public void onColor3Click()
	{
		mCurrentColor = ContextCompat.getColor(this, R.color.color3);
		mDrawingView.setPaintColor(mCurrentColor);
	}

	@OnClick(R.id.color4)
	public void onColor4Click()
	{
		mCurrentColor = ContextCompat.getColor(this, R.color.color4);
		mDrawingView.setPaintColor(mCurrentColor);
	}

	@OnClick(R.id.color5)
	public void onColor5Click()
	{
		mCurrentColor = ContextCompat.getColor(this, R.color.color5);
		mDrawingView.setPaintColor(mCurrentColor);
	}

	@OnClick(R.id.color6)
	public void onColor6Click()
	{
		mCurrentColor = ContextCompat.getColor(this, R.color.color6);
		mDrawingView.setPaintColor(mCurrentColor);
	}

	@OnClick(R.id.color7)
	public void onColor7Click()
	{
		mCurrentColor = ContextCompat.getColor(this, R.color.color7);
		mDrawingView.setPaintColor(mCurrentColor);
	}

	@OnClick(R.id.color8)
	public void onColor8Click()
	{
		mCurrentColor = ContextCompat.getColor(this, R.color.color8);
		mDrawingView.setPaintColor(mCurrentColor);
	}

	@OnClick(R.id.color9)
	public void onColor9Click()
	{
		mCurrentColor = ContextCompat.getColor(this, R.color.color9);
		mDrawingView.setPaintColor(mCurrentColor);
	}

	@OnClick(R.id.color10)
	public void onColor10Click()
	{
		mCurrentColor = ContextCompat.getColor(this, R.color.color10);
		mDrawingView.setPaintColor(mCurrentColor);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Resume the AdView.
		mAdView.resume();
	}

	@Override
	public void onPause() {
		// Pause the AdView.
		mAdView.pause();

		super.onPause();
	}

	@Override
	public void onDestroy() {
		// Destroy the AdView.
		mAdView.destroy();

		super.onDestroy();
	}
}
