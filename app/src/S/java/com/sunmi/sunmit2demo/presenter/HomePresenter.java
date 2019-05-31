package com.sunmi.sunmit2demo.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.modle.AllClassAndGoodsResult;
import com.sunmi.sunmit2demo.modle.ClassAndGoodsModle;
import com.sunmi.sunmit2demo.modle.CreateOrderResult;
import com.sunmi.sunmit2demo.modle.GoodsOrderModle;
import com.sunmi.sunmit2demo.modle.MenuItemModule;
import com.sunmi.sunmit2demo.modle.OrderInfo;
import com.sunmi.sunmit2demo.presenter.contact.HomeClassAndGoodsContact;
import com.sunmi.sunmit2demo.print.DeviceConnFactoryManager;
import com.sunmi.sunmit2demo.print.PrinterCode;
import com.sunmi.sunmit2demo.print.PrinterCommand;
import com.sunmi.sunmit2demo.print.ThreadPool;
import com.sunmi.sunmit2demo.server.ServerManager;
import com.sunmi.sunmit2demo.utils.Utils;
import com.tools.command.EscCommand;
import com.tools.command.LabelCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
                    e.onNext(result.getResult());
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
                    if (module.getPriceType() == 2) {
                        price = module.getPrice() * module.getGoodsCount();
                    } else {
                        price = module.getPrice();
                    }
                    GoodsOrderModle orderModle = new GoodsOrderModle(module.getGoodsCode(), String.valueOf(module.getGoodsCount()), price);
                    orderModles.add(orderModle);
                }
                CreateOrderResult result = ServerManager.createOrder(Util.appId, orderModles, totalPrice);
                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
                    e.onNext(result.getResult());
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

    /**
     * 打印票据
     */
    public void printReceipt(final Handler mHandler, final int id) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState() ) {
            Utils.toast( context, context.getString( R.string.str_cann_printer ) );
            return;
        }
        ThreadPool threadPool = ThreadPool.getInstantiation();
        threadPool.addTask( new Runnable() {
            @Override
            public void run() {
                if ( DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.ESC ) {
                    sendReceiptWithResponse(id);
                } else {
                    mHandler.obtainMessage(PrinterCode.PRINTER_COMMAND_ERROR).sendToTarget();
                }
            }
        } );
    }

    /**
     * 发送票据
     */
    void sendReceiptWithResponse(int id) {
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines( (byte) 3 );
        /* 设置打印居中 */
        esc.addSelectJustification( EscCommand.JUSTIFICATION.CENTER );
        /* 设置为倍高倍宽 */
        esc.addSelectPrintModes( EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF );
        /* 打印文字 */
        esc.addText( "Sample\n" );
        esc.addPrintAndLineFeed();

        /* 打印文字 */
        /* 取消倍高倍宽 */
        esc.addSelectPrintModes( EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF );
        /* 设置打印左对齐 */
        esc.addSelectJustification( EscCommand.JUSTIFICATION.LEFT );
        /* 打印文字 */
        esc.addText( "Print text\n" );
        /* 打印文字 */
        esc.addText( "Welcome to use SMARNET printer!\n" );

        /* 打印繁体中文 需要打印机支持繁体字库 */
        String message = "佳博智匯票據打印機\n";
        esc.addText( message, "GB2312" );
        esc.addPrintAndLineFeed();

        /* 绝对位置 具体详细信息请查看GP58编程手册 */
        esc.addText( "智汇" );
        esc.addSetHorAndVerMotionUnits( (byte) 7, (byte) 0 );
        esc.addSetAbsolutePrintPosition( (short) 6 );
        esc.addText( "网络" );
        esc.addSetAbsolutePrintPosition( (short) 10 );
        esc.addText( "设备" );
        esc.addPrintAndLineFeed();

        /* 打印图片 */
        /* 打印文字 */
        esc.addText( "Print bitmap!\n" );
        Bitmap b = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.gprinter );
        /* 打印图片 */
        esc.addRastBitImage( b, 380, 0 );

        /* 打印一维条码 */
        /* 打印文字 */
        esc.addText( "Print code128\n" );
        esc.addSelectPrintingPositionForHRICharacters( EscCommand.HRI_POSITION.BELOW );
        /*
         * 设置条码可识别字符位置在条码下方
         * 设置条码高度为60点
         */
        esc.addSetBarcodeHeight( (byte) 60 );
        /* 设置条码单元宽度为1 */
        esc.addSetBarcodeWidth( (byte) 1 );
        /* 打印Code128码 */
        esc.addCODE128( esc.genCodeB( "SMARNET" ) );
        esc.addPrintAndLineFeed();


        /*
         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
         */
        /* 打印文字 */
        esc.addText( "Print QRcode\n" );
        /* 设置纠错等级 */
        esc.addSelectErrorCorrectionLevelForQRCode( (byte) 0x31 );
        /* 设置qrcode模块大小 */
        esc.addSelectSizeOfModuleForQRCode( (byte) 3 );
        /* 设置qrcode内容 */
        esc.addStoreQRCodeData( "www.smarnet.cc" );
        esc.addPrintQRCode(); /* 打印QRCode */
        esc.addPrintAndLineFeed();

        /* 设置打印左对齐 */
        esc.addSelectJustification( EscCommand.JUSTIFICATION.CENTER );
        /* 打印文字 */
        esc.addText( "Completed!\r\n" );

        /* 开钱箱 */
        esc.addGeneratePlus( LabelCommand.FOOT.F5, (byte) 255, (byte) 255 );
        esc.addPrintAndFeedLines( (byte) 8 );
        /* 加入查询打印机状态，用于连续打印 */
        byte[] bytes = { 29, 114, 1 };
        esc.addUserCommand( bytes );
        Vector<Byte> datas = esc.getCommand();
        /* 发送数据 */
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately( datas );
    }
}
