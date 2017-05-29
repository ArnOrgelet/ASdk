package sdk.addeals.ahead_solutions.adsdk.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import sdk.addeals.ahead_solutions.adsdk.BR;
import sdk.addeals.ahead_solutions.adsdk.EventModels.EventManager;
import sdk.addeals.ahead_solutions.adsdk.EventModels.Observable;
import sdk.addeals.ahead_solutions.adsdk.EventModels.ViewPropertyChangeListener;
import sdk.addeals.ahead_solutions.adsdk.R;
import sdk.addeals.ahead_solutions.adsdk.ViewModels.ViewModelBase;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by ArnOr on 09/05/2017.
 */

public class EventHandingView<V extends ViewModelBase> extends View {
    protected int id;
    V viewModel;
    Context _context;
    EventManager eventManager = new EventManager(new Observable<>());
    public EventHandingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public EventHandingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public EventHandingView(Context context) {
        super(context);
        _context = context;
    }

    private void setPropertyChangeListener(){
        viewModel.addOnPropertyChangedCallback( //.addPropertyChangeListener(
            new android.databinding.Observable.OnPropertyChangedCallback(){//ViewPropertyChangeListener(this) {

            @Override
            public void onPropertyChanged(android.databinding.Observable observable, int i) {

            }//ViewPropertyChangeListener(this) {
            /*
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                Object val = propertyChangeEvent.getNewValue();
                int R_id = -1;
                switch(propertyChangeEvent.getPropertyName()){
                    case "ImpressionPixelSrc":
                        R_id = getResources().getIdentifier((String)val, "drawable", _context.getPackageName());
                        if(R_id > -1) {
                            Drawable d = ContextCompat.getDrawable(_context, R_id);
                            ((ImageButton) this.view).setImageResource(R_id);
                        }
                        break;
                    case "WebViewVisibility":
                        switch((int)val){
                            case View.VISIBLE:
                                ((WebView)this.view).setVisibility(View.VISIBLE);
                                break;
                            case View.GONE:
                                ((WebView)this.view).setVisibility(View.GONE);
                                break;
                            case View.INVISIBLE:
                                ((WebView)this.view).setVisibility(View.INVISIBLE);
                                break;
                        }
                        break;
                    case "HTMLFBTag":
                        break;
                    case "CampaignLinkURL":
                        if(view instanceof AdDealsBannerAd)
                            //((WebView)this.view).loadUrl((String)val);
                        break;
                    case "ProgressRingMargin":
                        break;
                    case "ClosingButton":
                        R_id = getResources().getIdentifier((String)val, "drawable", _context.getPackageName());
                        if(R_id > -1) {
                            Drawable d = ContextCompat.getDrawable(_context, R_id);
                            ((ImageButton) this.view).setImageResource(R_id);
                        }
                        break;
                    case "AppHeight":
                        setHeight((int)val);
                        break;
                    case "AppWidth":
                        setWidth((int)val);
                        break;
                    case "WebViewAdSrc":
                        ((WebView)this.view).loadUrl((String)val);
                        break;
                }
            }*/
        });
    }

    public void setWidth(int width){
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.width = width;
        this.setLayoutParams(params);
    }/*
    public int getWidth(){
        ViewGroup.LayoutParams params = this.getLayoutParams();
        return params.width;
    }*/
    public void setHeight(int height){
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = height;
        this.setLayoutParams(params);
    }/*
    public int getHeight(){
        ViewGroup.LayoutParams params = this.getLayoutParams();
        return (int)params.height;
    }*/
}
