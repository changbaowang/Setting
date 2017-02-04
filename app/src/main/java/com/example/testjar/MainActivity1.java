package com.example.testjar;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hexing.fdm.protocol.comm.CommOpticalSerialPort;
import cn.hexing.fdm.protocol.icomm.ICommucation;
import cn.hexing.fdm.protocol.model.CommPara;
import cn.hexing.fdm.protocol.model.HXFramePara;
import cn.hexing.fdm.services.CommServer;

public class MainActivity1 extends ActionBarActivity {
    private View view1, view2;
    private ViewPager mViewPager;  //对应的viewPager
    private List<View> viewList;//view数组
    private Button btn1, btn2, btn3,btn4,btn5,btnTimeSycn;

    public  String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.viewpager_item_view1, null);
        view2 = inflater.inflate(R.layout.viewpager_item_view2, null);
        btn1 = (Button) view1.findViewById(R.id.main1_viewpager_view1_btn1);
        btn2 = (Button) view2.findViewById(R.id.main1_viewpager_view2_btn1);
        btn3 = (Button) view2.findViewById(R.id.main1_viewpager_view2_btn2);
        btn4 = (Button) view2.findViewById(R.id.btnSwitchOn);
        btn5 = (Button) view2.findViewById(R.id.btnSwitchOut);
        btnTimeSycn = (Button) view1.findViewById(R.id.btnTimeSycn);
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view2);
        viewList.add(view1);


        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };



        mViewPager.setAdapter(pagerAdapter);
        btn1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btn2.setClickable(false);
                                        btn3.setClickable(false);
                                        btn4.setClickable(false);
                                        btn5.setClickable(false);
                                        btnTimeSycn.setClickable(false);
                                        CommServer commDlmsServer = null;
                                        ICommucation icomm = null;
                                        try {
                                            //执行上电操作
                                            RS232Controller mPower232 = new RS232Controller();
                                            mPower232.Rs232_PowerOn();
                                            // 光电头通讯参数设置
                                            String uartpath = "/dev/ttySAC3";
                                            int baudrate = 4800;
                                            int nBits = 8;
                                            String sVerify = "N";
                                            char cVerify = sVerify.charAt(0);
                                            int nStop = 1;
                                            icomm = new CommOpticalSerialPort();
                                            CommPara Cpara = new CommPara();
                                            Cpara.setComName(uartpath);
                                            Cpara.setBRate(baudrate);
                                            Cpara.setDBit(nBits);
                                            Cpara.setPty(cVerify);
                                            Cpara.setSbit(nStop);

                                            // DLMS 通讯参数
                                            final HXFramePara FramePara = new HXFramePara();
                                            FramePara.CommDeviceType = "Optical";// RF  Optical
                                            FramePara.FirstFrame = true;
                                            FramePara.Mode = HXFramePara.AuthMode.HLS;
                                            FramePara.enLevel = 0x00;
                                            FramePara.SourceAddr = 0x03;
                                            FramePara.strMeterNo = "254455455";
                                            FramePara.WaitT = 3000;
                                            FramePara.ByteWaitT = 1500;
                                            FramePara.Pwd = "00000000";
                                            FramePara.aesKey = new byte[16];
                                            FramePara.auKey = new byte[16];
                                            FramePara.enKey = new byte[16];
                                            String sysTstr = "4845430005000001";
                                            FramePara.StrsysTitleC = "4845430005000001";
                                            FramePara.encryptionMethod = HXFramePara.AuthMethod.AES_GCM_128;
                                            FramePara.sysTitleS = new byte[8];
                                            FramePara.MaxSendInfo_Value = 255;
                                            commDlmsServer = new CommServer();
                                            icomm = commDlmsServer.OpenDevice(Cpara, icomm);
                                            final CommServer finalCommDlmsServer = commDlmsServer;
                                            final ICommucation finalIcomm = icomm;
                                            FramePara.OBISattri = "1#1.0.144.128.0.255#2";//清事件
                                            FramePara.strDecDataType = "U8";
                                            FramePara.WriteData = "01";
                                            boolean strResult = commDlmsServer.Write(FramePara, icomm);
                                            if (strResult) {
                                                Toast.makeText(getApplicationContext(), "Clear Event Succeed",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Clear Event Failure",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            if (FramePara.CommDeviceType == "Optical") {
                                                commDlmsServer.DiscFrame(icomm);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            btn2.setClickable(true);
                                            btn3.setClickable(true);
                                            btn4.setClickable(true);
                                            btn5.setClickable(true);
                                            btnTimeSycn.setClickable(true);
                                            if (icomm != null) {
                                                commDlmsServer.Close(icomm);
                                            }
                                        }
                                    }
                                }
        );
        btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btn1.setClickable(false);
                                        btn3.setClickable(false);
                                        btn4.setClickable(false);
                                        btn5.setClickable(false);
                                        btnTimeSycn.setClickable(false);
                                        CommServer commDlmsServer = null;
                                        ICommucation icomm = null;
                                        try {
                                            //执行上电操作
                                            RS232Controller mPower232 = new RS232Controller();
                                            mPower232.Rs232_PowerOn();
                                            // 光电头通讯参数设置
                                            String uartpath = "/dev/ttySAC3";
                                            int baudrate = 4800;
                                            int nBits = 8;
                                            String sVerify = "N";
                                            char cVerify = sVerify.charAt(0);
                                            int nStop = 1;
                                            icomm = new CommOpticalSerialPort();
                                            CommPara Cpara = new CommPara();
                                            Cpara.setComName(uartpath);
                                            Cpara.setBRate(baudrate);
                                            Cpara.setDBit(nBits);
                                            Cpara.setPty(cVerify);
                                            Cpara.setSbit(nStop);

                                            // DLMS 通讯参数
                                            final HXFramePara FramePara = new HXFramePara();
                                            FramePara.CommDeviceType = "Optical";// RF  Optical
                                            FramePara.FirstFrame = true;
                                            FramePara.Mode = HXFramePara.AuthMode.HLS;
                                            FramePara.enLevel = 0x00;
                                            FramePara.SourceAddr = 0x03;
                                            FramePara.strMeterNo = "254455455";
                                            FramePara.WaitT = 3000;
                                            FramePara.ByteWaitT = 1500;
                                            FramePara.Pwd = "00000000";
                                            FramePara.aesKey = new byte[16];
                                            FramePara.auKey = new byte[16];
                                            FramePara.enKey = new byte[16];
                                            String sysTstr = "4845430005000001";
                                            FramePara.StrsysTitleC = "4845430005000001";
                                            FramePara.encryptionMethod = HXFramePara.AuthMethod.AES_GCM_128;
                                            FramePara.sysTitleS = new byte[8];
                                            FramePara.MaxSendInfo_Value = 255;
                                            commDlmsServer = new CommServer();
                                            icomm = commDlmsServer.OpenDevice(Cpara, icomm);
                                            final CommServer finalCommDlmsServer = commDlmsServer;
                                            final ICommucation finalIcomm = icomm;
                                            FramePara.OBISattri = "1#1.0.131.128.0.255#2";//设置显示项
                                            FramePara.strDecDataType = "Octs_string";
                                            FramePara.WriteData = "0DCA619010C37DC362C195C180C420C400C410C451C13CC00BC03E";
                                            boolean strResult = commDlmsServer.Write(FramePara, icomm);
                                            if (strResult) {
                                                FramePara.FirstFrame = false;
                                                FramePara.OBISattri = "1#1.0.134.128.0.255#2";//设置键1显示项
                                                FramePara.strDecDataType = "Octs_string";
                                                FramePara.WriteData = "0DCA619010C37DC362C195C180C420C400C410C451C13CC00BC03E";
                                                strResult = commDlmsServer.Write(FramePara, icomm);
                                                if (strResult) {
                                                    Toast.makeText(getApplicationContext(), "Display1 Setting(Single) Succeed",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Display1 Setting(Single) Failure",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Display Setting(Single) Failure",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            btn1.setClickable(true);
                                            btn3.setClickable(true);
                                            btn4.setClickable(true);
                                            btn5.setClickable(true);
                                            btnTimeSycn.setClickable(true);
                                            if (icomm != null) {
                                                commDlmsServer.Close(icomm);
                                            }
                                        }
                                    }
                                }
        );
        btn3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btn1.setClickable(false);
                                        btn2.setClickable(false);
                                        btn4.setClickable(false);
                                        btn5.setClickable(false);
                                        btnTimeSycn.setClickable(false);
                                        CommServer commDlmsServer = null;
                                        ICommucation icomm = null;
                                        try {
                                            //执行上电操作
                                            RS232Controller mPower232 = new RS232Controller();
                                            mPower232.Rs232_PowerOn();
                                            // 光电头通讯参数设置
                                            String uartpath = "/dev/ttySAC3";
                                            int baudrate = 4800;
                                            int nBits = 8;
                                            String sVerify = "N";
                                            char cVerify = sVerify.charAt(0);
                                            int nStop = 1;
                                            icomm = new CommOpticalSerialPort();
                                            CommPara Cpara = new CommPara();
                                            Cpara.setComName(uartpath);
                                            Cpara.setBRate(baudrate);
                                            Cpara.setDBit(nBits);
                                            Cpara.setPty(cVerify);
                                            Cpara.setSbit(nStop);

                                            // DLMS 通讯参数
                                            final HXFramePara FramePara = new HXFramePara();
                                            FramePara.CommDeviceType = "Optical";// RF  Optical
                                            FramePara.FirstFrame = true;
                                            FramePara.Mode = HXFramePara.AuthMode.HLS;
                                            FramePara.enLevel = 0x00;
                                            FramePara.SourceAddr = 0x03;
                                            FramePara.strMeterNo = "254455455";
                                            FramePara.WaitT = 3000;
                                            FramePara.ByteWaitT = 1500;
                                            FramePara.Pwd = "00000000";
                                            FramePara.aesKey = new byte[16];
                                            FramePara.auKey = new byte[16];
                                            FramePara.enKey = new byte[16];
                                            String sysTstr = "4845430005000001";
                                            FramePara.StrsysTitleC = "4845430005000001";
                                            FramePara.encryptionMethod = HXFramePara.AuthMethod.AES_GCM_128;
                                            FramePara.sysTitleS = new byte[8];
                                            FramePara.MaxSendInfo_Value = 255;
                                            commDlmsServer = new CommServer();
                                            icomm = commDlmsServer.OpenDevice(Cpara, icomm);
                                            final CommServer finalCommDlmsServer = commDlmsServer;
                                            final ICommucation finalIcomm = icomm;
                                            FramePara.OBISattri = "1#1.0.131.128.0.255#2";//设置显示项
                                            FramePara.strDecDataType = "Octs_string";
                                            FramePara.WriteData = "15CA61901060409030C37DC362C195C180C420C400C410C451C401C411C452C402C412C453C13CC00BC03E";
                                            boolean strResult = commDlmsServer.Write(FramePara, icomm);
                                            if (strResult) {
                                                FramePara.FirstFrame = false;
                                                FramePara.OBISattri = "1#1.0.134.128.0.255#2";//设置键1显示项
                                                FramePara.strDecDataType = "Octs_string";
                                                FramePara.WriteData = "15CA61901060409030C37DC362C195C180C420C400C410C451C401C411C452C402C412C453C13CC00BC03E";
                                                strResult = commDlmsServer.Write(FramePara, icomm);
                                                if (strResult) {
                                                    Toast.makeText(getApplicationContext(), "Display Setting(Three) Succeed",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Display 1 Setting(Three) Failure",
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Display Setting(Three) Failure",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            if (FramePara.CommDeviceType == "Optical") {
                                                commDlmsServer.DiscFrame(icomm);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            btn1.setClickable(true);
                                            btn2.setClickable(true);
                                            btn4.setClickable(true);
                                            btn5.setClickable(true);
                                            btnTimeSycn.setClickable(true);
                                            if (icomm != null) {
                                                commDlmsServer.Close(icomm);
                                            }
                                        }
                                    }
                                }
        );
        btn4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btn1.setClickable(false);
                                        btn2.setClickable(false);
                                        btn3.setClickable(false);
                                        btn5.setClickable(false);
                                        btnTimeSycn.setClickable(false);
                                        CommServer commDlmsServer = null;
                                        ICommucation icomm = null;
                                        try {
                                            //执行上电操作
                                            RS232Controller mPower232 = new RS232Controller();
                                            mPower232.Rs232_PowerOn();
                                            // 光电头通讯参数设置
                                            String uartpath = "/dev/ttySAC3";
                                            int baudrate = 4800;
                                            int nBits = 8;
                                            String sVerify = "N";
                                            char cVerify = sVerify.charAt(0);
                                            int nStop = 1;
                                            icomm = new CommOpticalSerialPort();
                                            CommPara Cpara = new CommPara();
                                            Cpara.setComName(uartpath);
                                            Cpara.setBRate(baudrate);
                                            Cpara.setDBit(nBits);
                                            Cpara.setPty(cVerify);
                                            Cpara.setSbit(nStop);

                                            // DLMS 通讯参数
                                            final HXFramePara FramePara = new HXFramePara();
                                            FramePara.CommDeviceType = "Optical";// RF  Optical
                                            FramePara.FirstFrame = true;
                                            FramePara.Mode = HXFramePara.AuthMode.HLS;
                                            FramePara.enLevel = 0x00;
                                            FramePara.SourceAddr = 0x03;
                                            FramePara.strMeterNo = "254455455";
                                            FramePara.WaitT = 3000;
                                            FramePara.ByteWaitT = 1500;
                                            FramePara.Pwd = "00000000";
                                            FramePara.aesKey = new byte[16];
                                            FramePara.auKey = new byte[16];
                                            FramePara.enKey = new byte[16];
                                            String sysTstr = "4845430005000001";
                                            FramePara.StrsysTitleC = "4845430005000001";
                                            FramePara.encryptionMethod = HXFramePara.AuthMethod.AES_GCM_128;
                                            FramePara.sysTitleS = new byte[8];
                                            FramePara.MaxSendInfo_Value = 255;
                                            commDlmsServer = new CommServer();
                                            icomm = commDlmsServer.OpenDevice(Cpara, icomm);
                                            final CommServer finalCommDlmsServer = commDlmsServer;
                                            final ICommucation finalIcomm = icomm;
                                            FramePara.OBISattri = "70#0.0.96.3.10.255#2";//合闸  
                                            FramePara.strDecDataType = "";
                                            FramePara.WriteData = "";
                                            boolean strResult = commDlmsServer.Action(FramePara, icomm);
                                            if (strResult) {

                                                    Toast.makeText(getApplicationContext(), "Relay On Succeed",
                                                            Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Relay On Failure",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            if (FramePara.CommDeviceType == "Optical") {
                                                commDlmsServer.DiscFrame(icomm);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            btn1.setClickable(true);
                                            btn2.setClickable(true);
                                            btn3.setClickable(true);
                                            btn5.setClickable(true);
                                            btnTimeSycn.setClickable(true);
                                            if (icomm != null) {
                                                commDlmsServer.Close(icomm);
                                            }
                                        }
                                    }
                                }
        );
        btn5.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btn1.setClickable(false);
                                        btn2.setClickable(false);
                                        btn3.setClickable(false);
                                        btn4.setClickable(false);
                                        btnTimeSycn.setClickable(false);
                                        CommServer commDlmsServer = null;
                                        ICommucation icomm = null;
                                        try {
                                            //执行上电操作
                                            RS232Controller mPower232 = new RS232Controller();
                                            mPower232.Rs232_PowerOn();
                                            // 光电头通讯参数设置
                                            String uartpath = "/dev/ttySAC3";
                                            int baudrate = 4800;
                                            int nBits = 8;
                                            String sVerify = "N";
                                            char cVerify = sVerify.charAt(0);
                                            int nStop = 1;
                                            icomm = new CommOpticalSerialPort();
                                            CommPara Cpara = new CommPara();
                                            Cpara.setComName(uartpath);
                                            Cpara.setBRate(baudrate);
                                            Cpara.setDBit(nBits);
                                            Cpara.setPty(cVerify);
                                            Cpara.setSbit(nStop);

                                            // DLMS 通讯参数
                                            final HXFramePara FramePara = new HXFramePara();
                                            FramePara.CommDeviceType = "Optical";// RF  Optical
                                            FramePara.FirstFrame = true;
                                            FramePara.Mode = HXFramePara.AuthMode.HLS;
                                            FramePara.enLevel = 0x00;
                                            FramePara.SourceAddr = 0x03;
                                            FramePara.strMeterNo = "254455455";
                                            FramePara.WaitT = 3000;
                                            FramePara.ByteWaitT = 1500;
                                            FramePara.Pwd = "00000000";
                                            FramePara.aesKey = new byte[16];
                                            FramePara.auKey = new byte[16];
                                            FramePara.enKey = new byte[16];
                                            String sysTstr = "4845430005000001";
                                            FramePara.StrsysTitleC = "4845430005000001";
                                            FramePara.encryptionMethod = HXFramePara.AuthMethod.AES_GCM_128;
                                            FramePara.sysTitleS = new byte[8];
                                            FramePara.MaxSendInfo_Value = 255;
                                            commDlmsServer = new CommServer();
                                            icomm = commDlmsServer.OpenDevice(Cpara, icomm);
                                            final CommServer finalCommDlmsServer = commDlmsServer;
                                            final ICommucation finalIcomm = icomm;
                                            FramePara.OBISattri = "70#0.0.96.3.10.255#1";//拉闸  
                                            FramePara.strDecDataType = "";
                                            FramePara.WriteData = "";
                                            boolean strResult = commDlmsServer.Action(FramePara, icomm);
                                            if (strResult) {

                                                Toast.makeText(getApplicationContext(), "Relay Off Succeed",
                                                        Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Relay Off Failure",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            if (FramePara.CommDeviceType == "Optical") {
                                                commDlmsServer.DiscFrame(icomm);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            btn1.setClickable(true);
                                            btn2.setClickable(true);
                                            btn3.setClickable(true);
                                            btn4.setClickable(true);
                                            btnTimeSycn.setClickable(true);
                                            if (icomm != null) {
                                                commDlmsServer.Close(icomm);
                                            }
                                        }
                                    }
                                }
        );




        btnTimeSycn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btn1.setClickable(false);
                                        btn2.setClickable(false);
                                        btn3.setClickable(false);
                                        btn4.setClickable(false);
                                        btn5.setClickable(false);
                                        CommServer commDlmsServer = null;
                                        ICommucation icomm = null;
                                        try {
                                            //执行上电操作
                                            RS232Controller mPower232 = new RS232Controller();
                                            mPower232.Rs232_PowerOn();
                                            // 光电头通讯参数设置
                                            String uartpath = "/dev/ttySAC3";
                                            int baudrate = 4800;
                                            int nBits = 8;
                                            String sVerify = "N";
                                            char cVerify = sVerify.charAt(0);
                                            int nStop = 1;
                                            icomm = new CommOpticalSerialPort();
                                            CommPara Cpara = new CommPara();
                                            Cpara.setComName(uartpath);
                                            Cpara.setBRate(baudrate);
                                            Cpara.setDBit(nBits);
                                            Cpara.setPty(cVerify);
                                            Cpara.setSbit(nStop);

                                            // DLMS 通讯参数
                                            final HXFramePara FramePara = new HXFramePara();
                                            FramePara.CommDeviceType = "Optical";// RF  Optical
                                            FramePara.FirstFrame = true;
                                            FramePara.Mode = HXFramePara.AuthMode.HLS;
                                            FramePara.enLevel = 0x00;
                                            FramePara.SourceAddr = 0x03;
                                            FramePara.strMeterNo = "254455455";
                                            FramePara.WaitT = 3000;
                                            FramePara.ByteWaitT = 1500;
                                            FramePara.Pwd = "00000000";
                                            FramePara.aesKey = new byte[16];
                                            FramePara.auKey = new byte[16];
                                            FramePara.enKey = new byte[16];
                                            String sysTstr = "4845430005000001";
                                            FramePara.StrsysTitleC = "4845430005000001";
                                            FramePara.encryptionMethod = HXFramePara.AuthMethod.AES_GCM_128;
                                            FramePara.sysTitleS = new byte[8];
                                            FramePara.MaxSendInfo_Value = 255;
                                            commDlmsServer = new CommServer();
                                            icomm = commDlmsServer.OpenDevice(Cpara, icomm);
                                            final CommServer finalCommDlmsServer = commDlmsServer;
                                            final ICommucation finalIcomm = icomm;
                                            FramePara.OBISattri = "8#0.0.1.0.0.255#2";//清事件
                                            FramePara.strDecDataType = "Octs_datetime";

                                            FramePara.WriteData = getStringDate();

                                            Toast.makeText(getApplicationContext(), FramePara.WriteData,
                                                    Toast.LENGTH_SHORT).show();

                                            boolean strResult = commDlmsServer.Write(FramePara, icomm);
                                            if (strResult) {
                                                Toast.makeText(getApplicationContext(), "Time Synchronization Succeed",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Time Synchronization Failure",
                                                        Toast.LENGTH_SHORT).show();
                                            }
//                                            FramePara.WriteData="";
//                                            FramePara.FirstFrame=false;
//                                            String strRe = commDlmsServer.Read(FramePara, icomm);
//                                            Toast.makeText(getApplicationContext(), strRe,
//                                                    Toast.LENGTH_SHORT).show();

                                            if (FramePara.CommDeviceType == "Optical") {
                                                commDlmsServer.DiscFrame(icomm);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            btn1.setClickable(true);
                                            btn2.setClickable(true);
                                            btn3.setClickable(true);
                                            btn4.setClickable(true);
                                            btn5.setClickable(true);
                                            if (icomm != null) {
                                                commDlmsServer.Close(icomm);
                                            }
                                        }
                                    }
                                }
        );
    }

    private ProgressDialog m_pDialog;

//    protected void dialog() {
//        m_pDialog = new ProgressDialog(MainActivity1.this);
//        // 设置进度条风格，风格为圆形，旋转的
//        m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        // 设置ProgressDialog 标题
//        m_pDialog.setTitle("提示");
//        // 设置ProgressDialog 提示信息
//        m_pDialog.setMessage("正在处理数据,请稍等!!");
//        // 设置ProgressDialog 的进度条是否不明确
//        m_pDialog.setIndeterminate(false);
//        // 设置ProgressDialog 是否可以按退回按键取消
//        m_pDialog.setCancelable(false);
//        m_pDialog.show();
//    }
}

