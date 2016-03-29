package com.nd.wang.androidtesttool;

import android.util.Log;

/**
 * 清理实验室文件夹 2016/1/5.
 */
public class ScriptsCmd {
    final String TAG = "ScriptCmd";

    public static ScriptsCmd pClearUpFile = null;

    public static ScriptsCmd getInstance() {
        if (pClearUpFile == null) {
            pClearUpFile = new ScriptsCmd();
        }
        return pClearUpFile;
    }

    /**
     * 获取进程列表
     * @param categories 过滤进程的条件
     * @return 获取进程列表
     */
    public String[] getProcess(String categories){

        String[] shellGrep = new String[]{
                "ps | grep " + categories
        };
        Log.i(TAG, "=================================开始==============================");
        for (String str : shellGrep) Log.i(TAG, "getProcess===" + str);
        ShellUtils.CommandResult result = ShellUtils.execCommand(shellGrep, true);
        Log.i(TAG, "=================================结果==============================");
        Log.i(TAG, "result.result===" + result.result);
        Log.i(TAG, "result.successMsg===" + result.successMsg + "////");
        Log.i(TAG, "result.errorMsg===" + result.errorMsg);

        if ("".equals(result.successMsg)) return null;
        Log.i(TAG, "=================================过滤不要的值==============================");
        String[] processArray = result.successMsg.replace("root", "").split(categories);
        String[] processIdArray = new String[processArray.length];
        for (int i=0;i<processArray.length;i++){
            String str = processArray[i].trim();
            processIdArray[i] = str.substring(0, str.indexOf(" ")).trim();
            Log.i(TAG, processIdArray[i]);
        }

        return processIdArray;

    }

    /**
     * 杀掉进程
     * @param processIdArray 进程id集合
     */
    public void killProcess(String[] processIdArray){

        if (processIdArray==null)return;
        String processArray[] = new String[processIdArray.length];
        for (int i=0; i<processArray.length; i++){
            processArray[i] = "kill -9 " + processIdArray[i];
        }

        Log.i(TAG, "=================================开始==============================");
        for (String str : processArray) Log.i(TAG, "killprocess===" + str);
        ShellUtils.CommandResult result = ShellUtils.execCommand(processArray, true);
        Log.i(TAG, "=================================结果==============================");
        Log.i(TAG, "result.result===" + result.result);
        Log.i(TAG, "result.successMsg===" + result.successMsg);
        Log.i(TAG, "result.errorMsg===" + result.errorMsg);
    }
}
