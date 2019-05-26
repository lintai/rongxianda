package com.sunmi.sunmit2demo.ui;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.sunmi.sunmit2demo.adapter.GoodsAdapter;
import com.sunmi.sunmit2demo.adapter.GoodsSortAdapter;
import com.sunmi.sunmit2demo.adapter.HomeGoodsViewPagerAdapter;
import com.sunmi.sunmit2demo.adapter.HomeMenuAdapter;
import com.sunmi.sunmit2demo.bean.GoodsCode;
import com.sunmi.sunmit2demo.bean.GvBeans;
import com.sunmi.sunmit2demo.bean.MenusBean;
import com.sunmi.sunmit2demo.decoration.GoodsSortGridSpacingItemDecoration;
import com.sunmi.sunmit2demo.dialog.PayDialog;
import com.sunmi.sunmit2demo.eventbus.GoodsItemClickEvent;
import com.sunmi.sunmit2demo.eventbus.PayCodeEvent;
import com.sunmi.sunmit2demo.eventbus.UpdateUnLockUserEvent;
import com.sunmi.sunmit2demo.fragment.GoodsManagerFragment;
import com.sunmi.sunmit2demo.modle.ClassAndGoodsModle;
import com.sunmi.sunmit2demo.modle.GoodsInfo;
import com.sunmi.sunmit2demo.modle.GoodsSortTagModle;
import com.sunmi.sunmit2demo.modle.MenuItemModule;
import com.sunmi.sunmit2demo.modle.OrderInfo;
import com.sunmi.sunmit2demo.present.TextDisplay;
import com.sunmi.sunmit2demo.present.VideoDisplay;
import com.sunmi.sunmit2demo.present.VideoMenuDisplay;
import com.sunmi.sunmit2demo.presenter.HomePresenter;
import com.sunmi.sunmit2demo.presenter.KPrinterPresenter;
import com.sunmi.sunmit2demo.presenter.PayMentPayPresenter;
import com.sunmi.sunmit2demo.presenter.PrinterPresenter;
import com.sunmi.sunmit2demo.presenter.ScalePresenter;
import com.sunmi.sunmit2demo.presenter.contact.HomeClassAndGoodsContact;
import com.sunmi.sunmit2demo.unlock.UnlockServer;
import com.sunmi.sunmit2demo.utils.ResourcesUtils;
import com.sunmi.sunmit2demo.utils.ScreenManager;
import com.sunmi.sunmit2demo.utils.SharePreferenceUtil;
import com.sunmi.sunmit2demo.view.CustomPopWindow;
import com.sunmi.sunmit2demo.view.Input2Dialog;
import com.sunmi.widget.dialog.InputDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewMainActivity extends BaseActivity implements View.OnClickListener, HomeClassAndGoodsContact.View, HomeMenuAdapter.GoodsCountChangeListener {

    private final String TAG = "NewMainActivity";
//    private TextView tv_user_lock;
    private RecyclerView mMenuRecyclerVeiew, mGoodsSortRecyclerView;
    private ViewPager mViewPager;
    private TextView mGoodsCountTv, mGoodsDiscountTv, mGoodsTotalPriceTv, mPayTv, mScanDataConfirmTv;
    private TextView mPrePageTv, mNextPageTv;

//    private EditText inputEt;

    private HomeMenuAdapter mMenuAdapter;
    private GoodsSortAdapter mGoodsSortAdapter;
    private HomeGoodsViewPagerAdapter mHomeGoodsViewPagerAdapter;

    private GoodsAdapter drinkAdapter;
    private GoodsAdapter fruitAdapter;
    private GoodsAdapter snackAdapter;
    private GoodsAdapter vegetableAdapter;
    private GoodsAdapter othersAdapter;

    //保存所有 商品信息
    private Map<String, GoodsInfo> allGoodsInfo;
    //保存所有 商品信息，结构跟allGoodsInfo不一样
    private List<ClassAndGoodsModle> classAndGoodsModles;

    private List<GvBeans> mDrinksBean;
    private List<GvBeans> mFruitsBean;
    private List<GvBeans> mSnacksBean;
    private List<GvBeans> mVegetablesBean;
    private List<GvBeans> mOthers;


    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private VideoDisplay videoDisplay = null;
    private ScreenManager screenManager = ScreenManager.getInstance();
    private VideoMenuDisplay videoMenuDisplay = null;
    public TextDisplay textDisplay = null;
    private PayDialog payDialog;
    private SunmiPrinterService woyouService = null;//商米标准打印 打印服务
    private ExtPrinterService extPrinterService = null;//k1 打印服务

    private String goods_data;
    public static PrinterPresenter printerPresenter;
    public static KPrinterPresenter kPrinterPresenter;
    public UnlockServer.Proxy mProxy = null;
    CustomPopWindow popWindow;
    private boolean willwelcome;

    public static boolean isK1 = false;
    public static boolean isVertical = false;
    SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);


    private PayMentPayPresenter payMentPayPresenter;
    private ScalePresenter scalePresenter;
    Input2Dialog mInputDialog;

    private HomePresenter mPresenter;

    //测试代码
    private String authoData;

    private int currPage;
    private int totalDatas;
    private final int DEFAULT_PAGE_SIZE = 6;


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

        if (isK1) {
            connectKPrintService();
        } else {
            connectPrintService();
        }
        EventBus.getDefault().register(this);
        initView();
        initData();
        initAction();

    }


    //连接打印服务
    private void connectPrintService() {

        try {
            InnerPrinterManager.getInstance().bindService(this,
                    innerPrinterCallback);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
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

        int goodsMode = (int) SharePreferenceUtil.getParam(this, GoodsManagerFragment.GOODSMODE_KEY, GoodsManagerFragment.defaultGoodsMode);
        switch (goodsMode) {
            case 0:

                break;
            case GoodsManagerFragment.Goods_1 | GoodsManagerFragment.Goods_2:

                break;
            case GoodsManagerFragment.Goods_3 | GoodsManagerFragment.Goods_4:

                break;
            case GoodsManagerFragment.Goods_1 | GoodsManagerFragment.Goods_2 + GoodsManagerFragment.Goods_3 | GoodsManagerFragment.Goods_4:

                break;
        }
        othersAdapter.notifyDataSetChanged();
        drinkAdapter.notifyDataSetChanged();
        fruitAdapter.notifyDataSetChanged();
        snackAdapter.notifyDataSetChanged();
        vegetableAdapter.notifyDataSetChanged();

        int payMode = (int) SharePreferenceUtil.getParam(this, PayDialog.PAY_MODE_KEY, 7);
        switch (payMode) {
            case PayDialog.PAY_FACE:
                break;
            case PayDialog.PAY_FACE | PayDialog.PAY_CODE | PayDialog.PAY_CASH:
                break;
        }
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
//        mScanDataConfirmTv = findViewById(R.id.tv_scan_data_confirm);
//
//        inputEt = findViewById(R.id.et_scan_data);
//        inputEt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        mPayTv.setOnClickListener(this);
        mPrePageTv.setOnClickListener(this);
        mNextPageTv.setOnClickListener(this);
//        mScanDataConfirmTv.setOnClickListener(this);

        this.myHandler.postDelayed(new Runnable() {
            public void run() {
                NewMainActivity.this.welcomeUserAnim();
            }
        }, 1000L);
    }


    private void initAction() {
        scalePresenter = new ScalePresenter(this, new ScalePresenter.ScalePresenterCallback() {
            @Override
            public void getData(final int net, final int pnet, final int statu) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateScaleInfo(net, pnet, statu);
                    }
                });
            }

            @Override
            public void isScaleCanUse(boolean isCan) {
            }
        });

        othersAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

        });
        drinkAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Log.e("@@@@", "code==" + mDrinksBean.get(position).getCode());
            }

        });
        snackAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

        });
        vegetableAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                checkScaleGoods(position, 0);
            }

            @Override
            public void onItemCarClick(View view, int position) {
                super.onItemCarClick(view, position);
                addGoodsByScale(scalePresenter.formatTotalMoney(), scalePresenter.getGvBeans());
            }
        });
        fruitAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                checkScaleGoods(position, 1);
            }

            @Override
            public void onItemCarClick(View view, int position) {
                super.onItemCarClick(view, position);
                addGoodsByScale(scalePresenter.formatTotalMoney(), scalePresenter.getGvBeans());
            }
        });

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
        mDrinksBean = GoodsCode.getInstance().getDrinks();
        mFruitsBean = GoodsCode.getInstance().getFruits();
        mSnacksBean = GoodsCode.getInstance().getSnacks();
        mVegetablesBean = GoodsCode.getInstance().getVegetables();
        mOthers = GoodsCode.getInstance().getOthers();

        drinkAdapter = new GoodsAdapter(mDrinksBean, 1);
        fruitAdapter = new GoodsAdapter(mFruitsBean, 2);
        snackAdapter = new GoodsAdapter(mSnacksBean, 3);
        vegetableAdapter = new GoodsAdapter(mVegetablesBean, 2);
        othersAdapter = new GoodsAdapter(mOthers, 0);

        payDialog = new PayDialog();
        payMentPayPresenter = new PayMentPayPresenter(this);

        payDialog.setPayMentPayPresenter(payMentPayPresenter);

        payDialog.setCompleteListener(new PayDialog.OnCompleteListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSuccess(final int payMode) {
                playSound(payMode);
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        paySuccessToPrinter(payMode);
                        payDialog.setPhoneNumber("");

                    }
                }, 1000);

            }

            @Override
            public void onComplete(int payMode) {
                payCompleteToReMenu();
            }
        });

        soundPool.load(NewMainActivity.this, R.raw.audio, 1);// 1
        soundPool.load(NewMainActivity.this, isZh(this) ? R.raw.alipay : R.raw.alipay_en, 1);// 2
        mPresenter.load();
    }


    private void payCompleteToReMenu() {
        if (!isVertical) {


            standByTime();

        } else {

        }
    }


    private void paySuccessToPrinter(int payMode) {
        if (isK1) {
            if (kPrinterPresenter != null) {
                kPrinterPresenter.print(goods_data, payMode);
            }
        } else {

            if (printerPresenter != null) {
                printerPresenter.print(goods_data, payMode);
            }
        }
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
//            case R.id.tv_scan_data_confirm:
//                addGoods("2002794016551");
//                if (inputEt == null && TextUtils.isEmpty(inputEt.getText())) {
//                    Toast.makeText(this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
//                }
//                authoData = inputEt.getText().toString();
//                if (mMenuAdapter.getDatas().size() == 0) {
//                    Toast.makeText(this, "请添加商品", Toast.LENGTH_SHORT).show();
//                }
//                System.out.print("");
//                if (mPresenter != null) {
//                    mPresenter.pay(mMenuAdapter.getDatas(), (int) mMenuAdapter.getGoodsTotalPrice());
//                }
//                break;

            default:
                break;
        }
    }

    private void buildMenuJson(List<MenusBean> menus, String price) {
        try {
            JSONObject data = new JSONObject();
            data.put("title", "Sunmi " + ResourcesUtils.getString(this, R.string.menus_title));
            JSONObject head = new JSONObject();
            head.put("param1", ResourcesUtils.getString(this, R.string.menus_number));
            head.put("param2", ResourcesUtils.getString(this, R.string.menus_goods_name));
            head.put("param3", ResourcesUtils.getString(this, R.string.menus_unit_price));
            data.put("head", head);
            data.put("flag", "true");
            JSONArray list = new JSONArray();
            for (int i = 0; i < menus.size(); i++) {
                JSONObject listItem = new JSONObject();
                listItem.put("param1", "" + (i + 1));
                listItem.put("param2", menus.get(i).getName());
                listItem.put("param3", menus.get(i).getMoney());
                listItem.put("type", menus.get(i).getType());
                listItem.put("code", menus.get(i).getCode());
                listItem.put("net", menus.get(i).getNet());
                list.put(listItem);
            }
            data.put("list", list);
            JSONArray KVPList = new JSONArray();
            JSONObject KVPListOne = new JSONObject();
            KVPListOne.put("name", ResourcesUtils.getString(this, R.string.shop_car_total) + " ");
            KVPListOne.put("value", price);
            JSONObject KVPListTwo = new JSONObject();
            KVPListTwo.put("name", ResourcesUtils.getString(this, R.string.shop_car_offer) + " ");
            KVPListTwo.put("value", "0.00");
            JSONObject KVPListThree = new JSONObject();
            KVPListThree.put("name", ResourcesUtils.getString(this, R.string.shop_car_number) + " ");
            KVPListThree.put("value", "" + menus.size());
            JSONObject KVPListFour = new JSONObject();
            KVPListFour.put("name", ResourcesUtils.getString(this, R.string.shop_car_receivable) + " ");
            KVPListFour.put("value", price);
            KVPList.put(0, KVPListOne);
            KVPList.put(1, KVPListTwo);
            KVPList.put(2, KVPListThree);
            KVPList.put(3, KVPListFour);
            data.put("KVPList", KVPList);
            Log.d("HHHH", "onClick: ---------->" + data.toString());
            goods_data = data.toString();
            Log.d(TAG, "buildMenuJson: ------->" + (videoMenuDisplay != null));
            if (payDialog.isVisible()) {
                return;
            }
            if (videoMenuDisplay != null && !videoMenuDisplay.isShow) {
                videoMenuDisplay.show();
                videoMenuDisplay.update(menus, data.toString());
            } else if (null != videoMenuDisplay) {
                videoMenuDisplay.update(menus, data.toString());
            }
            // 购物车有东西


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StringBuilder sb = new StringBuilder();
    private Handler myHandler = new Handler();

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        Log.e("action",action + "");
        printerPresenter.print(goods_data, 0);
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
                            if (payDialog.isVisible()) {
                                Log.e(TAG, "支付中");
                            } else {
                                addGoods(sb.toString());
                            }
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
                try {
                    price = Integer.parseInt(datas[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                goodsItemClickEvent(new GoodsItemClickEvent(pluGoodsInfo.getGoodsName(), price, pluGoodsInfo.getUnit(), String.valueOf(pluGoodsInfo.getPlu()), pluGoodsInfo.getPriceType()));
            } else {
                Toast.makeText(NewMainActivity.this, "未找到符合的商品", Toast.LENGTH_SHORT).show();
            }
        } else {
            goodsItemClickEvent(new GoodsItemClickEvent(goodsInfo.getGoodsName(), goodsInfo.getPrice(), goodsInfo.getUnit(), goodsInfo.getGoodsCode(), goodsInfo.getPriceType()));
        }
    }

    private void checkScaleGoods(int position, int type) {
        GvBeans gvBeans = null;
        switch (type) {
            case 0:
                gvBeans = mVegetablesBean.get(position);
                scalePresenter.setGvBeans(mVegetablesBean.get(position));
                fruitAdapter.setSelectPosition(-1);
                vegetableAdapter.setSelectPosition(position);
                break;
            case 1:
                gvBeans = mFruitsBean.get(position);
                scalePresenter.setGvBeans(mFruitsBean.get(position));
                fruitAdapter.setSelectPosition(position);
                vegetableAdapter.setSelectPosition(-1);
                break;
        }

        vegetableAdapter.notifyDataSetChanged();
        fruitAdapter.notifyDataSetChanged();
    }

    private void addGoodsByScale(String total, GvBeans gvBeans) {
        if (!scalePresenter.isStable() || scalePresenter.net <= 0) {
            return;
        }
    }

    /**
     * 更新秤显示信息
     *
     * @param net
     * @param pnet
     * @param statu
     */
    private void updateScaleInfo(final int net, int pnet, int statu) {

    }

    private void showPresetTare() {

        mInputDialog = new Input2Dialog.Builder(this)
                .setTitle(ResourcesUtils.getString(R.string.more_num_peele) + "/kg")
                .setLeftText(ResourcesUtils.getString(R.string.cancel))
                .setRightText(ResourcesUtils.getString(R.string.confrim))
                .setHint("0.00kg")
                .setCallBack(new InputDialog.DialogOnClickCallback() {
                    @Override
                    public void onSure(String text) {
                        if (!TextUtils.isEmpty(text)) {
                            float pnet = Float.parseFloat(text);
                            scalePresenter.setNumPnet((int) (pnet * 1000.0f));
                        }
                        mInputDialog.dismiss();
                    }

                    @Override
                    public void onCancel() {
                        mInputDialog.dismiss();
                    }
                })
                .build();
        mInputDialog.show();
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

    private View createBottomSheetView() {

        return null;
    }


    @Subscribe(
            threadMode = ThreadMode.MAIN
    )
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
        mMenuAdapter.addGoodsTotalPrice(event.price);

        List<MenuItemModule> modules = mMenuAdapter.getDatas();
        if (event.priceType == Constants.WEIGHT_PRICE_TYPE) {
            //称重类型商品直接添加
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
        mGoodsTotalPriceTv.setText(String.valueOf(mMenuAdapter.getGoodsTotalPrice() / 100));
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

    private void showDomainLock() {
        if (this.popWindow == null) {
            View var1 = LayoutInflater.from(this).inflate(R.layout.pop_lock, (ViewGroup) null);
            var1.setOnClickListener(this);
            this.popWindow = (new CustomPopWindow.PopupWindowBuilder(this)).setView(var1).create();
        }

//        this.popWindow.showAsDropDown(this.ivUserHeadIcon, -133, 15);
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

        if (scalePresenter != null && scalePresenter.isScaleSuccess()) {
            scalePresenter.onDestroy();
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
        List<GoodsSortTagModle> tags = mGoodsSortAdapter.getDatas();
        tags.clear();

        if (totalDatas <= 0 || page * DEFAULT_PAGE_SIZE >= totalDatas) {
            //没有该页对应的数据
            Toast.makeText(this, "没有更多数据了", Toast.LENGTH_SHORT).show();
//            mNextPageTv.setEnabled(false);
            currPage = page - 1;
            return;
        } else {
            currPage = page;
            if ((page + 1) * DEFAULT_PAGE_SIZE >= totalDatas) {
//                mNextPageTv.setEnabled(false);
            } else {
//                mNextPageTv.setEnabled(true);
            }
        }

        if (page < 0) {
            this.currPage = 0;
            Toast.makeText(this, "已经是第一页了", Toast.LENGTH_SHORT).show();
//            mPrePageTv.setEnabled(false);
            return;
        } else {
            currPage = page;
//            mPrePageTv.setEnabled(true);
        }

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
            bundle.putString(PayingActivity.GOODS_AUTHO_DATA, authoData);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Toast.makeText(this, "订单生成失败，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void change(int type, float money) {
        if (mMenuAdapter == null) return;
        mGoodsCountTv.setText(String.valueOf(mMenuAdapter.getGoodsCount()));
        mGoodsTotalPriceTv.setText(String.valueOf(mMenuAdapter.getGoodsTotalPrice() / 100));
        switch (type) {
            case HomeMenuAdapter.CHANGE_TYPE_ADD:
                break;
            case HomeMenuAdapter.CHANGE_TYPE_DELEASE:
                break;
                case HomeMenuAdapter.CHANGE_TYPE_DELETE:
                break;

        }
    }
}
