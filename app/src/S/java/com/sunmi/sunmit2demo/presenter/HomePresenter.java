package com.sunmi.sunmit2demo.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.sunmit2demo.Constants;
import com.sunmi.sunmit2demo.PreferenceUtil;
import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.modle.AllClassAndGoodsResult;
import com.sunmi.sunmit2demo.modle.ClassAndGoodsModle;
import com.sunmi.sunmit2demo.modle.CreateOrderResult;
import com.sunmi.sunmit2demo.modle.GoodsOrderModle;
import com.sunmi.sunmit2demo.modle.MenuItemModule;
import com.sunmi.sunmit2demo.modle.OrderInfo;
import com.sunmi.sunmit2demo.modle.PrinterModle;
import com.sunmi.sunmit2demo.modle.Result;
import com.sunmi.sunmit2demo.modle.ShopInfo;
import com.sunmi.sunmit2demo.presenter.contact.HomeClassAndGoodsContact;
import com.sunmi.sunmit2demo.print.DeviceConnFactoryManager;
import com.sunmi.sunmit2demo.print.PrinterCode;
import com.sunmi.sunmit2demo.print.PrinterCommand;
import com.sunmi.sunmit2demo.print.ThreadFactoryBuilder;
import com.sunmi.sunmit2demo.print.ThreadPool;
import com.sunmi.sunmit2demo.server.ServerManager;
import com.sunmi.sunmit2demo.utils.ToastUtil;
import com.sunmi.sunmit2demo.utils.Utils;
import com.tools.command.EscCommand;
import com.tools.command.LabelCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chrc
 * date   : 2019/5/2  4:07 PM
 * desc   :
 */
public class HomePresenter implements HomeClassAndGoodsContact.Presenter {


    private Context context;
    private HomeClassAndGoodsContact.View mView;

    public HomePresenter(Context context, HomeClassAndGoodsContact.View mView) {
        this.context = context;
        this.mView = mView;
    }


