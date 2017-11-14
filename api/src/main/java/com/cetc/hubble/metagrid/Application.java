package com.cetc.hubble.metagrid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(Application.class, args);
    }


    public static void test() throws Exception{
        String [] fileName = getFileName("D:\\mySpace\\lib");
        for(String name:fileName)
        {
            System.out.print(":$lib_dir/"+name);
        }

        System.out.println("--------------------------------");
        ArrayList<String> listFileName = new ArrayList<String>();
        getAllFileName("D:\\mySpace\\lib",listFileName);
        for(String name:listFileName)
        {
            System.out.println(name);
        }
    }

    public static void getAllFileName(String path,ArrayList<String> fileName)
    {
        File file = new File(path);
        File [] files = file.listFiles();
        String [] names = file.list();
        if(names != null)
            fileName.addAll(Arrays.asList(names));
        for(File a:files)
        {
            if(a.isDirectory())
            {
                getAllFileName(a.getAbsolutePath(),fileName);
            }
        }
    }

    public static String [] getFileName(String path)
    {
        File file = new File(path);
        String [] fileName = file.list();
        return fileName;
    }




}