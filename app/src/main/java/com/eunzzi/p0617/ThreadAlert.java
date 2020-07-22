package com.eunzzi.p0617;

import android.os.Handler;

public class ThreadAlert extends Thread {
    Handler handler;

    public ThreadAlert(Handler handler) {
        this.handler = handler;
    }

    public void run() {
        //반복적으로 수행할 작업을 한다.
        while (true) {
            handler.sendEmptyMessage(0);
            // 쓰레드에 있는 핸들러에게 메세지를 보냄. 메세지 송신.
            // 메시지를 송신하면 핸들러 객체 안에 있는 handleMessage()메서드가 수신하게 되며 이후에 아래 sleep에 설정된 시간마다 송수신을 반복하게 된다.
            try {
                Thread.sleep(30000); //30초씩 한번 씩 알림.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
