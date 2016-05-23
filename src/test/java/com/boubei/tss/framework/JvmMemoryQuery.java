package com.boubei.tss.framework;
 
public class JvmMemoryQuery {
    
    public static void main(String[] args) throws Exception{
        Runtime lRuntime = Runtime.getRuntime();
        System.out.println("Free Momery:" + lRuntime.freeMemory()/1024/1024 + "M ");
        System.out.println("Max Momery:" + lRuntime.maxMemory()/1024/1024 + "M");
        System.out.println("Total Momery:" + lRuntime.totalMemory()/1024/1024 + "M");
        System.out.println("Available Processors : " + lRuntime.availableProcessors());
    }
}
