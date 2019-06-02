package com.sunmi.sunmit2demo.ui;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.extprinterservice.ExtPrinterService;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.sunmit2demo.BaseActivity;
import com.sunmi.sunmit2demo.BasePresentationHelper;
import com.sunmi.sunmit2demo.Constants;
import com.sunmi.sunmit2demo.MyApplication;
import com.sunmi.sunmit2demo.PreferenceUtil;
import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.adapter.GoodsSortAdapter;
import com.sunmi.sunmit2demo.adapter.HomeGoodsViewPagerAdapter;
import com.sunmi.sunmit2demo.adapter.HomeMenuAdapter;
import com.sunmi.sunmit2demo.bean.GoodsCode;
import com.sunmi.sunmit2demo.decoration.GoodsSortGridSpacingItemDecoration;
import com.sunmi.sunmit2demo.eventbus.GoodsItemClickEvent;
import com.sunmi.sunmit2demo.eventbus.PayCodeEvent;
import com.sunmi.sunmit2demo.eventbus.PrintDataEvent;
import com.sunmi.sunmit2demo.eventbus.UpdateUnLockUserEvent;
import com.sunmi.sunmit2demo.fragment.GoodsManagerFragment;
import com.sunmi.sunmit2demo.modle.ClassAndGoodsModle;
import com.sunmi.sunmit2demo.modle.GoodsInfo;
import com.sunmi.sunmit2demo.modle.GoodsSortTagModle;
import com.sunmi.sunmit2demo.modle.MenuItemModule;
import com.sunmi.sunmit2demo.modle.OrderInfo;
import com.sunmi.sunmit2demo.modle.PrinterModle;
import com.sunmi.sunmit2demo.present.TextDisplay;
import com.sunmi.sunmit2demo.present.VideoDisplay;
import com.sunmi.sunmit2demo.present.VideoMenuDisplay;
import com.sunmi.sunmit2demo.presenter.HomePresenter;
import com.sunmi.sunmit2demo.presenter.KPrinterPresenter;
import com.sunmi.sunmit2demo.presenter.PayMentPayPresenter;
import com.sunmi.sunmit2demo.presenter.PrinterPresenter;
import com.sunmi.sunmit2demo.presenter.contact.HomeClassAndGoodsContact;
import com.sunmi.sunmit2demo.print.DeviceConnFactoryManager;
import com.sunmi.sunmit2demo.unlock.UnlockServer;
import com.sunmi.sunmit2demo.utils.ResourcesUtils;
import com.sunmi.sunmit2demo.utils.ScreenManager;
import com.sunmi.sunmit2demo.utils.SharePreferenceUtil;
import com.sunmi.sunmit2demo.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static com.sunmi.sunmit2demo.print.DeviceConnFactoryManager.ACTION_QUERY_PRINTER_STATE;
import static com.sunmi.sunmit2demo.print.DeviceConnFactoryManager.CONN_STATE_FAILED;

public class NewMainActivity extends BaseActivity implements View.OnClickListener, HomeClassAndGoodsContact.View, HomeMenuAdapter.GoodsCountChangeListener {

    private final String TAG = "NewMainActivity";

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private RecyclerView mMenuRecyclerVeiew, mGoodsSortRecyclerView;
    private ViewPager mViewPager;
    private TextView mGoodsCountTv, mGoodsDiscountTv, mGoodsTotalPriceTv, mPayTv, mScanDataConfirmTv;
    private TextView mPrePageTv, mNextPageTv;
    private LinearLayout goodsDiscountLayout;

    private HomeMenuAdapter mMenuAdapter;
    private GoodsSortAdapter mGoodsSortAdapter;
    private HomeGoodsViewPagerAdapter mHomeGoodsViewPagerAdapter;

