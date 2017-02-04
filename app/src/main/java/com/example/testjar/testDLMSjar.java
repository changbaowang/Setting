package com.example.testjar;

import cn.hexing.fdm.protocol.bll.dlmsService.DataType;
import cn.hexing.fdm.protocol.comm.CommOpticalSerialPort;
import cn.hexing.fdm.protocol.icomm.ICommucation;
import cn.hexing.fdm.protocol.model.CommPara;
import cn.hexing.fdm.protocol.model.HXFramePara;
import cn.hexing.fdm.protocol.model.HXFramePara.AuthMethod;
import cn.hexing.fdm.protocol.model.HXFramePara.AuthMode;
import cn.hexing.fdm.services.CommServer;

public class testDLMSjar {
	private synchronized String Read(String strOBIS, DataType dType, String strMeterNo,
			String strUnit) {
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

	private String Write(String strOBIS, DataType dType, String strMeterNo,
			String WriteData) {
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

			FramePara.WriteData = WriteData;// "2014-11-12 06:06:06";
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

}
