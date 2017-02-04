package com.example.testjar;

import cn.hexing.fdm.protocol.bll.dlmsService.DataType;
import cn.hexing.fdm.protocol.comm.CommOpticalSerialPort;
import cn.hexing.fdm.protocol.icomm.ICommucation;
import cn.hexing.fdm.protocol.model.CommPara;
import cn.hexing.fdm.protocol.model.HXFramePara;
import cn.hexing.fdm.protocol.model.HXFramePara.AuthMethod;
import cn.hexing.fdm.protocol.model.HXFramePara.AuthMode;
import cn.hexing.fdm.services.CommServer;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {

    public Button btnTest;
    public Button btnDisplay;
    public Button btnDispalyThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        btnTest = (Button) findViewById(R.id.btnTest);
        btnTest.setOnClickListener(this);

        btnDisplay = (Button) findViewById(R.id.btnDispaly);
        btnDisplay.setOnClickListener(this);
        btnDispalyThree = (Button) findViewById(R.id.btnDisplayThree);
        btnDispalyThree.setOnClickListener(this);
        return true;
    }

    public void onClick(View v) {
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
            HXFramePara FramePara = new HXFramePara();
            FramePara.CommDeviceType = "Optical";// RF  Optical
            FramePara.FirstFrame = true;
            FramePara.Mode = AuthMode.HLS;
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
            FramePara.encryptionMethod = AuthMethod.AES_GCM_128;
            FramePara.sysTitleS = new byte[8];
            FramePara.MaxSendInfo_Value = 255;
            commDlmsServer = new CommServer();
            icomm = commDlmsServer.OpenDevice(Cpara, icomm);

            switch (v.getId()) {
                case R.id.btnTest:
//                    dialog();
                    Toast.makeText(getApplicationContext(), "xxxxxxxxxxxxxxxxxxDisplay Setting(Single) Failure",
                            Toast.LENGTH_SHORT).show();
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

//			FramePara.OBISattri = "22#0.0.15.0.0.255#4";//Monthly Billing Date
//			FramePara.strDecDataType = "Array_dd";
//			FramePara.WriteData="19";//
//
//			Boolean strRead =commDlmsServer.Write(FramePara, icomm);
//
//			if (strRead) {
//				//FramePara.WriteData="11";//
//				FramePara.FirstFrame=false;
//				String strResult1 =commDlmsServer.Read(FramePara, icomm);
//				Toast.makeText(getApplicationContext(), "Succeed :"+strResult1 ,
//						Toast.LENGTH_SHORT).show();
//			}
//			else
//			{
//				Toast.makeText(getApplicationContext(), "Failure" ,
//						Toast.LENGTH_SHORT).show();
//			}

                    break;
                case R.id.btnDispaly:
                    dialog();
                    FramePara.OBISattri = "1#1.0.131.128.0.255#2";//设置显示项
                    FramePara.strDecDataType = "Octs_string";
                    FramePara.WriteData = "0DCA619010C37DC362C195C180C420C400C410C451C13CC00BC03E";
                    strResult = commDlmsServer.Write(FramePara, icomm);
                    if (strResult) {
                        FramePara.FirstFrame = false;
                        FramePara.OBISattri = "1#1.0.134.128.0.255#2";//设置键1显示项
                        FramePara.strDecDataType = "Octs_string";
                        FramePara.WriteData = "0DCA619010C37DC362C195C180C420C400C410C451C13CC00BC03E";
                        strResult = commDlmsServer.Write(FramePara, icomm);
                        if (strResult) {
                            Toast.makeText(getApplicationContext(), "Display Setting(Single) Succeed",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Display 1 Setting(Single) Failure",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Display Setting(Single) Failure",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (FramePara.CommDeviceType == "Optical") {
                        commDlmsServer.DiscFrame(icomm);
                    }
                    break;
                case R.id.btnDisplayThree:
                    dialog();
                    FramePara.OBISattri = "1#1.0.131.128.0.255#2";//设置显示项
                    FramePara.strDecDataType = "Octs_string";
                    FramePara.WriteData = "15CA61901060409030C37DC362C195C180C420C400C410C451C401C411C452C402C412C453C13CC00BC03E";
                    strResult = commDlmsServer.Write(FramePara, icomm);
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
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            m_pDialog.cancel();
            if (icomm != null) {
                commDlmsServer.Close(icomm);

            }
        }
    }

    private ProgressDialog m_pDialog;

    protected void dialog() {
        m_pDialog = new ProgressDialog(MainActivity.this);
        // 设置进度条风格，风格为圆形，旋转的
        m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 标题
        m_pDialog.setTitle("提示");
        // 设置ProgressDialog 提示信息
        m_pDialog.setMessage("正在处理数据,请稍等!!");
        // 设置ProgressDialog 的进度条是否不明确
        m_pDialog.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        m_pDialog.setCancelable(false);
        m_pDialog.show();
    }

    private String Read(String strOBIS, DataType dType, String strMeterNo, String strUnit) {
        CommServer commDlmsServer = null;
        ICommucation icomm = null;
        String strResult = null;
        try {
            String uartpath = "/dev/ttySAC1";
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
            HXFramePara FramePara = new HXFramePara();
            FramePara.CommDeviceType = "RF";// RF Optical
            FramePara.FirstFrame = false;
            FramePara.Mode = AuthMode.HLS;
            FramePara.enLevel = 0x00;
            FramePara.SourceAddr = 0x03;
            FramePara.strMeterNo = strMeterNo;// "254455455";
            FramePara.OBISattri = strOBIS;// "70#0.0.96.3.10.255#1";
            FramePara.WaitT = 3000;
            FramePara.ByteWaitT = 1500;
            FramePara.Pwd = "00000000";
            FramePara.aesKey = new byte[16];
            FramePara.auKey = new byte[16];
            FramePara.enKey = new byte[16];
            FramePara.StrsysTitleC = "4845430005000001";
            FramePara.encryptionMethod = AuthMethod.AES_GCM_128;
            FramePara.sysTitleS = new byte[8];
            FramePara.MaxSendInfo_Value = 255;
            FramePara.decDataType = dType;// DataType.clock;
            FramePara.strUnitString = strUnit;

            commDlmsServer = new CommServer();
            icomm = commDlmsServer.OpenDevice(Cpara, icomm);

            strResult = commDlmsServer.Read(FramePara, icomm);

            if (FramePara.CommDeviceType == "Optical") {
                commDlmsServer.DiscFrame(icomm);
            }

        } catch (Exception ex) {
        } finally {
            if (icomm != null) {
                commDlmsServer.Close(icomm);
            }
        }
        return strResult;
    }

    private String Write(String strOBIS, DataType dType, String strMeterNo, String WriteData) {
        CommServer commDlmsServer = null;
        ICommucation icomm = null;
        String strResult = null;
        try {
            String uartpath = "/dev/ttySAC1";
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
            HXFramePara FramePara = new HXFramePara();
            FramePara.CommDeviceType = "RF";// RF Optical
            FramePara.FirstFrame = false;
            FramePara.Mode = AuthMode.HLS;
            FramePara.enLevel = 0x00;
            FramePara.SourceAddr = 0x03;
            FramePara.strMeterNo = strMeterNo;// "254455455";
            FramePara.OBISattri = strOBIS;// "70#0.0.96.3.10.255#1";
            FramePara.WaitT = 3000;
            FramePara.ByteWaitT = 1500;
            FramePara.Pwd = "00000000";
            FramePara.aesKey = new byte[16];
            FramePara.auKey = new byte[16];
            FramePara.enKey = new byte[16];
            FramePara.StrsysTitleC = "4845430005000001";
            FramePara.encryptionMethod = AuthMethod.AES_GCM_128;
            FramePara.sysTitleS = new byte[8];
            FramePara.MaxSendInfo_Value = 255;
            FramePara.decDataType = dType;// DataType.clock;
            FramePara.strUnitString = "";

            commDlmsServer = new CommServer();
            icomm = commDlmsServer.OpenDevice(Cpara, icomm);

            FramePara.WriteData = WriteData;//"2014-11-12 06:06:06";
            boolean WFlag = commDlmsServer.Write(FramePara, icomm);

            if (FramePara.CommDeviceType == "Optical") {
                commDlmsServer.DiscFrame(icomm);
            }

        } catch (Exception ex) {
        } finally {
            if (icomm != null) {
                commDlmsServer.Close(icomm);
            }
        }
        return strResult;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