    //保存所有 商品信息
    private Map<String, GoodsInfo> allGoodsInfo;
    //保存所有 商品信息，结构跟allGoodsInfo不一样
    private List<ClassAndGoodsModle> classAndGoodsModles;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private VideoDisplay videoDisplay = null;
    private ScreenManager screenManager = ScreenManager.getInstance();
    private VideoMenuDisplay videoMenuDisplay = null;
    public TextDisplay textDisplay = null;
    private SunmiPrinterService woyouService = null;//商米标准打印 打印服务
    private ExtPrinterService extPrinterService = null;//k1 打印服务

    public static PrinterPresenter printerPresenter;
    public static KPrinterPresenter kPrinterPresenter;
    public UnlockServer.Proxy mProxy = null;
    private boolean willwelcome;

    public static boolean isK1 = false;
    public static boolean isVertical = false;
    SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);


    private PayMentPayPresenter payMentPayPresenter;

    private UsbManager usbManager;
    private	String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH
    };
    ArrayList<String> notAllowPermit = new ArrayList<>();
    private static final int	REQUEST_CODE = 0x004;
    private PendingIntent mPermissionIntent;

    private HomePresenter mPresenter;

    private int currPage;
    private int totalDatas;
    private final int DEFAULT_PAGE_SIZE = 6;

    private int id;
    private String usbName;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 屏幕宽度
        int height = dm.heightPixels;// 屏幕宽度
        Log.e("@@@", dm.densityDpi + "  " + dm.density);
        //Toast.makeText(this, dm.densityDpi + "  " + dm.density, Toast.LENGTH_LONG).show();
        isVertical = height > width;

        isK1 = MyApplication.getInstance().isHaveCamera() && isVertical;
        checkPermission();
        requestPermission();

        EventBus.getDefault().register(this);
        initView();
        initData();

//        if (isK1) {
//            connectKPrintService();
//        } else {
            connectPrintService();
