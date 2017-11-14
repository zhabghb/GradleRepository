package com.cetc.hubble.metagrid.controller.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShellUtil {

    private static final Logger LOG = Logger.getLogger(ShellUtil.class);

    public static int executeCmd(String cmd){
        Preconditions.checkArgument(cmd != null, "the parameter is null");
        LOG.info("shell cmd is: " + cmd);
        List<String> cmds = Lists.newArrayList();
        cmds.add("sh");
        cmds.add("-c");
        cmds.add(cmd);
        ProcessBuilder pb = new ProcessBuilder(cmds);
        int exitValue = -1;
        try {
            //合并输出流和错误流
            pb.redirectErrorStream(true);
            Process start = pb.start();

            new Thread(new Runnable() {
                @Override
                public void run () {
                    try {
                        BufferedReader outbufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream()));
                        String line = null;

                        while ((line = outbufferedReader.readLine()) != null) {
                            LOG.info(line);
                        }
    //                   BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
    //                    while ((line = bufferedReader.readLine()) != null) {
    //                        LOG.info(line);
    //                    }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            int waitFor = start.waitFor();
            exitValue = start.exitValue();
//            InputStream errorStream = start.getErrorStream();
            LOG.info("finished exitValue: " + exitValue);
            LOG.info("finished waitFor: " + waitFor);


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return exitValue ;
    }

    private static void printEnv(ProcessBuilder pb) {
        Map<String, String> env = pb.environment();
        Iterator<String> it = env.keySet().iterator();
        while (it.hasNext()) {
            String sysatt = (String) it.next();
            LOG.info("System Attribute:" + sysatt + "=" + env.get(sysatt));
        }
    }
}