    @Override
    public void load() {
        Observable.create(new ObservableOnSubscribe<List<ClassAndGoodsModle>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ClassAndGoodsModle>> e) throws Exception {
                AllClassAndGoodsResult result = ServerManager.getClassGoodsList(Util.appId);
                try {
                    //这个请求不是必要的，不能中断整个流程。故try catch
                    Result<ShopInfo> shopInfoResult = ServerManager.getShopInfo(Util.appId);
                    if (shopInfoResult != null && shopInfoResult.getErrno() == 0 && shopInfoResult.getResult() != null) {
                        ShopInfo shopInfo = shopInfoResult.getResult();
                        ServerManager.downloadPic(shopInfo.getQrcode());
                        PreferenceUtil.putString(context, PreferenceUtil.KEY.SHOP_INFO, new Gson().toJson(shopInfoResult.getResult()));
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
                    e.onNext(result.getResult());
                } else if (result != null && result.getError() != null) {
                    e.onError(new Throwable(result.getError()));
                } else {
                    e.onError(new Throwable());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<ClassAndGoodsModle>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ClassAndGoodsModle> value) {
                        mView.loadComplete(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null && e.getMessage() != null) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
//                        mView.loadComplete(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void pay(final List<MenuItemModule> goodList, final int totalPrice) {
        if (goodList == null || goodList.size() == 0) return;
        Observable.create(new ObservableOnSubscribe<OrderInfo>() {
            @Override
            public void subscribe(ObservableEmitter<OrderInfo> e) throws Exception {
                List<GoodsOrderModle> orderModles = new ArrayList<>();
                int size = goodList.size();
                for (int i = 0; i < size; i++) {
                    MenuItemModule module = goodList.get(i);
                    float price;
                    if (module.getPriceType() == Constants.WEIGHT_PRICE_TYPE) {
                        price = module.getTotalPrice();
                    } else {
                        price = module.getPrice();
                    }
                    GoodsOrderModle orderModle = new GoodsOrderModle(module.getGoodsCode(), String.valueOf(module.getGoodsCount()), price);
                    orderModles.add(orderModle);
                }
                CreateOrderResult result = ServerManager.createOrder(Util.appId, orderModles, totalPrice);
                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
                    e.onNext(result.getResult());
                } else if (result != null && result.getError() != null) {
                    e.onError(new Throwable(result.getError()));
                } else {
                    e.onError(new Throwable());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<OrderInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OrderInfo value) {
                        mView.orderCreateComplete(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null && e.getMessage() != null) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        mView.orderCreateComplete(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public String getUsbDeviceList() {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        // Get the list of attached devices
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devices.values().iterator();
        int count = devices.size();
        if (count > 0) {
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                String devicename = device.getDeviceName();
                if (checkUsbDevicePidVid(device)) {
                    return devicename;
                }
            }
        }
        return "";
    }

    boolean checkUsbDevicePidVid(UsbDevice dev) {
        int pid = dev.getProductId();
        int vid = dev.getVendorId();
        return ((vid == 34918 && pid == 256) || (vid == 1137 && pid == 85)
                || (vid == 6790 && pid == 30084)
                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 512)
                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 768)
                || (vid == 26728 && pid == 1024) || (vid == 26728 && pid == 1280)
                || (vid == 26728 && pid == 1536));
    }

    private int counts;

    /**
     * 打印票据
     */
    public void printReceipt(final Handler mHandler,
                             final int id,
                             final PrinterModle printerModle,
                             final int count) {
        counts = count;
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState() ) {
            mView.printerOutOfConnected();
            ToastUtil.showShort( context, context.getString( R.string.str_cann_printer ) );
            return;
        }
        ThreadPool.getInstantiation().addTask( new Runnable() {
            @Override
            public void run() {
                if ( DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] != null
                        && DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState() ) {
                    ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder( "MainActivity_sendContinuity_Timer" );
                    final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor( 1, threadFactoryBuilder );
                    scheduledExecutorService.schedule( threadFactoryBuilder.newThread( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Log.i("bill_print===", "counts="+counts);
                            if (counts < 0) {
                                scheduledExecutorService.shutdown();
                                Log.i("bill_print===", "shutdown");
                                return;
                            }
                            if ( DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.ESC ) {
                                Log.i("bill_print===", "sendReceiptWithResponse");
                                sendReceiptWithResponse(id, printerModle);
                            }
                        }
                    } ), 1000, TimeUnit.MILLISECONDS );
                }
            }
        } );
//        ThreadPool threadPool = ThreadPool.getInstantiation();
//        threadPool.addTask( new Runnable() {
//            @Override
//            public void run() {
//                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.ESC ) {
//                    sendReceiptWithResponse(id, printerModle);
//                } else {
//                    mHandler.obtainMessage(PrinterCode.PRINTER_COMMAND_ERROR).sendToTarget();
//                }
//            }
//        } );
    }

    /**
     * 开钱箱
     */
    public void openCashBox(final Handler mHandler, final int id) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState() ) {
            mView.printerOutOfConnected();
            ToastUtil.showShort( context, context.getString( R.string.str_cann_printer ) );
            return;
        }
        ThreadPool threadPool = ThreadPool.getInstantiation();
        threadPool.addTask( new Runnable() {
            @Override
            public void run() {
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.ESC ) {
                    /* 开钱箱 */
                    EscCommand esc = new EscCommand();
                    esc.addGeneratePlus( LabelCommand.FOOT.F5, (byte) 255, (byte) 255 );
                    Vector<Byte> datas = esc.getCommand();
                    /* 发送数据 */
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately( datas );
                } else {
                    mHandler.obtainMessage(PrinterCode.PRINTER_COMMAND_ERROR).sendToTarget();
                }
            }
        } );
    }

    /**
     * 发送票据
     */
    void sendReceiptWithResponse(int id, PrinterModle printerModle) {
        ShopInfo shopInfo = null;
        String shopInfoString = PreferenceUtil.getString(context, PreferenceUtil.KEY.SHOP_INFO, "");
        if (!TextUtils.isEmpty(shopInfoString)) {
            shopInfo = new Gson().fromJson(shopInfoString, new TypeToken<ShopInfo>(){}.getType());
        }


        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines( (byte) 3 );
        /* 设置打印居中 */
        esc.addSelectJustification( EscCommand.JUSTIFICATION.CENTER );
        /* 设置为倍高倍宽 */
        esc.addSelectPrintModes( EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF );
        esc.addText( shopInfo.getShopname()+"\n" ,"GB2312");
        esc.addPrintAndLineFeed();
        esc.addPrintAndLineFeed();
        /* 打印文字 */
        /* 取消倍高倍宽 */
        esc.addSelectPrintModes( EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF );
        /* 设置打印左对齐 */
        esc.addSelectJustification( EscCommand.JUSTIFICATION.LEFT );
        /* 打印文字 */
        esc.addText( "订单号：" + printerModle.getOrderNo() + "\n" ,"GB2312");
        /* 打印时间 */
        esc.addText( "时间：" + printerModle.getTime() + "\n","GB2312" );
        esc.addText( "商品名称","GB2312" );
        //定位      单价   数量   总价
        esc.addSetHorAndVerMotionUnits( (byte) 7, (byte) 0 );
        esc.addSetAbsolutePrintPosition( (short) 7 );
        esc.addText( "单价","GB2312" );
        esc.addSetAbsolutePrintPosition( (short) 10 );
        esc.addText( "数量","GB2312" );
        esc.addSetAbsolutePrintPosition( (short) 13 );
        esc.addText( "总价\n","GB2312" );
        esc.addText( "————————————————\n","GB2312" );
        /* 打印商品详情 */
        //TODO
        for(MenuItemModule menuItem : printerModle.getModules()){
            esc.addText( menuItem.getGoodsName()+"\n","GB2312" );
            esc.addSetHorAndVerMotionUnits( (byte) 7, (byte) 0 );
            esc.addSetAbsolutePrintPosition( (short) 7 );
            esc.addText( menuItem.getPrice()/100.0+"","GB2312" );
            esc.addSetAbsolutePrintPosition( (short) 10 );
            esc.addText( menuItem.getGoodsCount()+"","GB2312" );
            esc.addSetAbsolutePrintPosition( (short) 12 );
            esc.addText( menuItem.getPrice()/100.0*menuItem.getGoodsCount()+"\n","GB2312" );
        }
        esc.addText( "————————————————\n","GB2312" );
        //打印商品总价
        esc.addText( "原价："+printerModle.getTotalPrice()+"\n" ,"GB2312");
        esc.addText( "优惠："+printerModle.getDiscountPrice()+"\n" ,"GB2312");
        esc.addText( "总价："+printerModle.getRealPrice()+"\n" ,"GB2312");
        esc.addText( "支付方式："+printerModle.getPayType()+"\n" ,"GB2312");
        esc.addText( "————————————————","GB2312" );
        //留白
        esc.addPrintAndLineFeed();
        esc.addText( "店铺地址："+shopInfo.getAdress()+"\n" ,"GB2312");
        esc.addText( "联系方式："+shopInfo.getPhone()+"\n" ,"GB2312");

        /*Bitmap b = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.gprinter );*/
        byte[] bitmaps = Util.read();
        if (bitmaps != null) {
            Bitmap b = BitmapFactory.decodeByteArray(bitmaps, 0, bitmaps.length);
            if (b != null) {
                /* 打印图片 */
                /* 设置打印居中 */
                esc.addSelectJustification( EscCommand.JUSTIFICATION.CENTER );
                esc.addRastBitImage( b, 320, 0 );
            }
        }

        /* 打印一维条码 *//*
        *//* 打印文字 *//*
        esc.addText( "Print code128\n" );
        esc.addSelectPrintingPositionForHRICharacters( EscCommand.HRI_POSITION.BELOW );
        *//*
         * 设置条码可识别字符位置在条码下方
         * 设置条码高度为60点
         *//*
        esc.addSetBarcodeHeight( (byte) 60 );
        *//* 设置条码单元宽度为1 *//*
        esc.addSetBarcodeWidth( (byte) 1 );
        *//* 打印Code128码 *//*
        esc.addCODE128( esc.genCodeB( "SMARNET" ) );
        esc.addPrintAndLineFeed();


        *//*
         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
         *//*
        *//* 打印文字 *//*
        esc.addText( "Print QRcode\n" );

        /* 设置纠错等级 *//*
        esc.addSelectErrorCorrectionLevelForQRCode( (byte) 0x31 );
        *//* 设置qrcode模块大小 *//*
        esc.addSelectSizeOfModuleForQRCode( (byte) 3 );
        *//* 设置qrcode内容 *//*
        esc.addStoreQRCodeData( "www.smarnet.cc" );
        esc.addPrintQRCode(); *//* 打印QRCode *//*
        esc.addPrintAndLineFeed();

        *//* 设置打印左对齐 *//*
        esc.addSelectJustification( EscCommand.JUSTIFICATION.CENTER );*/
        /* 打印文字 */
        //esc.addText( "Completed!\r\n" );

        /* 开钱箱 */
//        esc.addGeneratePlus( LabelCommand.FOOT.F5, (byte) 255, (byte) 255 );
        esc.addPrintAndFeedLines( (byte) 8 );
        /* 加入查询打印机状态，用于连续打印 */
        byte[] bytes = { 29, 114, 1 };
        esc.addUserCommand( bytes );
        Vector<Byte> datas = esc.getCommand();
        /* 发送数据 */
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately( datas );
    }

}