//        }
    }


    //连接打印服务
    private void connectPrintService() {

        closeport();
        /* 获取USB设备名 */
        usbName = mPresenter.getUsbDeviceList();
        if (TextUtils.isEmpty(usbName)) {
            Utils.toast(this, "no usb device");
            return;
        }
        /* 通过USB设备名找到USB设备 */
        UsbDevice usbDevice = Utils.getUsbDeviceFromName( NewMainActivity.this, usbName );
        /* 判断USB设备是否有权限 */
        if (usbManager.hasPermission( usbDevice ) ) {
            usbConn(usbDevice);
        } else {        /* 请求权限 */
            mPermissionIntent = PendingIntent.getBroadcast( this, 0, new Intent( ACTION_USB_PERMISSION ), 0 );
            usbManager.requestPermission( usbDevice, mPermissionIntent );
        }

        try {
            InnerPrinterManager.getInstance().bindService(this,
                    innerPrinterCallback);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     * usb连接
     *
     * @param usbDevice
     */
    private void usbConn( UsbDevice usbDevice ) {
        new DeviceConnFactoryManager.Build()
                .setId(id)
                .setConnMethod( DeviceConnFactoryManager.CONN_METHOD.USB )
                .setUsbDevice( usbDevice )
                .setContext( this )
                .build();
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
    }

    /**
     * 重新连接回收上次连接的对象，避免内存泄漏
     */
    private void closeport()
    {
        if ( DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] != null &&DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort != null )
        {
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].reader.cancel();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort.closePort();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort = null;
        }
    }

    private InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
        @Override
        protected void onConnected(SunmiPrinterService service) {
            woyouService = service;
            printerPresenter = new PrinterPresenter(NewMainActivity.this, woyouService);

        }

        @Override
        protected void onDisconnected() {
            woyouService = null;

        }
    };

    //连接K1打印服务
    private void connectKPrintService() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.extprinterservice");
        intent.setAction("com.sunmi.extprinterservice.PrinterService");
        bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            extPrinterService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            extPrinterService = ExtPrinterService.Stub.asInterface(service);
            kPrinterPresenter = new KPrinterPresenter(NewMainActivity.this, extPrinterService);
        }
    };

    protected void onStop() {
        super.onStop();
        this.willwelcome = false;

        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBleService();

        if ((Boolean) SharePreferenceUtil.getParam(this, "BLE_KEY", false)) {
            if (this.willwelcome) {
                this.welcomeUserAnim();
            }

            this.willwelcome = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter( ACTION_USB_PERMISSION );
        filter.addAction( ACTION_USB_DEVICE_DETACHED );
        filter.addAction( ACTION_QUERY_PRINTER_STATE );
        filter.addAction( DeviceConnFactoryManager.ACTION_CONN_STATE );
        filter.addAction( ACTION_USB_DEVICE_ATTACHED );
        registerReceiver( receiver, filter );
    }


    private void initView() {
        mMenuRecyclerVeiew = findViewById(R.id.pay_menu_recyclerview);
        mGoodsSortRecyclerView = findViewById(R.id.goods_sort_recyclerview);
        mViewPager = findViewById(R.id.goods_view_pager);

        mGoodsCountTv = findViewById(R.id.tv_goods_count);
        mGoodsDiscountTv = findViewById(R.id.tv_goods_discount);
        mGoodsTotalPriceTv = findViewById(R.id.tv_goods_total_price);
        mPrePageTv = findViewById(R.id.tv_pre_page);
        mNextPageTv = findViewById(R.id.tv_next_page);
        mPayTv = findViewById(R.id.tv_pay);

        goodsDiscountLayout = findViewById(R.id.layout_goods_discount);
        goodsDiscountLayout.setOnClickListener(this);

        mPayTv.setOnClickListener(this);
        mPrePageTv.setOnClickListener(this);
        mNextPageTv.setOnClickListener(this);

        this.myHandler.postDelayed(new Runnable() {
            public void run() {
                NewMainActivity.this.welcomeUserAnim();
            }
        }, 1000L);

        findViewById(R.id.tv_has_selected).setOnClickListener(this);
    }

    private void checkPermission() {
        for ( String permission : permissions ) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission( this, permission ) ) {
                notAllowPermit.add(permission);
            }
        }
    }

    private void requestPermission() {
        if (notAllowPermit.size() > 0 ) {
            String[] p = new String[notAllowPermit.size()];
            ActivityCompat.requestPermissions( this, notAllowPermit.toArray( p ), REQUEST_CODE);
        }
    }

    private void initViewPager(List<ClassAndGoodsModle> datas) {
        if (datas == null || datas.size() == 0) return;

        mHomeGoodsViewPagerAdapter = new HomeGoodsViewPagerAdapter(getSupportFragmentManager(), datas);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mHomeGoodsViewPagerAdapter);
        mViewPager.setCurrentItem(0);
    }


    private void initData() {
        mPresenter = new HomePresenter(this, this);
        usbManager = (UsbManager) getSystemService( Context.USB_SERVICE);

        mMenuAdapter = new HomeMenuAdapter(new ArrayList<MenuItemModule>());
        mMenuAdapter.setChangeListener(this);
        mMenuRecyclerVeiew.setLayoutManager(new LinearLayoutManager(this));
        mMenuRecyclerVeiew.setAdapter(mMenuAdapter);

        mGoodsSortAdapter = new GoodsSortAdapter(new ArrayList<GoodsSortTagModle>());
        mGoodsSortAdapter.setOnItemClickListener(new GoodsSortAdapter.ItemClickListenr() {
            @Override
            public void onItemClick(long classId) {
                int pos = mHomeGoodsViewPagerAdapter.getPositionWithClassId(classId);
                if (pos != -1) {
                    mViewPager.setCurrentItem(pos);
                }
            }

        });
        mGoodsSortRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mGoodsSortRecyclerView.addItemDecoration(new GoodsSortGridSpacingItemDecoration(3, 10));
        mGoodsSortRecyclerView.setAdapter(mGoodsSortAdapter);

        screenManager.init(this);
        Display[] displays = screenManager.getDisplays();
        Log.e(TAG, "屏幕数量" + displays.length);
        for (int i = 0; i < displays.length; i++) {
            Log.e(TAG, "屏幕" + displays[i]);
        }
        Display display = screenManager.getPresentationDisplays();
        if (display != null && !isVertical) {
            videoDisplay = new VideoDisplay(this, display, Environment.getExternalStorageDirectory().getPath() + "/video_02.mp4");
            videoMenuDisplay = new VideoMenuDisplay(this, display, Environment.getExternalStorageDirectory().getPath() + "/video_02.mp4");
            textDisplay = new TextDisplay(this, display);
        }

        payMentPayPresenter = new PayMentPayPresenter(this);

        soundPool.load(NewMainActivity.this, R.raw.audio, 1);// 1
        soundPool.load(NewMainActivity.this, isZh(this) ? R.raw.alipay : R.raw.alipay_en, 1);// 2
        mPresenter.load();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pay:
                if (mPresenter != null) {
                    mPresenter.pay(mMenuAdapter.getDatas(), (int) mMenuAdapter.getGoodsTotalPrice());
                }
                break;
            case R.id.tv_pre_page:
                resetViewData(currPage - 1);
                break;
            case R.id.tv_next_page:
                resetViewData(currPage + 1);
                break;

            case R.id.layout_goods_discount:

                break;
            case R.id.tv_has_selected:
                try {
                    connectPrintService();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private StringBuilder sb = new StringBuilder();
    private Handler myHandler = new Handler();

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        switch (action) {
            case KeyEvent.ACTION_DOWN:
                int unicodeChar = event.getUnicodeChar();
                sb.append((char) unicodeChar);
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
                    return super.dispatchKeyEvent(event);
                }
                final int len = sb.length();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (len != sb.length()) return;
                        Log.e(TAG, "isQRcode");
                        if (sb.length() > 0) {
                            addGoods(sb.toString());
                            sb.setLength(0);
                        }
                    }
                }, 200);
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private void addGoods(String code) {
        code = code.replaceAll("[^0-9a-zA-Z]", "");
        Log.e(TAG, "扫码===" + code + "   " + GoodsCode.getInstance().getGood().containsKey(code));
//        inputEt.setText(sb.toString());

        GoodsInfo goodsInfo = allGoodsInfo.get(code);
        if (goodsInfo == null) {
            String[] datas = Util.getGoodsPluCodeAndPrice(code);
            if (!TextUtils.isEmpty(code)
                    && !TextUtils.isEmpty(PreferenceUtil.getString(this,PreferenceUtil.KEY.PAYING_TYPE, ""))) {
                //在支付中。。。
                EventBus.getDefault().post(new PayCodeEvent(code));
            } else if (datas != null) {
                //称重物品
                String plu = datas[0];
                GoodsInfo pluGoodsInfo = allGoodsInfo.get(plu);
                if (pluGoodsInfo == null) {
                    Toast.makeText(NewMainActivity.this, "未找到符合的商品", Toast.LENGTH_SHORT).show();
                    return;
                }
                int price = pluGoodsInfo.getPrice();
                int weight = 0;
                try {
                    price = Integer.parseInt(datas[1]);
                    weight = Integer.parseInt(datas[2]);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                goodsItemClickEvent(new GoodsItemClickEvent(
                        pluGoodsInfo.getGoodsName(),
                        pluGoodsInfo.getPrice(),
                        "/"+pluGoodsInfo.getWeight() + pluGoodsInfo.getUnit(),
                        String.valueOf(pluGoodsInfo.getGoodsCode()),
                        pluGoodsInfo.getPriceType(),
                        weight,
                        price));
            } else {
                Toast.makeText(NewMainActivity.this, "未找到符合的商品", Toast.LENGTH_SHORT).show();
            }
        } else {
            goodsItemClickEvent(new GoodsItemClickEvent(goodsInfo.getGoodsName(), goodsInfo.getPrice(), goodsInfo.getUnit(), goodsInfo.getGoodsCode(), goodsInfo.getPriceType()));
        }
    }

    //待机
    private void standByTime() {
        if (videoDisplay != null && !videoDisplay.isShow) {
            videoDisplay.show();
        }
    }


    private void playSound(final int payMode) {
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundPool.play(1, 1, 1, 10, 0, 1);
                if (payMode == 2) {
                    soundPool.play(2, 1, 1, 10, 0, 1);
                }
            }
        }, 200);
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UpdateUnLockUserEvent var1) {
        this.willwelcome = true;
//        this.tv_user_lock.setAlpha(0.0F);
//        this.tv_user_lock.setText(Utils.getPmOrAm() + var1.getName());
//        if (var1.isShowAnim()) {
//            this.tv_user_lock.post(new Runnable() {
//                public void run() {
//                    NewMainActivity.this.welcomeUserAnim();
//                }
//            });
//        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void printDataEvent(PrintDataEvent event) {
        if (event.openCashBox) {
            mPresenter.openCashBox(myHandler, id);
            return;
        }
        if (mMenuAdapter == null
                || mMenuAdapter.getDatas() == null
                || mMenuAdapter.getDatas().size() == 0) {
            return;
        }
        List<MenuItemModule> modules = new ArrayList<>();
        modules.addAll(mMenuAdapter.getDatas());

        mMenuAdapter.clear();
        //更新所有清单总价格
        mGoodsCountTv.setText("0");
        mGoodsTotalPriceTv.setText("0");

        String discountPrice = null;
        String totalPrice = null;
        try {
            discountPrice = mGoodsDiscountTv.getText().toString();
            totalPrice = mGoodsTotalPriceTv.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        mPresenter.printReceipt(myHandler, id,
                new PrinterModle(modules,
                        event.orderNo,
                        event.time,
                        Utils.parseFloat((TextUtils.isEmpty(totalPrice) ? "":totalPrice) + (TextUtils.isEmpty(discountPrice) ? "":discountPrice)),
                        Utils.parseFloat((TextUtils.isEmpty(discountPrice) ? "":discountPrice)),
                        Utils.parseFloat((TextUtils.isEmpty(totalPrice) ? "":totalPrice)),
                        event.payType));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goodsItemClickEvent(GoodsItemClickEvent event) {
        //加入购物清单
        MenuItemModule menuItemModule = new MenuItemModule();
        menuItemModule.setGoodsName(event.goodsName);
        menuItemModule.setPrice(event.price);
        menuItemModule.setUnit(event.unit);
        menuItemModule.setGoodsCount(1);
        menuItemModule.setGoodsCode(event.goodsCode);
        menuItemModule.setPriceType(event.priceType);

        mMenuAdapter.addGoodsCount(1);
        mMenuAdapter.addGoodsTotalPrice(event.priceType == Constants.WEIGHT_PRICE_TYPE ? event.totalPrice : event.price);

        List<MenuItemModule> modules = mMenuAdapter.getDatas();
        if (event.priceType == Constants.WEIGHT_PRICE_TYPE) {
            //称重类型商品直接添加
            menuItemModule.setGoodsCount(event.weight);

            modules.add(0, menuItemModule);
            mMenuAdapter.notifyDataSetChanged();
        } else if (modules.size() > 0) {
            int size = modules.size();
            boolean hasThisGood = false;
            for (int i = 0; i < size; i++) {
                MenuItemModule module = modules.get(i);
                if (menuItemModule.getPrice() == module.getPrice()
                    && !TextUtils.isEmpty(menuItemModule.getGoodsName())
                    && menuItemModule.getGoodsName().equals(module.getGoodsName())) {
                    //价格相等且货物名称相同,说明原有清单列表已有该货物，将该货物的count+1
                    module.setGoodsCount(module.getGoodsCount()+1);
                    mMenuAdapter.notifyItemChanged(i);
                    hasThisGood = true;
                    break;
                }
            }
            if (!hasThisGood) {
                modules.add(0, menuItemModule);
                mMenuAdapter.notifyDataSetChanged();
            }
        } else {
            modules.add(0, menuItemModule);
            mMenuAdapter.notifyDataSetChanged();
        }
        //更新所有清单总价格
        mGoodsCountTv.setText(String.valueOf(mMenuAdapter.getGoodsCount()));
        mGoodsTotalPriceTv.setText(Utils.numberFormat(mMenuAdapter.getGoodsTotalPrice() / 100));
    }


    private void welcomeUserAnim() {
//        this.tv_user_lock.setAlpha(1.0F);
//        int var1 = this.tv_user_lock.getMeasuredWidth() + 17;
//        AnimatorSet var2 = new AnimatorSet();
//        ObjectAnimator var3 = ObjectAnimator.ofFloat(this.tv_user_lock, "translationX", new float[]{(float) var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, (float) var1, (float) var1, 0.0F});
//        var3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator var1) {
//                if (var1.getCurrentPlayTime() > 4200L && NewMainActivity.this.tv_user_lock.getText().toString().contains(Utils.getPmOrAm())) {
//                    NewMainActivity.this.tv_user_lock.setText(NewMainActivity.this.tv_user_lock.getText().toString().replace(Utils.getPmOrAm(), ""));
//                }
//
//            }
//        });
//        var2.setDuration(5000L);
//        var2.setInterpolator(new LinearInterpolator());
//        var2.play(var3);
//        var2.start();
    }

    private void initBleService() {
        boolean isOpen = (Boolean) SharePreferenceUtil.getParam(this, "BLE_KEY", false);
        if (isOpen && mProxy == null) {
            this.bindService(new Intent(this, UnlockServer.class), this.mServiceConnection, Service.BIND_AUTO_CREATE);
        }
        if (mProxy != null) {
            if (isOpen) {
                mProxy.updateAllUser();
            } else {
                mProxy.close();
            }
        }
    }


    @Override
    protected void onDestroy() {
        soundPool.release();
        if (extPrinterService != null) {
            unbindService(connService);
        }
        if (woyouService != null) {
            try {
                InnerPrinterManager.getInstance().unBindService(this,
                        innerPrinterCallback);
            } catch (InnerPrinterException e) {
                e.printStackTrace();
            }
        }
        if (mProxy != null) {
            unbindService(mServiceConnection);
        }

        if (payMentPayPresenter != null) {
            payMentPayPresenter.destoryReceiver();
        }


        printerPresenter = null;
        kPrinterPresenter = null;
        EventBus.getDefault().unregister(this);
        BasePresentationHelper.getInstance().dismissAll();
        super.onDestroy();
    }

    //退出时的时间
    private long mExitTime;

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(NewMainActivity.this, ResourcesUtils.getString(this, R.string.tips_exit), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName var1, IBinder var2) {
            NewMainActivity.this.mProxy = (UnlockServer.Proxy) var2;
        }

        public void onServiceDisconnected(ComponentName var1) {
        }
    };

    @Override
    public void loadComplete(List<ClassAndGoodsModle> datas) {
        if (datas != null && datas.size() > 0) {
            //保存所有商品信息
            if (allGoodsInfo == null) {
                allGoodsInfo = new HashMap<>();
            } else {
                allGoodsInfo.clear();
            }
            for (int i = 0; i < datas.size(); i++) {
                List<GoodsInfo> goodsList = datas.get(i).getGoodsList();
                for (int j = 0; j < goodsList.size(); j++) {
                    GoodsInfo goodsInfo = goodsList.get(j);
                    if (goodsInfo.getPriceType() == 1) {
                        allGoodsInfo.put(goodsInfo.getGoodsCode(), goodsInfo);
                    } else {
                        //称重商品已plu码为key
                        allGoodsInfo.put(String.valueOf(goodsInfo.getPlu()), goodsInfo);
                    }
                }
            }

            if (classAndGoodsModles == null) {
                classAndGoodsModles = new ArrayList<>();
            } else {
                classAndGoodsModles.clear();
            }

            for (int i = 0; i < datas.size(); i++) {
                ClassAndGoodsModle classAndGoodsModle = datas.get(i);
                if (classAndGoodsModle != null && classAndGoodsModle.getGoodsList() != null && classAndGoodsModle.getGoodsList().size() > 0) {
                    classAndGoodsModles.add(classAndGoodsModle);
                }
            }

            currPage = 0;
            totalDatas = classAndGoodsModles.size();
            resetViewData(0);
        }
    }

    //选择 商品分类的分页数据
    private void resetViewData(int page) {
        if (totalDatas <= 0 || page * DEFAULT_PAGE_SIZE >= totalDatas) {
            //没有该页对应的数据
            Toast.makeText(this, "没有更多数据了", Toast.LENGTH_SHORT).show();
            currPage = page - 1;
            return;
        } else {
            currPage = page;

        }

        if (page < 0) {
            this.currPage = 0;
            Toast.makeText(this, "已经是第一页了", Toast.LENGTH_SHORT).show();
            return;
        } else {
            currPage = page;
        }

        List<GoodsSortTagModle> tags = mGoodsSortAdapter.getDatas();
        tags.clear();

        int fromIndex = page * DEFAULT_PAGE_SIZE;
        int toIndex = (page + 1) * DEFAULT_PAGE_SIZE > totalDatas  ? totalDatas : (page + 1) * DEFAULT_PAGE_SIZE;
        List<ClassAndGoodsModle> modles = classAndGoodsModles.subList(fromIndex, toIndex);
        for (int i = 0; i < modles.size(); i++) {
            ClassAndGoodsModle modle = modles.get(i);
            if (modle != null && !TextUtils.isEmpty(modle.getClassName())) {
                tags.add(new GoodsSortTagModle(modle.getClassId(), modle.getClassName()));
            }
        }
        mGoodsSortAdapter.notifyDataSetChanged();

        initViewPager(modles);
    }

    @Override
    public void orderCreateComplete(OrderInfo orderInfo) {
        if (orderInfo != null) {
            Intent intent = new Intent(this, ChoosePayWayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(PayingActivity.ORDER_RESULT, orderInfo);
            bundle.putString(PayingActivity.GOODS_COUNT, String.valueOf(mMenuAdapter.getGoodsCount()));
            bundle.putFloat(PayingActivity.GOODS_ORIGINAL_PRICE, mMenuAdapter.getGoodsTotalPrice());
//            bundle.putString(PayingActivity.GOODS_AUTHO_DATA, authoData);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Toast.makeText(this, "订单生成失败，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void change(int type, float money) {
        if (mMenuAdapter == null) return;
        mGoodsCountTv.setText(Utils.numberFormat(mMenuAdapter.getGoodsCount()));
        mGoodsTotalPriceTv.setText(Utils.numberFormat(mMenuAdapter.getGoodsTotalPrice() / 100));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive( Context context, Intent intent )
        {
            String action = intent.getAction();
            switch ( action )
            {
                case ACTION_USB_PERMISSION:
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra( UsbManager.EXTRA_DEVICE );
                        if ( intent.getBooleanExtra( UsbManager.EXTRA_PERMISSION_GRANTED, false ) )
                        {
                            if ( device != null )
                            {
                                System.out.println( "permission ok for device " + device );
                                usbConn( device );
                            }
                        } else {
                            System.out.println( "permission denied for device " + device );
                        }
                    }
                    break;
                /* Usb连接断开、蓝牙连接断开广播 */
                case ACTION_USB_DEVICE_DETACHED:
//                    mHandler.obtainMessage( CONN_STATE_DISCONN ).sendToTarget();
                    break;
                case DeviceConnFactoryManager.ACTION_CONN_STATE:
                    int state = intent.getIntExtra( DeviceConnFactoryManager.STATE, -1 );
                    int deviceId = intent.getIntExtra( DeviceConnFactoryManager.DEVICE_ID, -1 );
                    switch ( state )
                    {
                        case DeviceConnFactoryManager.CONN_STATE_DISCONNECT:
                            if ( id == deviceId ) {

                            }
                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTING:

                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTED:

                            break;
                        case CONN_STATE_FAILED:
                            break;
                        default:
                            break;
                    }
                    break;
                case ACTION_QUERY_PRINTER_STATE:

                    break;
                default:
                    break;
            }
        }
    };
}
