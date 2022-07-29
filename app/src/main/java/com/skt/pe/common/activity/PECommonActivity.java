package com.skt.pe.common.activity;

import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.View;
import android.widget.ListAdapter;

import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.primitive.Primitive;
import com.skt.pe.common.service.Controller;
import com.skt.pe.common.service.XMLData;

public abstract class PECommonActivity extends SKTActivity {
    private Primitive primitive = null;
    private final static int WHAT_BACKGROUND_COMM = 99332;

    protected void onReceive(Primitive primitive, SKTException e) {
    }

    public void executePrimitive(Primitive primitive) {
        executePrimitive(primitive, true);
    }

    protected void executePrimitive(Primitive primitive, boolean isPendding) {
        this.primitive = primitive;
        requestPrimitive(primitive, isPendding);
    }

    private void requestPrimitive(Primitive primitive, boolean isPendding) {
        this.primitive = primitive;
        new Action(primitive.getPrimitiveName(), isPendding).execute();
    }

    @Override
    protected int onActionPre(String primitive) {
        return this.primitive.getDialogMessage();
    }

    @Override
    protected XMLData onAction(String primitive, String... args) throws SKTException {
        Controller controller = new Controller(this);
        XMLData result = controller.request(this.primitive.getParameters(), false, this.primitive.getUrlPath());
        this.primitive.convertXML(result);
        return result;
    }

    protected void onActionPost(String primitive, XMLData result, SKTException e) throws SKTException {

        onReceive(this.primitive, e);
    }

    public void selectSingleChoiceDialog(String title, int stringArrayId, int select, OnClickListener selectListener
            , OnClickListener positiveListener, OnClickListener negativelistener) {
        new AlertDialog.Builder(PECommonActivity.this)
                .setTitle(title)
                .setSingleChoiceItems(stringArrayId, select, (OnClickListener) selectListener)
                .setPositiveButton("확인", (OnClickListener) positiveListener)
                .setNegativeButton("취소", (OnClickListener) negativelistener)
                .show();
    }

    public void selectSingleChoiceDialog(String title, String[] stringArray, int select, OnClickListener selectListener
            , OnClickListener positiveListener, OnClickListener negativelistener) {
        new AlertDialog.Builder(PECommonActivity.this)
                .setTitle(title)
                .setSingleChoiceItems(stringArray, select, (OnClickListener) selectListener)
                .setPositiveButton("확인", (OnClickListener) positiveListener)
                .setNegativeButton("취소", (OnClickListener) negativelistener)
                .show();
    }

    public void selectMultiChoiceDialog(String title, String[] stringArray, boolean[] select, OnMultiChoiceClickListener selectListener
            , OnClickListener positiveListener, OnClickListener negativelistener) {
        new AlertDialog.Builder(PECommonActivity.this)
                .setTitle(title)
                .setMultiChoiceItems(stringArray, select, (OnMultiChoiceClickListener) selectListener)
                .setPositiveButton("확인", (OnClickListener) positiveListener)
                .setNegativeButton("취소", (OnClickListener) negativelistener)
                .show();
    }

    public void customViewDialog(String title, View view
            , OnClickListener positiveListener, OnClickListener negativelistener) {
        new AlertDialog.Builder(PECommonActivity.this)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("확인", (OnClickListener) positiveListener)
                .setNegativeButton("취소", (OnClickListener) negativelistener)
                .show();
    }

    public void customListViewDialog(String title, ListAdapter adaptor
            , OnClickListener positiveListener) {
        new AlertDialog.Builder(PECommonActivity.this)
                .setTitle(title)
                .setAdapter(adaptor, null)
                .setInverseBackgroundForced(true)
                .setPositiveButton("확인", (OnClickListener) positiveListener)
                .show();
    }

    public void alert(int resId) {
        this.alert("알림", getResources().getString(resId), null);
    }

    public void alert(int resId, OnClickListener listener) {
        this.alert("알림", getResources().getString(resId), listener);
    }

    public void alert(String message) {
        this.alert("알림", message, null);
    }

    public void alert(String message, OnClickListener listener) {
        this.alert("알림", message, listener);
    }

    public void alert(String title, String message, OnClickListener listener) {
        new AlertDialog.Builder(PECommonActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("확인", listener)
                .show();
    }

    public void alertYesNo(String title, String message, OnClickListener yesListener
            , OnClickListener noListener) {
        new AlertDialog.Builder(PECommonActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("확인", yesListener)
                .setNegativeButton("취소", noListener)
                .show();

    }

    public void exitApp() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
